package com.eskisehir.eventapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.eskisehir.eventapi.domain.model.Category;
import com.eskisehir.eventapi.domain.model.MobilityPreference;
import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.dto.RecommendationRequest;
import com.eskisehir.eventapi.repository.BanditEventRepository;
import com.eskisehir.eventapi.repository.WeatherDataRepository;
import com.eskisehir.eventapi.service.RecommendationEngine;
import com.eskisehir.eventapi.service.RecommendationService;
import com.eskisehir.eventapi.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecommendationControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private StubRecommendationEngine recommendationEngine;
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recommendationEngine = new StubRecommendationEngine();
        
        // Create a real WeatherService with no repository (API disabled)
        // Use a simple stub for WeatherDataRepository to avoid database calls
        WeatherDataRepository stubRepository = (WeatherDataRepository) Proxy.newProxyInstance(
                WeatherDataRepository.class.getClassLoader(),
                new Class[]{WeatherDataRepository.class},
                (proxy, method, args) -> null
        );
        weatherService = new WeatherService(stubRepository, new RestTemplate());
        
        mockMvc = MockMvcBuilders.standaloneSetup(
                new RecommendationController(recommendationEngine, weatherService)
        ).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getRecommendations_ReturnsOk() throws Exception {
        RecommendationRequest request = new RecommendationRequest();
        request.setPreferredCategories(List.of(Category.CAFE));
        request.setPreferredTags(List.of("local"));
        request.setLimit(5);
        request.setMobilityPreference(MobilityPreference.WALKING);

        Poi poi = new Poi();
        poi.setId(1L);
        poi.setName("Kafe Eskişehir");
        poi.setCategory(Category.CAFE);
        poi.setVenue("Odunpazarı");
        poi.setLatitude(39.76);
        poi.setLongitude(30.52);

        recommendationEngine.recommendationScores = linkedScores(poi, 0.91);

        mockMvc.perform(post("/api/recommendations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Kafe Eskişehir"))
            .andExpect(jsonPath("$[0].rankingScore").value(0.91));
    }

    @Test
    void getRecommendations_AllowsColdStartRequestWithoutPreferredCategories() throws Exception {
        RecommendationRequest request = new RecommendationRequest();
        request.setLimit(3);

        Poi poi = new Poi();
        poi.setId(7L);
        poi.setName("Trend Müze");
        poi.setCategory(Category.MUSEUM);
        poi.setVenue("Odunpazarı");
        poi.setLatitude(39.77);
        poi.setLongitude(30.53);

        recommendationEngine.recommendationScores = linkedScores(poi, 0.64);

        mockMvc.perform(post("/api/recommendations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7))
            .andExpect(jsonPath("$[0].name").value("Trend Müze"))
            .andExpect(jsonPath("$[0].rankingScore").value(0.64));
    }

    @Test
    void getRecommendationsByQuery_MapsQueryParamsIntoRequest() throws Exception {
        Poi poi = new Poi();
        poi.setId(9L);
        poi.setName("Nehir Parkuru");
        poi.setCategory(Category.RIVERSIDE);
        poi.setVenue("Porsuk");
        poi.setLatitude(39.78);
        poi.setLongitude(30.54);

        recommendationEngine.recommendationScores = linkedScores(poi, 0.88);

        mockMvc.perform(get("/api/recommendations")
                .param("count", "4")
                .param("latitude", "39.78")
                .param("longitude", "30.54")
                .param("categoryFilter", "RIVERSIDE")
                .param("userId", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(9))
                .andExpect(jsonPath("$[0].name").value("Nehir Parkuru"))
                .andExpect(jsonPath("$[0].rankingScore").value(0.88));

        org.junit.jupiter.api.Assertions.assertEquals(4, recommendationEngine.lastRequest.getLimit());
        org.junit.jupiter.api.Assertions.assertEquals(39.78, recommendationEngine.lastRequest.getLatitude());
        org.junit.jupiter.api.Assertions.assertEquals(30.54, recommendationEngine.lastRequest.getLongitude());
        org.junit.jupiter.api.Assertions.assertEquals(15L, recommendationEngine.lastRequest.getUserId());
        org.junit.jupiter.api.Assertions.assertEquals(List.of(Category.RIVERSIDE), recommendationEngine.lastRequest.getPreferredCategories());
    }

    @Test
    void getTrending_ReturnsOk() throws Exception {
        Poi poiA = new Poi();
        poiA.setId(1L);
        poiA.setName("Alan A");
        poiA.setCategory(Category.PARK);
        poiA.setPopularityScore(0.8);

        recommendationEngine.trending = List.of(poiA);

        mockMvc.perform(get("/api/recommendations/trending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Alan A"));
    }

    private static LinkedHashMap<Poi, Double> linkedScores(Object... values) {
        LinkedHashMap<Poi, Double> result = new LinkedHashMap<>();
        for (int index = 0; index < values.length; index += 2) {
            result.put((Poi) values[index], (Double) values[index + 1]);
        }
        return result;
    }

    private static final class StubRecommendationEngine extends RecommendationEngine {
        private Map<Poi, Double> recommendationScores = Collections.emptyMap();
        private List<Poi> trending = Collections.emptyList();
        private RecommendationRequest lastRequest;

        private StubRecommendationEngine() {
            super(
                    new RecommendationService(null) {
                        @Override
                        public List<Poi> getAllActivePois() {
                            return Collections.emptyList();
                        }
                    },
                    new com.eskisehir.eventapi.algorithm.RecommendationCandidateGenerator() {
                        @Override
                        public List<Poi> generateCandidates(RecommendationRequest request) {
                            return Collections.emptyList();
                        }
                    },
                    new com.eskisehir.eventapi.algorithm.RecommendationRanker() {
                        @Override
                        public Map<Poi, Double> rankCandidates(RecommendationRequest request, List<Poi> candidates) {
                            return Collections.emptyMap();
                        }
                    },
                    new com.eskisehir.eventapi.algorithm.RecommendationStrategy() {
                        @Override
                        public Map<Poi, Double> scorePois(RecommendationRequest request, List<Poi> candidates) {
                            return Collections.emptyMap();
                        }
                    },
                    new com.eskisehir.eventapi.algorithm.ColdStartStrategy() {
                        @Override
                        public java.util.Map<Poi, Double> scorePois(RecommendationRequest request, List<Poi> candidates) {
                            return Collections.emptyMap();
                        }
                    },
                    emptyBanditEventRepository());
        }

        @Override
        public List<Poi> getRecommendations(RecommendationRequest request) {
            this.lastRequest = request;
            return recommendationScores.keySet().stream().toList();
        }

        @Override
        public Map<Poi, Double> getRecommendationScores(RecommendationRequest request) {
            this.lastRequest = request;
            return recommendationScores;
        }

        @Override
        public List<Poi> getTrending(int limit) {
            return trending;
        }

        private static BanditEventRepository emptyBanditEventRepository() {
            return (BanditEventRepository) Proxy.newProxyInstance(
                    BanditEventRepository.class.getClassLoader(),
                    new Class[]{BanditEventRepository.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("findByUserId") || method.getName().equals("findByPoiId")) {
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
