package com.eskisehir.eventapi.controller;

import com.eskisehir.eventapi.dto.TurnByTurnNavigationResponse;
import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.repository.PoiRepository;
import com.eskisehir.eventapi.repository.RouteRepository;
import com.eskisehir.eventapi.service.RoutePlanner;
import com.eskisehir.eventapi.service.RouteService;
import com.eskisehir.eventapi.service.GeoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Proxy;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test for turn-by-turn navigation directions endpoint (Phase 4.6).
 */
public class RouteControllerTurnByTurnTest {

    private MockMvc mockMvc;
    private RouteController routeController;
    private RoutePlanner routePlanner;
    private StubPoiRepository poiRepository;
    private RouteRepository routeRepository;
    private RouteService routeService;

    @BeforeEach
    public void setUp() {
        GeoService geoService = new GeoService();
        routePlanner = new RoutePlanner(geoService);
        poiRepository = new StubPoiRepository();
        
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

        // Set up test POIs
        Poi poi1 = new Poi();
        poi1.setId(1L);
        poi1.setName("Museum");
        poi1.setLatitude(39.7700);  // Changed to be different from start
        poi1.setLongitude(30.5300);  // Changed to be different from start
        poi1.setPrice(100.0);

        Poi poi2 = new Poi();
        poi2.setId(2L);
        poi2.setName("Park");
        poi2.setLatitude(39.7750);
        poi2.setLongitude(30.5350);
        poi2.setPrice(50.0);

        Poi poi3 = new Poi();
        poi3.setId(3L);
        poi3.setName("Cafe");
        poi3.setLatitude(39.7800);
        poi3.setLongitude(30.5400);
        poi3.setPrice(30.0);

        poiRepository.poisById = Map.of(1L, poi1, 2L, poi2, 3L, poi3);
    }

    @Test
    public void testGetDirections_ReturnsStepByStepInstructions() throws Exception {
        // Given: 3 POI IDs in order
        String eventIds = "1,2,3";

        // When: Request turn-by-turn directions
        mockMvc.perform(get("/api/routes/directions")
                .param("eventIds", eventIds)
                .param("startLatitude", "39.7667")
                .param("startLongitude", "30.5256"))
                // Then: Should return navigation steps with instructions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.steps", hasSize(3)))
                .andExpect(jsonPath("$.steps[0].stepNumber", equalTo(1)))
                .andExpect(jsonPath("$.steps[0].toPoiName", notNullValue()))
                .andExpect(jsonPath("$.steps[0].instruction", containsString("Walk to")))
                .andExpect(jsonPath("$.steps[0].distanceKm", greaterThan(0.0)))
                .andExpect(jsonPath("$.steps[0].durationMinutes", greaterThan(0)))
                .andExpect(jsonPath("$.totalDistanceKm", greaterThan(0.0)))
                .andExpect(jsonPath("$.totalDurationMinutes", greaterThan(0)));
    }

    @Test
    public void testGetDirections_WithoutStartLocation_UsesDefault() throws Exception {
        // When: Request without explicit start location (should use Eskişehir center)
        mockMvc.perform(get("/api/routes/directions")
                .param("eventIds", "1,2"))
                // Then: Should still generate valid directions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.steps", hasSize(2)))
                .andExpect(jsonPath("$.totalDurationMinutes", greaterThan(0)));
    }

    @Test
    public void testGetDirections_NoValidPois_ReturnsBadRequest() throws Exception {
        // When: Request with non-existent POI IDs
        mockMvc.perform(get("/api/routes/directions")
                .param("eventIds", "999,9999"))
                // Then: Should return 400
                .andExpect(status().isBadRequest());
    }

    /**
     * Stub PoiRepository for testing without database.
     */
    static class StubPoiRepository {
        Map<Long, Poi> poisById = new HashMap<>();

        PoiRepository asRepository() {
            return (PoiRepository) java.lang.reflect.Proxy.newProxyInstance(
                    PoiRepository.class.getClassLoader(),
                    new Class<?>[]{PoiRepository.class},
                    (proxy, method, args) -> {
                        if ("findById".equals(method.getName())) {
                            return Optional.ofNullable(poisById.get((Long) args[0]));
                        } else if ("findAll".equals(method.getName())) {
                            return new ArrayList<>(poisById.values());
                        }
                        return null;
                    }
            );
        }
    }
}


