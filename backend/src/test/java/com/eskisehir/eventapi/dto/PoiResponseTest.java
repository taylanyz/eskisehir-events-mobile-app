package com.eskisehir.eventapi.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PoiResponseTest {

    @Test
    void testPoiResponseCreation() {
        // Given & When
        PoiResponse response = new PoiResponse();
        response.setId(1L);
        response.setName("Test Museum");

        // Then
        assertEquals(1L, response.getId());
        assertEquals("Test Museum", response.getName());
    }

    @Test
    void testPoiResponseLocationFields() {
        // Given
        PoiResponse response = new PoiResponse();
        response.setId(1L);
        response.setLatitude(38.75);
        response.setLongitude(30.50);

        // When & Then
        assertEquals(38.75, response.getLatitude());
        assertEquals(30.50, response.getLongitude());
    }

    @Test
    void testPoiResponseScoreFields() {
        // Given
        PoiResponse response = new PoiResponse();
        response.setPopularityScore(75.0);
        response.setCrowdProxy(45.0);
        response.setSustainabilityScore(68.0);
        response.setLocalBusinessScore(60.0);

        // When & Then
        assertEquals(75.0, response.getPopularityScore());
        assertEquals(45.0, response.getCrowdProxy());
        assertEquals(68.0, response.getSustainabilityScore());
        assertEquals(60.0, response.getLocalBusinessScore());
    }

    @Test
    void testPoiResponseCategoryAndDistrict() {
        // Given
        PoiResponse response = new PoiResponse();
        response.setName("Test Place");
        response.setDistrict("ODUNPAZARI");

        // When & Then
        assertEquals("Test Place", response.getName());
        assertEquals("ODUNPAZARI", response.getDistrict());
    }

    @Test
    void testPoiResponseBasicFields() {
        // Given
        PoiResponse response = new PoiResponse();
        response.setId(2L);
        response.setName("Test POI");
        response.setDescription("A test point of interest");
        response.setVenue("Test Venue");

        // When & Then
        assertEquals(2L, response.getId());
        assertEquals("Test POI", response.getName());
        assertEquals("A test point of interest", response.getDescription());
        assertEquals("Test Venue", response.getVenue());
    }

    @Test
    void testPoiResponseBudgetAndPrice() {
        // Given
        PoiResponse response = new PoiResponse();
        response.setPrice(150.0);

        // When & Then
        assertEquals(150.0, response.getPrice());
    }

    @Test
    void testPoiResponseEstimatedVisit() {
        // Given
        PoiResponse response = new PoiResponse();
        response.setEstimatedVisitMinutes(120);

        // When & Then
        assertEquals(120, response.getEstimatedVisitMinutes());
    }

    @Test
    void testPoiResponseFamilyFriendly() {
        // Given
        PoiResponse response = new PoiResponse();
        response.setFamilyFriendly(true);

        // When & Then
        assertTrue(response.getFamilyFriendly());
    }

    @Test
    void testPoiResponseRankingScore() {
        // Given
        PoiResponse response = new PoiResponse();
        response.setRankingScore(88.5);

        // When & Then
        assertEquals(88.5, response.getRankingScore());
    }
}
