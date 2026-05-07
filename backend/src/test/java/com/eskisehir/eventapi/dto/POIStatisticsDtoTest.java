package com.eskisehir.eventapi.dto;

import com.eskisehir.eventapi.model.POI;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class POIStatisticsDtoTest {

    @Test
    void testPOIStatisticsDtoCreation() {
        // Given & When
        POIStatisticsDto dto = new POIStatisticsDto();
        dto.setTotalPOIs(100L);
        dto.setTotalCategories(25);
        dto.setTotalDistricts(10);

        // Then
        assertEquals(100L, dto.getTotalPOIs());
        assertEquals(25, dto.getTotalCategories());
        assertEquals(10, dto.getTotalDistricts());
    }

    @Test
    void testPOIStatisticsDtoScores() {
        // Given
        POIStatisticsDto dto = new POIStatisticsDto();
        dto.setAveragePopularityScore(72.5f);
        dto.setAverageCrowdScore(45.0f);
        dto.setAverageSustainabilityScore(68.0f);
        dto.setAverageLocalBusinessScore(60.5f);

        // When & Then
        assertEquals(72.5f, dto.getAveragePopularityScore());
        assertEquals(45.0f, dto.getAverageCrowdScore());
        assertEquals(68.0f, dto.getAverageSustainabilityScore());
        assertEquals(60.5f, dto.getAverageLocalBusinessScore());
    }

    @Test
    void testPOIStatisticsDtoAccessibilityCounts() {
        // Given
        POIStatisticsDto dto = new POIStatisticsDto();
        dto.setWheelchairAccessibleCount(15);
        dto.setChildFriendlyCount(30);
        dto.setFreeCount(45);

        // When & Then
        assertEquals(15, (int) dto.getWheelchairAccessibleCount());
        assertEquals(30, (int) dto.getChildFriendlyCount());
        assertEquals(45, (int) dto.getFreeCount());
    }

    @Test
    void testPOIStatisticsDtoCategoryDistribution() {
        // Given
        POIStatisticsDto dto = new POIStatisticsDto();
        Map<String, Long> categoryDist = new HashMap<>();
        categoryDist.put("MUSEUM", 15L);
        categoryDist.put("PARK", 20L);
        categoryDist.put("CAFE", 25L);
        dto.setCategoryDistribution(categoryDist);

        // When & Then
        assertNotNull(dto.getCategoryDistribution());
        assertEquals(3, dto.getCategoryDistribution().size());
        assertEquals(15L, dto.getCategoryDistribution().get("MUSEUM"));
        assertEquals(20L, dto.getCategoryDistribution().get("PARK"));
    }

    @Test
    void testPOIStatisticsDtoDistrictDistribution() {
        // Given
        POIStatisticsDto dto = new POIStatisticsDto();
        Map<String, Long> districtDist = new HashMap<>();
        districtDist.put("ODUNPAZARI", 20L);
        districtDist.put("SAZOVA", 15L);
        districtDist.put("YUNUSELI", 25L);
        dto.setDistrictDistribution(districtDist);

        // When & Then
        assertNotNull(dto.getDistrictDistribution());
        assertEquals(3, dto.getDistrictDistribution().size());
        assertEquals(20L, dto.getDistrictDistribution().get("ODUNPAZARI"));
    }

    @Test
    void testPOIStatisticsDtoPrimitiveWrapperConversion() {
        // Given
        POIStatisticsDto dto = new POIStatisticsDto();
        
        // When: Test overloaded setters with primitive and wrapper types
        dto.setTotalPOIs(100L);  // Long wrapper
        dto.setTotalCategories(25); // Integer wrapper
        
        // Then
        assertEquals(100L, dto.getTotalPOIs());
        assertEquals(25, dto.getTotalCategories());
    }

    @Test
    void testPOIStatisticsDtoNullHandling() {
        // Given
        POIStatisticsDto dto = new POIStatisticsDto();
        
        // When: leaving fields as null
        // Then: getters should return null or default
        assertNull(dto.getCategoryDistribution());
        assertNull(dto.getDistrictDistribution());
    }
}
