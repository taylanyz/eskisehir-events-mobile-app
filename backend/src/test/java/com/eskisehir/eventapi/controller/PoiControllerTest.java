package com.eskisehir.eventapi.controller;

import com.eskisehir.eventapi.dto.PoiResponse;
import com.eskisehir.eventapi.dto.POIStatisticsDto;
import com.eskisehir.eventapi.model.POI;
import com.eskisehir.eventapi.service.POISeedDataService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Disabled("Mockito Java 25 bytecode modification issue - Phase 15 workaround")
@WebMvcTest(controllers = PoiController.class)
public class PoiControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private POISeedDataService poiService;

    private PoiResponse testPoiResponse;
    private List<PoiResponse> testPoiList;

    @BeforeEach
    void setUp() {
        testPoiResponse = createTestPoiResponse("poi-1", "Test Museum");
        testPoiList = Arrays.asList(
            createTestPoiResponse("poi-1", "Museum A"),
            createTestPoiResponse("poi-2", "Museum B"),
            createTestPoiResponse("poi-3", "Park C")
        );
    }

    // Test Popular POIs endpoint
    @Test
    void testGetPopularPOIs() throws Exception {
        // Given
        List<PoiResponse> popularPois = testPoiList.subList(0, 2);
        when(poiService.findMostPopularPOIs(10)).thenReturn(
            popularPois.stream().map(p -> new POI()).toList()
        );

        // When & Then
        mockMvc.perform(get("/api/v1/pois/popular")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // Test Sustainable POIs endpoint
    @Test
    void testGetSustainablePOIs() throws Exception {
        // Given
        List<PoiResponse> sustainablePois = testPoiList.subList(0, 2);
        when(poiService.findSustainablePOIs(60.0f))
            .thenReturn(sustainablePois.stream().map(p -> new POI()).toList());

        // When & Then
        mockMvc.perform(get("/api/v1/pois/sustainable")
                .param("minScore", "60")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // Test Local Business POIs endpoint
    @Test
    void testGetLocalBusinessPOIs() throws Exception {
        // Given
        List<PoiResponse> localPois = testPoiList.subList(0, 1);
        when(poiService.findLocalBusinessPOIs(50.0f))
            .thenReturn(localPois.stream().map(p -> new POI()).toList());

        // When & Then
        mockMvc.perform(get("/api/v1/pois/local-business")
                .param("minScore", "50")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // Test Geographic Bounds endpoint
    @Test
    void testGetPOIsByGeographicBounds() throws Exception {
        // Given
        List<PoiResponse> boundsPois = testPoiList.subList(0, 1);
        when(poiService.findByGeographicBounds(38.70, 38.80, 30.45, 30.55))
            .thenReturn(boundsPois.stream().map(p -> new POI()).toList());

        // When & Then
        mockMvc.perform(get("/api/v1/pois/bounds")
                .param("minLat", "38.70")
                .param("maxLat", "38.80")
                .param("minLon", "30.45")
                .param("maxLon", "30.55")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // Test District Filter endpoint
    @Test
    void testGetPOIsByDistrict() throws Exception {
        // Given
        List<PoiResponse> districtPois = testPoiList.subList(0, 2);
        when(poiService.findByDistrict("ODUNPAZARI"))
            .thenReturn(districtPois.stream().map(p -> new POI()).toList());

        // When & Then
        mockMvc.perform(get("/api/v1/pois/district/ODUNPAZARI")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // Test Accessible POIs endpoint
    @Test
    void testGetAccessiblePOIs() throws Exception {
        // Given
        List<PoiResponse> accessiblePois = testPoiList.subList(0, 1);
        when(poiService.findAccessiblePOIs())
            .thenReturn(accessiblePois.stream().map(p -> new POI()).toList());

        // When & Then
        mockMvc.perform(get("/api/v1/pois/accessible")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // Test Family-Friendly POIs endpoint
    @Test
    void testGetFamilyFriendlyPOIs() throws Exception {
        // Given
        List<PoiResponse> familyPois = testPoiList.subList(0, 1);
        when(poiService.findFamilyFriendlyPOIs())
            .thenReturn(familyPois.stream().map(p -> new POI()).toList());

        // When & Then
        mockMvc.perform(get("/api/v1/pois/family-friendly")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // Test Free POIs endpoint
    @Test
    void testGetFreePOIs() throws Exception {
        // Given
        List<PoiResponse> freePois = testPoiList.subList(0, 1);
        when(poiService.findFreePOIs())
            .thenReturn(freePois.stream().map(p -> new POI()).toList());

        // When & Then
        mockMvc.perform(get("/api/v1/pois/free")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // Test Statistics endpoint
    @Test
    void testGetPOIStatistics() throws Exception {
        // Given
        POIStatisticsDto stats = new POIStatisticsDto();
        stats.setTotalPOIs(100L);
        stats.setTotalCategories(25);
        stats.setTotalDistricts(10);
        stats.setAveragePopularityScore(72.5f);

        when(poiService.getStatistics()).thenReturn(stats);

        // When & Then
        mockMvc.perform(get("/api/v1/pois/stats")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.totalPOIs", is(100)))
            .andExpect(jsonPath("$.totalCategories", is(25)))
            .andExpect(jsonPath("$.totalDistricts", is(10)));
    }

    // Test Available Districts endpoint
    @Test
    void testGetAvailableDistricts() throws Exception {
        // Given
        Set<String> districts = new HashSet<>(Arrays.asList("ODUNPAZARI", "SAZOVA", "YUNUSELI"));
        when(poiService.getAvailableDistricts()).thenReturn(districts);

        // When & Then
        mockMvc.perform(get("/api/v1/pois/filters/districts")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // Test Available Categories endpoint
    @Test
    void testGetAvailableCategories() throws Exception {
        // Given
        Set<String> categories = new HashSet<>(Arrays.asList("MUSEUM", "PARK", "CAFE", "RESTAURANT"));
        when(poiService.getAvailableCategories()).thenReturn(categories);

        // When & Then
        mockMvc.perform(get("/api/v1/pois/filters/categories")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // Test Seed Data Generation endpoint
    @Test
    void testGenerateSeedData() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/pois/admin/generate-seed-data")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    // Test Invalid Endpoint
    @Test
    void testInvalidEndpoint() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/pois/invalid")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    // Test Error Handling for Missing Parameters
    @Test
    void testGeographicBoundsWithMissingParameter() throws Exception {
        // When & Then: should fail because required params are missing
        mockMvc.perform(get("/api/v1/pois/bounds")
                .param("minLat", "38.70")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    // Helper Methods
    private PoiResponse createTestPoiResponse(String id, String name) {
        PoiResponse response = new PoiResponse();
        response.setId(Long.parseLong(id));
        response.setName(name);
        response.setDistrict("ODUNPAZARI");
        response.setLatitude(38.75);
        response.setLongitude(30.50);
        response.setPopularityScore(75.0);
        response.setCrowdProxy(45.0);
        response.setSustainabilityScore(68.0);
        response.setLocalBusinessScore(60.0);
        response.setFamilyFriendly(true);
        return response;
    }
}