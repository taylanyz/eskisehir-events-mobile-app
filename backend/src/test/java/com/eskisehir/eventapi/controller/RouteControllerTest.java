package com.eskisehir.eventapi.controller;

import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.dto.RouteRequest;
import com.eskisehir.eventapi.dto.RouteResponse;
import com.eskisehir.eventapi.repository.PoiRepository;
import com.eskisehir.eventapi.repository.RouteRepository;
import com.eskisehir.eventapi.service.RoutePlanner;
import com.eskisehir.eventapi.service.RouteService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Controller-level tests for route generation endpoints.
 * Uses manual stubs for Java 25 compatibility (avoids Mockito inline).
 */
class RouteControllerTest {

    private MockMvc mockMvc;
    private StubPoiRepository poiRepository;
    private RoutePlanner routePlanner;
    private RouteRepository routeRepository;
    private RouteService routeService;
    private RouteController routeController;
    private ObjectMapper objectMapper = new ObjectMapper();

    void setUp() {
        poiRepository = new StubPoiRepository();
        routePlanner = new RoutePlanner(new com.eskisehir.eventapi.service.GeoService());
        
        // Create stub repositories and services for Phase 5.2
        routeRepository = (RouteRepository) Proxy.newProxyInstance(
                RouteRepository.class.getClassLoader(),
                new Class[]{RouteRepository.class},
                (proxy, method, args) -> null
        );
        
        routeService = new RouteService(
                routeRepository,
                null, // RouteRatingRepository can be null for basic tests
                null  // UserRepository can be null for basic tests
        );
        
        routeController = new RouteController(routePlanner, poiRepository.asRepository(), routeRepository, routeService);
        mockMvc = MockMvcBuilders.standaloneSetup(routeController).build();
    }

    @Test
    void generateRoute_OrdersPoisByProximity() throws Exception {
        setUp();

        // Create test POIs
        Poi poi1 = new Poi();
        poi1.setId(1L);
        poi1.setName("POI 1");
        poi1.setLatitude(39.77);
        poi1.setLongitude(30.53);
        poi1.setPrice(100.0);

        Poi poi2 = new Poi();
        poi2.setId(2L);
        poi2.setName("POI 2");
        poi2.setLatitude(39.78);
        poi2.setLongitude(30.54);
        poi2.setPrice(150.0);

        Poi poi3 = new Poi();
        poi3.setId(3L);
        poi3.setName("POI 3");
        poi3.setLatitude(39.76);
        poi3.setLongitude(30.52);
        poi3.setPrice(75.0);

        poiRepository.poisById = Map.of(1L, poi1, 2L, poi2, 3L, poi3);

        // Create request
        RouteRequest request = new RouteRequest();
        request.setEventIds(Arrays.asList(1L, 2L, 3L));
        request.setStartLatitude(39.7667);
        request.setStartLongitude(30.5256);
        request.setMaxWalkingMinutes(120);
        request.setMaxBudget(500.0);

        String requestBody = objectMapper.writeValueAsString(request);

        // Execute
        mockMvc.perform(post("/api/routes/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderedPois.length()").value(3))
                .andExpect(jsonPath("$.totalDistanceKm").exists())
                .andExpect(jsonPath("$.totalWalkingMinutes").exists())
                .andExpect(jsonPath("$.estimatedCostTRY").value(325.0))
                .andExpect(jsonPath("$.routeStatus").value("FEASIBLE"));
    }

    @Test
    void generateRoute_MarksPartialIfExceedsWalkingTime() throws Exception {
        setUp();

        Poi poi1 = new Poi();
        poi1.setId(1L);
        poi1.setName("POI 1");
        poi1.setLatitude(39.77);
        poi1.setLongitude(30.53);
        poi1.setPrice(100.0);

        Poi poi2 = new Poi();
        poi2.setId(2L);
        poi2.setName("POI 2");
        poi2.setLatitude(40.0);
        poi2.setLongitude(31.0);
        poi2.setPrice(150.0);

        poiRepository.poisById = Map.of(1L, poi1, 2L, poi2);

        RouteRequest request = new RouteRequest();
        request.setEventIds(Arrays.asList(1L, 2L));
        request.setStartLatitude(39.7667);
        request.setStartLongitude(30.5256);
        request.setMaxWalkingMinutes(5);  // Very short constraint
        request.setMaxBudget(500.0);

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/routes/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routeStatus").value("PARTIAL"));
    }

    @Test
    void generateRoute_ReturnsEmptyOnNoValidPois() throws Exception {
        setUp();

        RouteRequest request = new RouteRequest();
        request.setEventIds(Arrays.asList());
        request.setStartLatitude(39.7667);
        request.setStartLongitude(30.5256);

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/routes/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    /**
     * Stub PoiRepository for testing.
     */
    private static final class StubPoiRepository {
        Map<Long, Poi> poisById = new HashMap<>();

        PoiRepository asRepository() {
            return (PoiRepository) Proxy.newProxyInstance(
                    PoiRepository.class.getClassLoader(),
                    new Class[]{PoiRepository.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("findById")) {
                            return Optional.ofNullable(poisById.get((Long) args[0]));
                        }
                        return null;
                    });
        }
    }
}
