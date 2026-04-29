package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.domain.model.BanditEvent;
import com.eskisehir.eventapi.domain.model.BanditArmStat;
import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.domain.model.User;
import com.eskisehir.eventapi.domain.model.UserInteraction;
import com.eskisehir.eventapi.dto.InteractionRequest;
import com.eskisehir.eventapi.exception.PoiNotFoundException;
import com.eskisehir.eventapi.exception.UserNotFoundException;
import com.eskisehir.eventapi.repository.BanditEventRepository;
import com.eskisehir.eventapi.repository.BanditStatsRepository;
import com.eskisehir.eventapi.repository.PoiRepository;
import com.eskisehir.eventapi.repository.UserInteractionRepository;
import com.eskisehir.eventapi.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class InteractionService {

    private static final Logger log = LoggerFactory.getLogger(InteractionService.class);
    private final UserRepository userRepository;
    private final PoiRepository poiRepository;
    private final UserInteractionRepository userInteractionRepository;
    private final BanditEventRepository banditEventRepository;
    private final BanditStatsRepository banditStatsRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public InteractionService(
            UserRepository userRepository,
            PoiRepository poiRepository,
            UserInteractionRepository userInteractionRepository,
            BanditEventRepository banditEventRepository,
            BanditStatsRepository banditStatsRepository) {
        this.userRepository = userRepository;
        this.poiRepository = poiRepository;
        this.userInteractionRepository = userInteractionRepository;
        this.banditEventRepository = banditEventRepository;
        this.banditStatsRepository = banditStatsRepository;
    }

    @Transactional
    public UserInteraction logInteraction(InteractionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + request.getUserId()));

        Poi poi = poiRepository.findById(request.getPoiId())
                .orElseThrow(() -> new PoiNotFoundException(request.getPoiId()));

        UserInteraction interaction = new UserInteraction();
        interaction.setUser(user);
        interaction.setPoi(poi);
        interaction.setInteractionType(request.getInteractionType());
        interaction.setContextWeather(request.getWeather());
        interaction.setContextTimeOfDay(request.getTimeOfDay());
        interaction.setContextDayOfWeek(request.getDayOfWeek());

        UserInteraction savedInteraction = userInteractionRepository.save(interaction);
        log.debug("Saved user interaction {} for user {} and poi {}",
                savedInteraction.getId(), user.getId(), poi.getId());

        recordBanditEvent(user, poi, request);
        return savedInteraction;
    }

    @Transactional
    public UserInteraction logView(Long userId, Long poiId) {
        return logInteraction(buildInteractionRequest(userId, poiId, com.eskisehir.eventapi.domain.model.InteractionType.VIEW, null));
    }

    @Transactional
    public void logBookmark(Long userId, Long poiId) {
        logInteraction(buildInteractionRequest(userId, poiId, com.eskisehir.eventapi.domain.model.InteractionType.SAVE, null));
    }

    @Transactional
    public void logShare(Long userId, Long poiId) {
        logInteraction(buildInteractionRequest(userId, poiId, com.eskisehir.eventapi.domain.model.InteractionType.SHARE, null));
    }

    @Transactional
    public void logNegativeFeedback(Long userId, Long poiId, String reason) {
        logInteraction(buildInteractionRequest(userId, poiId, com.eskisehir.eventapi.domain.model.InteractionType.DISLIKE, reason));
    }

    private InteractionRequest buildInteractionRequest(Long userId, Long poiId, com.eskisehir.eventapi.domain.model.InteractionType interactionType, String comment) {
        InteractionRequest request = new InteractionRequest();
        request.setUserId(userId);
        request.setPoiId(poiId);
        request.setInteractionType(interactionType);
        request.setComment(comment);
        return request;
    }

    private void recordBanditEvent(User user, Poi poi, InteractionRequest request) {
        BanditEvent event = new BanditEvent();
        event.setUser(user);
        event.setPoi(poi);
        event.setReward(request.getInteractionType().getRewardValue());
        event.setContextVectorJson(buildContextJson(request));
        banditEventRepository.save(event);
        updateBanditStats(user, poi, event.getReward());
        log.debug("Recorded bandit event for user {} poi {} reward {}",
                user.getId(), poi.getId(), event.getReward());
    }

    private void updateBanditStats(User user, Poi poi, Double reward) {
        BanditArmStat stats = banditStatsRepository.findByUserIdAndPoiId(user.getId(), poi.getId())
                .orElseGet(() -> {
                    BanditArmStat newStats = new BanditArmStat();
                    newStats.setUser(user);
                    newStats.setPoi(poi);
                    return newStats;
                });

        stats.setPlays(stats.getPlays() + 1);
        if (reward != null && reward >= 0.75) {
            stats.setWins(stats.getWins() + 1);
            stats.setAlpha(stats.getAlpha() + 1.0);
        } else {
            stats.setBeta(stats.getBeta() + 1.0);
        }
        stats.setUpdatedAt(java.time.LocalDateTime.now());
        banditStatsRepository.save(stats);
    }

    private String buildContextJson(InteractionRequest request) {
        Map<String, String> context = new HashMap<>();

        if (request.getWeather() != null) {
            context.put("weather", request.getWeather());
        }
        if (request.getTimeOfDay() != null) {
            context.put("timeOfDay", request.getTimeOfDay());
        }
        if (request.getDayOfWeek() != null) {
            context.put("dayOfWeek", request.getDayOfWeek());
        }
        if (request.getInteractionType() != null) {
            context.put("interactionType", request.getInteractionType().name());
        }
        if (request.getComment() != null) {
            context.put("comment", request.getComment());
        }

        try {
            return objectMapper.writeValueAsString(context);
        } catch (JsonProcessingException ex) {
            log.warn("Unable to serialize interaction context, storing empty JSON", ex);
            return "{}";
        }
    }
}
