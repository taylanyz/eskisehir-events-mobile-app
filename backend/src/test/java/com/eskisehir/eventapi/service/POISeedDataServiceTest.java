package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.dto.POIStatisticsDto;
import com.eskisehir.eventapi.model.POI;
import com.eskisehir.eventapi.repository.POIPhase13Repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class POISeedDataServiceTest {

    @Mock
    private POIPhase13Repository poiRepository;

    @InjectMocks
    private POISeedDataService poiService;

    private POI testPOI;

    @BeforeEach
    void setUp() {
        testPOI = createTestPOI("poi-1", POI.POICategory.MUSEUM, POI.District.ODUNPAZARI);
    }

    // CRUD Operations Tests
    @Test
    void testGetPOIById_Found() {
        // Given
        when(poiRepository.findById("poi-1")).thenReturn(Optional.of(testPOI));

        // When
        Optional<POI> result = poiService.getPOIById("poi-1");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test POI: poi-1", result.get().getName());
        verify(poiRepository, times(1)).findById("poi-1");
    }

    @Test
    void testGetPOIById_NotFound() {
        // Given
        when(poiRepository.findById("non-existent")).thenReturn(Optional.empty());

        // When
        Optional<POI> result = poiService.getPOIById("non-existent");

        // Then
        assertFalse(result.isPresent());
        verify(poiRepository, times(1)).findById("non-existent");
    }

    // Category Filtering Tests
    @Test
    void testFindByCategory() {
        // Given
        List<POI> museums = Arrays.asList(
            createTestPOI("poi-1", POI.POICategory.MUSEUM, POI.District.ODUNPAZARI),
            createTestPOI("poi-2", POI.POICategory.MUSEUM, POI.District.SAZOVA)
        );
        when(poiRepository.findByCategory(POI.POICategory.MUSEUM)).thenReturn(museums);

        // When
        List<POI> result = poiService.findByCategory("MUSEUM");

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getCategory() == POI.POICategory.MUSEUM));
        verify(poiRepository, times(1)).findByCategory(POI.POICategory.MUSEUM);
    }

    @Test
    void testFindByCategory_Empty() {
        // Given - Use a valid category that won't have test data
        when(poiRepository.findByCategory(POI.POICategory.CINEMA)).thenReturn(List.of());

        // When
        List<POI> result = poiService.findByCategory("CINEMA");

        // Then
        assertTrue(result.isEmpty());
    }

    // District Filtering Tests
    @Test
    void testFindByDistrict() {
        // Given
        List<POI> odunPois = Arrays.asList(
            createTestPOI("poi-1", POI.POICategory.MUSEUM, POI.District.ODUNPAZARI),
            createTestPOI("poi-2", POI.POICategory.PARK, POI.District.ODUNPAZARI)
        );
        when(poiRepository.findByDistrict(POI.District.ODUNPAZARI)).thenReturn(odunPois);

        // When
        List<POI> result = poiService.findByDistrict("ODUNPAZARI");

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getDistrict() == POI.District.ODUNPAZARI));
        verify(poiRepository, times(1)).findByDistrict(POI.District.ODUNPAZARI);
    }

    // Score-based Filtering Tests
    @Test
    void testFindMostPopularPOIs() {
        // Given
        POI popular1 = createTestPOI("poi-1", POI.POICategory.MUSEUM, POI.District.ODUNPAZARI);
        popular1.setPopularityScore(95.0f);
        
        POI popular2 = createTestPOI("poi-2", POI.POICategory.PARK, POI.District.SAZOVA);
        popular2.setPopularityScore(85.0f);
        
        POI unpopular = createTestPOI("poi-3", POI.POICategory.CAFE, POI.District.TEPEBASΙ);
        unpopular.setPopularityScore(30.0f);

        when(poiRepository.findAll()).thenReturn(Arrays.asList(popular1, popular2, unpopular));

        // When
        List<POI> result = poiService.findMostPopularPOIs(2);

        // Then
        assertEquals(2, result.size());
        assertEquals(95.0f, result.get(0).getPopularityScore());
        assertEquals(85.0f, result.get(1).getPopularityScore());
        verify(poiRepository, atLeastOnce()).findAll();
    }

    @Test
    void testFindSustainablePOIs() {
        // Given
        POI sustainable1 = createTestPOI("poi-1", POI.POICategory.PARK, POI.District.ODUNPAZARI);
        sustainable1.setSustainabilityScore(80.0f);
        
        POI sustainable2 = createTestPOI("poi-2", POI.POICategory.PARK, POI.District.SAZOVA);
        sustainable2.setSustainabilityScore(75.0f);

        when(poiRepository.findAll()).thenReturn(Arrays.asList(sustainable1, sustainable2));

        // When
        List<POI> result = poiService.findSustainablePOIs(70.0f);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getSustainabilityScore() >= 70.0f));
    }

    @Test
    void testFindLocalBusinessPOIs() {
        // Given
        POI localBiz1 = createTestPOI("poi-1", POI.POICategory.RESTAURANT, POI.District.ODUNPAZARI);
        localBiz1.setLocalBusinessScore(85.0f);
        
        POI localBiz2 = createTestPOI("poi-2", POI.POICategory.CAFE, POI.District.SAZOVA);
        localBiz2.setLocalBusinessScore(78.0f);

        when(poiRepository.findAll()).thenReturn(Arrays.asList(localBiz1, localBiz2));

        // When
        List<POI> result = poiService.findLocalBusinessPOIs(75.0f);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getLocalBusinessScore() >= 75.0f));
    }

    // Accessibility Tests
    @Test
    void testFindAccessiblePOIs() {
        // Given
        List<POI> accessible = Arrays.asList(
            createAccessiblePOI("poi-1"),
            createAccessiblePOI("poi-2")
        );
        when(poiRepository.findAccessiblePOIs()).thenReturn(accessible);

        // When
        List<POI> result = poiService.findAccessiblePOIs();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getWheelchairAccessible() != null && p.getWheelchairAccessible()));
        verify(poiRepository, times(1)).findAccessiblePOIs();
    }

    @Test
    void testFindFamilyFriendlyPOIs() {
        // Given
        List<POI> familyFriendly = Arrays.asList(
            createFamilyFriendlyPOI("poi-1"),
            createFamilyFriendlyPOI("poi-2")
        );
        when(poiRepository.findFamilyFriendlyPOIs()).thenReturn(familyFriendly);

        // When
        List<POI> result = poiService.findFamilyFriendlyPOIs();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getChildFriendly() != null && p.getChildFriendly()));
        verify(poiRepository, times(1)).findFamilyFriendlyPOIs();
    }

    @Test
    void testFindFreePOIs() {
        // Given
        List<POI> freePois = Arrays.asList(
            createFreePOI("poi-1"),
            createFreePOI("poi-2")
        );
        when(poiRepository.findFreePOIs()).thenReturn(freePois);

        // When
        List<POI> result = poiService.findFreePOIs();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getPriceLevel() == POI.PriceLevel.FREE));
        verify(poiRepository, times(1)).findFreePOIs();
    }

    // Geographic Filtering Tests
    @Test
    void testFindByGeographicBounds() {
        // Given
        POI inBounds = createTestPOI("poi-1", POI.POICategory.MUSEUM, POI.District.ODUNPAZARI);
        inBounds.setLatitude(38.75);
        inBounds.setLongitude(30.50);

        when(poiRepository.findAll()).thenReturn(Arrays.asList(inBounds));

        // When
        List<POI> result = poiService.findByGeographicBounds(38.70, 38.80, 30.45, 30.55);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).getLatitude() >= 38.70);
        assertTrue(result.get(0).getLatitude() <= 38.80);
    }

    // Statistics Tests
    @Test
    void testGetStatistics() {
        // Given
        List<POI> pois = Arrays.asList(
            createTestPOI("poi-1", POI.POICategory.MUSEUM, POI.District.ODUNPAZARI),
            createTestPOI("poi-2", POI.POICategory.PARK, POI.District.SAZOVA),
            createTestPOI("poi-3", POI.POICategory.MUSEUM, POI.District.ODUNPAZARI)
        );
        when(poiRepository.findAll()).thenReturn(pois);

        // When
        POIStatisticsDto stats = poiService.getStatistics();

        // Then
        assertNotNull(stats);
        assertEquals(3L, stats.getTotalPOIs());
        assertTrue(stats.getTotalCategories() > 0);
        assertTrue(stats.getTotalDistricts() > 0);
        verify(poiRepository, atLeastOnce()).findAll();
    }

    @Test
    void testGetAvailableDistricts() {
        // Given
        List<POI> pois = Arrays.asList(
            createTestPOI("poi-1", POI.POICategory.MUSEUM, POI.District.ODUNPAZARI),
            createTestPOI("poi-2", POI.POICategory.PARK, POI.District.SAZOVA)
        );
        when(poiRepository.findAll()).thenReturn(pois);

        // When
        Set<String> districts = poiService.getAvailableDistricts();

        // Then
        assertTrue(districts.size() >= 2);
        assertTrue(districts.contains("ODUNPAZARI") || districts.contains(POI.District.ODUNPAZARI.toString()));
    }

    @Test
    void testGetAvailableCategories() {
        // Given
        List<POI> pois = Arrays.asList(
            createTestPOI("poi-1", POI.POICategory.MUSEUM, POI.District.ODUNPAZARI),
            createTestPOI("poi-2", POI.POICategory.PARK, POI.District.SAZOVA)
        );
        when(poiRepository.findAll()).thenReturn(pois);

        // When
        Set<String> categories = poiService.getAvailableCategories();

        // Then
        assertTrue(categories.size() >= 2);
        assertTrue(categories.contains("MUSEUM") || categories.contains(POI.POICategory.MUSEUM.toString()));
    }

    @Test
    void testGetCategoryDistribution() {
        // Given
        List<POI> pois = Arrays.asList(
            createTestPOI("poi-1", POI.POICategory.MUSEUM, POI.District.ODUNPAZARI),
            createTestPOI("poi-2", POI.POICategory.MUSEUM, POI.District.SAZOVA),
            createTestPOI("poi-3", POI.POICategory.PARK, POI.District.ODUNPAZARI)
        );
        when(poiRepository.findAll()).thenReturn(pois);

        // When
        Map<String, Long> distribution = poiService.getCategoryDistribution();

        // Then
        assertNotNull(distribution);
        assertTrue(distribution.size() > 0);
    }

    @Test
    void testGetDistrictDistribution() {
        // Given
        List<POI> pois = Arrays.asList(
            createTestPOI("poi-1", POI.POICategory.MUSEUM, POI.District.ODUNPAZARI),
            createTestPOI("poi-2", POI.POICategory.PARK, POI.District.ODUNPAZARI),
            createTestPOI("poi-3", POI.POICategory.MUSEUM, POI.District.SAZOVA)
        );
        when(poiRepository.findAll()).thenReturn(pois);

        // When: Get statistics which includes district distribution
        POIStatisticsDto stats = poiService.getStatistics();

        // Then: Distribution should be included in statistics
        assertNotNull(stats.getDistrictDistribution());
        assertTrue(stats.getDistrictDistribution().size() > 0);
    }

    // Helper Methods
    private POI createTestPOI(String id, POI.POICategory category, POI.District district) {
        POI poi = new POI();
        poi.setId(id);
        poi.setName("Test POI: " + id);
        poi.setEnglishName("Test POI: " + id);
        poi.setCategory(category);
        poi.setDistrict(district);
        poi.setLatitude(38.75);
        poi.setLongitude(30.50);
        poi.setPopularityScore(60.0f);
        poi.setCrowdProxyScore(50.0f);
        poi.setSustainabilityScore(65.0f);
        poi.setLocalBusinessScore(60.0f);
        poi.setWheelchairAccessible(false);
        poi.setChildFriendly(false);
        return poi;
    }

    private POI createAccessiblePOI(String id) {
        POI poi = createTestPOI(id, POI.POICategory.MUSEUM, POI.District.ODUNPAZARI);
        poi.setWheelchairAccessible(true);
        return poi;
    }

    private POI createFamilyFriendlyPOI(String id) {
        POI poi = createTestPOI(id, POI.POICategory.PARK, POI.District.ODUNPAZARI);
        poi.setChildFriendly(true);
        return poi;
    }

    private POI createFreePOI(String id) {
        POI poi = createTestPOI(id, POI.POICategory.PARK, POI.District.ODUNPAZARI);
        poi.setPriceLevel(POI.PriceLevel.FREE);
        return poi;
    }
}
