package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.domain.model.BanditEvent;
import com.eskisehir.eventapi.domain.model.BanditArmStat;
import com.eskisehir.eventapi.domain.model.User;
import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.ml.RewardFunction;
import com.eskisehir.eventapi.ml.ContextVectorBuilder;
import com.eskisehir.eventapi.repository.BanditEventRepository;
import com.eskisehir.eventapi.repository.BanditStatsRepository;
import com.eskisehir.eventapi.repository.UserRepository;
import com.eskisehir.eventapi.repository.PoiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Bandit Learning Service: Records interactions as bandit events for Thompson Sampling.
 *
 * Thompson Sampling is a contextual multi-armed bandit algorithm that:
 * 1. Maintains per-user-per-POI (arm) statistics: alpha (successes), beta (failures)
 * 2. Samples from Beta distribution to guide exploration
 * 3. Updates statistics based on user rewards (positive interactions)
 */
@Service
@Transactional
public class BanditLearningService {

    private static final Logger log = LoggerFactory.getLogger(BanditLearningService.class);

    private final BanditEventRepository banditEventRepository;
    private final BanditStatsRepository banditStatsRepository;
    private final UserRepository userRepository;
    private final PoiRepository poiRepository;
    private final RewardFunction rewardFunction;
    private final ContextVectorBuilder contextVectorBuilder;

    public BanditLearningService(
            BanditEventRepository banditEventRepository,
            BanditStatsRepository banditStatsRepository,
            UserRepository userRepository,
            PoiRepository poiRepository,
            RewardFunction rewardFunction,
            ContextVectorBuilder contextVectorBuilder) {
        this.banditEventRepository = banditEventRepository;
        this.banditStatsRepository = banditStatsRepository;
        this.userRepository = userRepository;
        this.poiRepository = poiRepository;
        this.rewardFunction = rewardFunction;
        this.contextVectorBuilder = contextVectorBuilder;
    }

    /**
     * Record a user interaction as a bandit event for learning.
     *
     * @param userId         User who interacted
     * @param poiId          POI that was recommended/shown
     * @param interactionType Type of interaction (view, click, save, add_to_route, visited, positive_feedback, dislike)
     * @param contextJson    JSON context vector at time of interaction
     */
    public void recordInteraction(Long userId, Long poiId, String interactionType, String contextJson) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            Optional<Poi> poiOpt = poiRepository.findById(poiId);

            if (userOpt.isEmpty() || poiOpt.isEmpty()) {
                log.warn("User {} or POI {} not found for bandit event", userId, poiId);
                return;
            }

            User user = userOpt.get();
            Poi poi = poiOpt.get();

            // Create bandit event
            BanditEvent event = new BanditEvent();
            event.setUser(user);
            event.setPoi(poi);
            event.setContextVectorJson(contextJson);

            // Map interaction to reward
            double reward = rewardFunction.getReward(interactionType);
            event.setReward(reward);

            banditEventRepository.save(event);

            // Update arm statistics (user-POI pair)
            updateArmStatistics(user, poi, reward);

            log.debug("Recorded bandit event: user={}, poi={}, interaction={}, reward={}",
                    userId, poiId, interactionType, reward);

        } catch (Exception e) {
            log.error("Error recording bandit event", e);
        }
    }

    /**
     * Update Thompson Sampling statistics for a user-POI arm.
     * Maintains beta distribution parameters: alpha (successes), beta (failures).
     *
     * Reward >= 0.75 → success (alpha += 1)
     * Reward < 0.75  → failure (beta += 1)
     */
    private void updateArmStatistics(User user, Poi poi, double reward) {
        Optional<BanditArmStat> statOpt = banditStatsRepository.findByUserIdAndPoiId(user.getId(), poi.getId());

        BanditArmStat stat;
        if (statOpt.isPresent()) {
            stat = statOpt.get();
        } else {
            stat = new BanditArmStat();
            stat.setUser(user);
            stat.setPoi(poi);
            stat.setAlpha(1.0);
            stat.setBeta(1.0);
            stat.setPlays(0L);
            stat.setWins(0L);
        }

        // Increment play count
        stat.setPlays(stat.getPlays() + 1);

        // Update beta distribution: reward >= 0.75 is success
        if (reward >= 0.75) {
            stat.setAlpha(stat.getAlpha() + 1);
            stat.setWins(stat.getWins() + 1);
        } else {
            stat.setBeta(stat.getBeta() + 1);
        }

        stat.setUpdatedAt(LocalDateTime.now());
        banditStatsRepository.save(stat);

        log.debug("Updated arm stats: user={}, poi={}, alpha={}, beta={}, plays={}, wins={}",
                user.getId(), poi.getId(), stat.getAlpha(), stat.getBeta(), stat.getPlays(), stat.getWins());
    }

    /**
     * Get current arm statistics for exploration-exploitation trade-off.
     * Returns null if no history exists.
     */
    public BanditArmStat getArmStatistics(Long userId, Long poiId) {
        return banditStatsRepository.findByUserIdAndPoiId(userId, poiId).orElse(null);
    }

    /**
     * Record feedback-derived reward signal for Thompson Sampling update (Phase 11).
     * Called after user submits feedback on a completed route.
     * MVP: Placeholder for feedback integration (full implementation in Phase 12).
     */
    public void recordFeedbackReward(com.eskisehir.eventapi.service.feedback.FeedbackReward feedbackReward) {
        try {
            // Phase 11 MVP: Log feedback reward
            // Full integration: Update Thompson Sampling stats for POIs in route based on feedback score
            log.info("Feedback reward recorded for bandit learning (Phase 11 MVP placeholder)");
            
            // Future implementation:
            // 1. Retrieve route and its POIs
            // 2. Apply feedback.getRewardScore() to each POI
            // 3. Update Thompson Sampling alpha/beta distributions
            // 4. Log themes for quality monitoring
            
        } catch (Exception e) {
            log.error("Error recording feedback reward for bandit learning", e);
        }
    }
}
