package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.algorithm.ColdStartStrategy;
import com.eskisehir.eventapi.algorithm.RecommendationStrategy;
import com.eskisehir.eventapi.domain.model.BanditEvent;
import com.eskisehir.eventapi.domain.model.Category;
import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.dto.RecommendationRequest;
import com.eskisehir.eventapi.repository.BanditEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class RecommendationEngineTest {

    private StubRecommendationService recommendationService;
    private StubRecommendationStrategy recommendationStrategy;
    private StubColdStartStrategy coldStartStrategy;
    private StubBanditEventRepository banditEventRepository;
    private RecommendationEngine recommendationEngine;

    @BeforeEach
    void setUp() {
        recommendationService = new StubRecommendationService();
        recommendationStrategy = new StubRecommendationStrategy();
        coldStartStrategy = new StubColdStartStrategy();
        banditEventRepository = new StubBanditEventRepository();
        recommendationEngine = new RecommendationEngine(
                recommendationService,
                recommendationStrategy,
                coldStartStrategy,
            banditEventRepository.asRepository());
    }

    @Test
    void getRecommendations_ReturnsTopScoredPois() {
        Poi poiA = new Poi();
        poiA.setId(1L);
        poiA.setName("Kafe A");
        poiA.setCategory(Category.CAFE);
        poiA.setLatitude(39.76);
        poiA.setLongitude(30.52);
        poiA.setVenue("Odunpazarı");

        Poi poiB = new Poi();
        poiB.setId(2L);
        poiB.setName("Müze B");
        poiB.setCategory(Category.MUSEUM);
        poiB.setLatitude(39.77);
        poiB.setLongitude(30.51);
        poiB.setVenue("Tepebaşı");

        RecommendationRequest request = new RecommendationRequest();
        request.setPreferredCategories(List.of(Category.CAFE));
        request.setLimit(1);
        request.setUserId(12L);

        recommendationService.activePois = List.of(poiA, poiB);
        recommendationStrategy.scored = linkedScores(poiA, 0.8, poiB, 0.2);
        banditEventRepository.userEvents = List.of(new BanditEvent(), new BanditEvent(), new BanditEvent());

        List<Poi> result = recommendationEngine.getRecommendations(request);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(1, recommendationStrategy.invocationCount);
        assertEquals(0, coldStartStrategy.invocationCount);
    }

    @Test
    void getRecommendationScores_ReturnsOrderedScoresWithLimitApplied() {
        Poi poiA = new Poi();
        poiA.setId(1L);
        Poi poiB = new Poi();
        poiB.setId(2L);

        RecommendationRequest request = new RecommendationRequest();
        request.setLimit(1);
        request.setUserId(12L);

        recommendationService.activePois = List.of(poiA, poiB);
        recommendationStrategy.scored = linkedScores(poiA, 0.85, poiB, 0.33);
        banditEventRepository.userEvents = List.of(new BanditEvent(), new BanditEvent(), new BanditEvent());

        Map<Poi, Double> result = recommendationEngine.getRecommendationScores(request);

        assertEquals(1, result.size());
        assertIterableEquals(List.of(poiA), result.keySet());
        assertEquals(0.85, result.get(poiA));
    }

    @Test
    void getRecommendations_UsesColdStartForAnonymousUser() {
        Poi poiA = new Poi();
        poiA.setId(1L);
        poiA.setName("A");

        RecommendationRequest request = new RecommendationRequest();
        request.setLimit(1);

        recommendationService.activePois = List.of(poiA);
        coldStartStrategy.scored = linkedScores(poiA, 0.7);

        List<Poi> result = recommendationEngine.getRecommendations(request);

        assertEquals(1, result.size());
        assertEquals(1, coldStartStrategy.invocationCount);
        assertEquals(0, recommendationStrategy.invocationCount);
    }

    @Test
    void getRecommendations_UsesColdStartWhenUserHasTooFewEvents() {
        Poi poiA = new Poi();
        poiA.setId(1L);
        poiA.setName("A");

        RecommendationRequest request = new RecommendationRequest();
        request.setLimit(1);
        request.setUserId(99L);

        recommendationService.activePois = List.of(poiA);
        coldStartStrategy.scored = linkedScores(poiA, 0.6);
        banditEventRepository.userEvents = List.of(new BanditEvent(), new BanditEvent());

        List<Poi> result = recommendationEngine.getRecommendations(request);

        assertEquals(1, result.size());
        assertEquals(1, coldStartStrategy.invocationCount);
        assertEquals(0, recommendationStrategy.invocationCount);
    }

    @Test
    void getTrending_ReturnsPoisSortedByPopularity() {
        Poi poiA = new Poi();
        poiA.setId(1L);
        poiA.setName("A");
        poiA.setPopularityScore(0.4);

        Poi poiB = new Poi();
        poiB.setId(2L);
        poiB.setName("B");
        poiB.setPopularityScore(0.9);

        recommendationService.activePois = List.of(poiA, poiB);

        List<Poi> trending = recommendationEngine.getTrending(2);

        assertEquals(2, trending.size());
        assertEquals(2L, trending.get(0).getId());
        assertEquals(1L, trending.get(1).getId());
    }

    private static LinkedHashMap<Poi, Double> linkedScores(Object... values) {
        LinkedHashMap<Poi, Double> result = new LinkedHashMap<>();
        for (int index = 0; index < values.length; index += 2) {
            result.put((Poi) values[index], (Double) values[index + 1]);
        }
        return result;
    }

    private static final class StubRecommendationService extends RecommendationService {
        private List<Poi> activePois = Collections.emptyList();

        private StubRecommendationService() {
            super(null);
        }

        @Override
        public List<Poi> getAllActivePois() {
            return activePois;
        }
    }

    private static final class StubRecommendationStrategy implements RecommendationStrategy {
        private Map<Poi, Double> scored = Collections.emptyMap();
        private int invocationCount;

        @Override
        public Map<Poi, Double> scorePois(RecommendationRequest request, List<Poi> candidates) {
            invocationCount++;
            return scored;
        }
    }

    private static final class StubColdStartStrategy extends ColdStartStrategy {
        private Map<Poi, Double> scored = Collections.emptyMap();
        private int invocationCount;

        @Override
        public Map<Poi, Double> scorePois(RecommendationRequest request, List<Poi> candidates) {
            invocationCount++;
            return scored;
        }
    }

    private static final class StubBanditEventRepository {
        private List<BanditEvent> userEvents = Collections.emptyList();

        private BanditEventRepository asRepository() {
            return (BanditEventRepository) Proxy.newProxyInstance(
                    BanditEventRepository.class.getClassLoader(),
                    new Class[]{BanditEventRepository.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("findByUserId")) {
                            return userEvents;
                        }
                        if (method.getName().equals("findByPoiId")) {
                            return Collections.emptyList();
                        }
                        if (method.getReturnType().equals(boolean.class)) {
                            return false;
                        }
                        if (method.getReturnType().equals(long.class)) {
                            return 0L;
                        }
                        return null;
                    });
        }
    }
}
