package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.domain.model.BanditEvent;
import com.eskisehir.eventapi.domain.model.BanditArmStat;
import com.eskisehir.eventapi.domain.model.InteractionType;
import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.domain.model.User;
import com.eskisehir.eventapi.domain.model.UserInteraction;
import com.eskisehir.eventapi.dto.InteractionRequest;
import com.eskisehir.eventapi.repository.BanditEventRepository;
import com.eskisehir.eventapi.repository.BanditStatsRepository;
import com.eskisehir.eventapi.repository.PoiRepository;
import com.eskisehir.eventapi.repository.UserInteractionRepository;
import com.eskisehir.eventapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InteractionServiceTest {

    private StubUserRepository userRepository;
    private StubPoiRepository poiRepository;
    private StubUserInteractionRepository userInteractionRepository;
    private StubBanditEventRepository banditEventRepository;
    private StubBanditStatsRepository banditStatsRepository;
    private InteractionService interactionService;

    @BeforeEach
    void setUp() {
        userRepository = new StubUserRepository();
        poiRepository = new StubPoiRepository();
        userInteractionRepository = new StubUserInteractionRepository();
        banditEventRepository = new StubBanditEventRepository();
        banditStatsRepository = new StubBanditStatsRepository();
        interactionService = new InteractionService(
                userRepository.asRepository(),
                poiRepository.asRepository(),
                userInteractionRepository.asRepository(),
            banditEventRepository.asRepository(),
            banditStatsRepository.asRepository());
    }

    @Test
    void logInteraction_SavesInteractionAndBanditEvent() {
        User user = new User();
        user.setId(1L);
        Poi poi = new Poi();
        poi.setId(2L);

        userRepository.usersById = Map.of(1L, user);
        poiRepository.poisById = Map.of(2L, poi);

        InteractionRequest request = new InteractionRequest();
        request.setUserId(1L);
        request.setPoiId(2L);
        request.setInteractionType(InteractionType.POSITIVE_FEEDBACK);
        request.setWeather("clear");
        request.setTimeOfDay("MORNING");
        request.setDayOfWeek("SATURDAY");

        UserInteraction savedInteraction = interactionService.logInteraction(request);

        assertEquals(99L, savedInteraction.getId());
        assertEquals(99L, userInteractionRepository.savedInteraction.getId());
        assertEquals(1L, banditEventRepository.savedEvent.getUser().getId());
        assertEquals(2L, banditEventRepository.savedEvent.getPoi().getId());
        assertEquals(InteractionType.POSITIVE_FEEDBACK.getRewardValue(), banditEventRepository.savedEvent.getReward());
        assertEquals(2.0, banditStatsRepository.savedStat.getAlpha());
        assertEquals(1.0, banditStatsRepository.savedStat.getBeta());
        assertEquals(1L, banditStatsRepository.savedStat.getWins());
        assertTrue(banditEventRepository.savedEvent.getContextVectorJson().contains("clear"));
        assertTrue(banditEventRepository.savedEvent.getContextVectorJson().contains("SATURDAY"));
    }

    @Test
    void helperMethods_MapToExpectedInteractionTypes() {
        User user = new User();
        user.setId(3L);
        Poi poi = new Poi();
        poi.setId(4L);

        userRepository.usersById = Map.of(3L, user);
        poiRepository.poisById = Map.of(4L, poi);

        UserInteraction viewInteraction = interactionService.logView(3L, 4L);
        assertEquals(InteractionType.VIEW, viewInteraction.getInteractionType());

        interactionService.logBookmark(3L, 4L);
        assertEquals(InteractionType.SAVE, userInteractionRepository.savedInteraction.getInteractionType());

        interactionService.logShare(3L, 4L);
        assertEquals(InteractionType.SHARE, userInteractionRepository.savedInteraction.getInteractionType());

        interactionService.logNegativeFeedback(3L, 4L, "too crowded");
        assertEquals(InteractionType.DISLIKE, userInteractionRepository.savedInteraction.getInteractionType());
        assertEquals(5.0, banditStatsRepository.savedStat.getBeta());
        assertEquals(4L, banditStatsRepository.savedStat.getPlays());
        assertTrue(banditEventRepository.savedEvent.getContextVectorJson().contains("too crowded"));
    }

    private static final class StubUserRepository {
        private Map<Long, User> usersById = Collections.emptyMap();

        private UserRepository asRepository() {
            return (UserRepository) Proxy.newProxyInstance(
                    UserRepository.class.getClassLoader(),
                    new Class[]{UserRepository.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("findById")) {
                            return Optional.ofNullable(usersById.get((Long) args[0]));
                        }
                        if (method.getReturnType().equals(boolean.class)) {
                            return false;
                        }
                        return null;
                    });
        }
    }

    private static final class StubPoiRepository {
        private Map<Long, Poi> poisById = Collections.emptyMap();

        private PoiRepository asRepository() {
            return (PoiRepository) Proxy.newProxyInstance(
                    PoiRepository.class.getClassLoader(),
                    new Class[]{PoiRepository.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("findById")) {
                            return Optional.ofNullable(poisById.get((Long) args[0]));
                        }
                        if (method.getName().equals("findByIsActiveTrue")) {
                            return Collections.emptyList();
                        }
                        if (method.getReturnType().equals(boolean.class)) {
                            return false;
                        }
                        return null;
                    });
        }
    }

    private static final class StubUserInteractionRepository {
        private UserInteraction savedInteraction;

        private UserInteractionRepository asRepository() {
            return (UserInteractionRepository) Proxy.newProxyInstance(
                    UserInteractionRepository.class.getClassLoader(),
                    new Class[]{UserInteractionRepository.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("save")) {
                            savedInteraction = (UserInteraction) args[0];
                            savedInteraction.setId(99L);
                            return savedInteraction;
                        }
                        if (method.getName().startsWith("findBy")) {
                            return Collections.emptyList();
                        }
                        if (method.getReturnType().equals(long.class)) {
                            return 0L;
                        }
                        return null;
                    });
        }
    }

    private static final class StubBanditEventRepository {
        private BanditEvent savedEvent;

        private BanditEventRepository asRepository() {
            return (BanditEventRepository) Proxy.newProxyInstance(
                    BanditEventRepository.class.getClassLoader(),
                    new Class[]{BanditEventRepository.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("save")) {
                            savedEvent = (BanditEvent) args[0];
                            return savedEvent;
                        }
                        if (method.getName().startsWith("findBy")) {
                            return Collections.emptyList();
                        }
                        return null;
                    });
        }
    }

    private static final class StubBanditStatsRepository {
        private BanditArmStat savedStat;

        private BanditStatsRepository asRepository() {
            return (BanditStatsRepository) Proxy.newProxyInstance(
                    BanditStatsRepository.class.getClassLoader(),
                    new Class[]{BanditStatsRepository.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("findByUserIdAndPoiId")) {
                            return Optional.ofNullable(savedStat);
                        }
                        if (method.getName().equals("findByUserId")) {
                            return savedStat == null ? Collections.emptyList() : List.of(savedStat);
                        }
                        if (method.getName().equals("save")) {
                            savedStat = (BanditArmStat) args[0];
                            return savedStat;
                        }
                        return null;
                    });
        }
    }
}
