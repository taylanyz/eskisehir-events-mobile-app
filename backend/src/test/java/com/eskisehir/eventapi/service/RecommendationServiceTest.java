package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.domain.model.BudgetLevel;
import com.eskisehir.eventapi.domain.model.Category;
import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.dto.RecommendationRequest;
import com.eskisehir.eventapi.dto.RouteRequest;
import com.eskisehir.eventapi.repository.PoiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecommendationServiceTest {

    private StubPoiRepository poiRepository;
    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        poiRepository = new StubPoiRepository();
        recommendationService = new RecommendationService(poiRepository.asRepository());
    }

    @Test
    void getRecommendations_SortsByScoreThenSoonerDate() {
        Poi topCafe = poi(1L, "Top Cafe", Category.CAFE, 39.76, 30.52);
        topCafe.setTags(List.of("local", "dessert"));
        topCafe.setPrice(150.0);
        topCafe.setSustainabilityScore(0.9);
        topCafe.setCrowdProxy(0.2);

        Poi earlyEvent = poi(2L, "Early Event", Category.CAFE, 39.77, 30.53);
        earlyEvent.setDate(LocalDateTime.now().plusDays(1));
        earlyEvent.setTags(List.of("local"));

        Poi lateEvent = poi(3L, "Late Event", Category.CAFE, 39.78, 30.54);
        lateEvent.setDate(LocalDateTime.now().plusDays(3));
        lateEvent.setTags(List.of("local"));

        poiRepository.activePois = List.of(lateEvent, topCafe, earlyEvent);

        RecommendationRequest request = new RecommendationRequest();
        request.setPreferredCategories(List.of(Category.CAFE));
        request.setPreferredTags(List.of("local", "dessert"));
        request.setMaxPrice(200.0);
        request.setLimit(3);

        List<Poi> result = recommendationService.getRecommendations(request);

        assertEquals(List.of(topCafe, earlyEvent, lateEvent), result);
    }

    @Test
    void getRecommendations_FiltersZeroScorePoisAndRespectsLimit() {
        Poi matchingPoi = poi(10L, "Matching", Category.MUSEUM, 39.76, 30.52);
        matchingPoi.setTags(List.of("history", "art"));

        Poi secondaryPoi = poi(11L, "Secondary", Category.MUSEUM, 39.77, 30.53);
        secondaryPoi.setTags(List.of("history"));

        Poi zeroScorePoi = poi(12L, "Zero", Category.PARK, 39.78, 30.54);

        poiRepository.activePois = List.of(zeroScorePoi, secondaryPoi, matchingPoi);

        RecommendationRequest request = new RecommendationRequest();
        request.setPreferredCategories(List.of(Category.MUSEUM));
        request.setPreferredTags(List.of("history", "art"));
        request.setLimit(1);

        List<Poi> result = recommendationService.getRecommendations(request);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
    }

    @Test
    void planRoute_UsesNearestNeighborFromStartLocation() {
        Poi first = poi(20L, "First", Category.PARK, 39.7668, 30.5257);
        Poi second = poi(21L, "Second", Category.PARK, 39.7700, 30.5300);
        Poi third = poi(22L, "Third", Category.PARK, 39.7900, 30.5600);

        poiRepository.poisById = Map.of(
                20L, first,
                21L, second,
                22L, third
        );

        RouteRequest request = new RouteRequest();
        request.setEventIds(List.of(22L, 21L, 20L));
        request.setStartLatitude(39.7667);
        request.setStartLongitude(30.5256);

        List<Poi> route = recommendationService.planRoute(request);

        assertEquals(List.of(first, second, third), route);
    }

    private static Poi poi(Long id, String name, Category category, double latitude, double longitude) {
        Poi poi = new Poi();
        poi.setId(id);
        poi.setName(name);
        poi.setCategory(category);
        poi.setLatitude(latitude);
        poi.setLongitude(longitude);
        poi.setVenue(name + " Venue");
        poi.setBudgetLevel(BudgetLevel.MEDIUM);
        poi.setIsActive(true);
        return poi;
    }

    private static final class StubPoiRepository {
        private List<Poi> activePois = Collections.emptyList();
        private Map<Long, Poi> poisById = new HashMap<>();

        private PoiRepository asRepository() {
            return (PoiRepository) Proxy.newProxyInstance(
                    PoiRepository.class.getClassLoader(),
                    new Class[]{PoiRepository.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("findByIsActiveTrue")) {
                            return activePois;
                        }
                        if (method.getName().equals("findById")) {
                            return Optional.ofNullable(poisById.get((Long) args[0]));
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