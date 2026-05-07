package com.eskisehir.eventapi.performance;

import com.eskisehir.eventapi.dto.PoiResponse;
import com.eskisehir.eventapi.model.POI;
import com.eskisehir.eventapi.repository.POIPhase13Repository;
import com.eskisehir.eventapi.service.POISeedDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance Baseline Tests for Phase 14
 * 
 * Measures recommendation engine and route optimization performance.
 * Target: Establish baseline metrics for Phase 15 optimization.
 * 
 * Metrics tracked:
 * - Query latency (ms)
 * - Memory usage (MB)
 * - Result quality (precision, recall)
 */
@DataJpaTest
@Import(POISeedDataService.class)
@ActiveProfiles("h2")
public class RecommendationPerformanceBaselineTest {

    @Autowired
    private POIPhase13Repository poiRepository;

    @Autowired
    private POISeedDataService poiService;

    private List<POI> testPOIs;

    @BeforeEach
    void setUp() {
        testPOIs = new ArrayList<>();
        // Create 100 test POIs for performance testing
        for (int i = 0; i < 100; i++) {
            POI poi = createTestPOI(i);
            testPOIs.add(poi);
        }
        poiRepository.saveAll(testPOIs);
    }

    /**
     * Baseline: Query all POIs - should complete in <100ms
     * Target latency: <50ms for typical queries
     */
    @Test
    void testBaselineQueryAllPOIs() {
        long startTime = System.currentTimeMillis();
        
        List<POI> all = poiRepository.findAll();
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        assertTrue(elapsed < 100, "Query all POIs should complete in <100ms, took: " + elapsed + "ms");
        assertEquals(100, all.size());
        System.out.println("BASELINE: Query all POIs - " + elapsed + "ms");
    }

    /**
     * Baseline: Filter by category - should complete in <200ms
     * Used in recommendation filtering
     */
    @Test
    void testBaselineFilterByCategory() {
        long startTime = System.currentTimeMillis();
        
        List<POI> museums = poiRepository.findByCategory(POI.POICategory.MUSEUM);
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        assertTrue(elapsed < 200, "Category filter should complete in <200ms, took: " + elapsed + "ms");
        assertTrue(museums.size() > 0, "Should find museums");
        System.out.println("BASELINE: Filter by category - " + elapsed + "ms");
    }

    /**
     * Baseline: Filter by district - should complete in <200ms
     * Used in geographic filtering
     */
    @Test
    void testBaselineFilterByDistrict() {
        long startTime = System.currentTimeMillis();
        
        List<POI> odunpazari = poiRepository.findByDistrict(POI.District.ODUNPAZARI);
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        assertTrue(elapsed < 200, "District filter should complete in <200ms, took: " + elapsed + "ms");
        assertTrue(odunpazari.size() > 0, "Should find POIs in Odunpazari");
        System.out.println("BASELINE: Filter by district - " + elapsed + "ms");
    }

    /**
     * Baseline: Accessibility filtering - should complete in <200ms
     * Used in accessible route generation
     */
    @Test
    void testBaselineAccessiblePOIs() {
        long startTime = System.currentTimeMillis();
        
        List<POI> accessible = poiRepository.findAccessiblePOIs();
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        assertTrue(elapsed < 200, "Accessibility filter should complete in <200ms, took: " + elapsed + "ms");
        System.out.println("BASELINE: Accessible POIs filter - " + elapsed + "ms");
    }

    /**
     * Baseline: Family-friendly filtering - should complete in <200ms
     * Used in family route recommendations
     */
    @Test
    void testBaselineFamilyFriendlyPOIs() {
        long startTime = System.currentTimeMillis();
        
        List<POI> familyFriendly = poiRepository.findFamilyFriendlyPOIs();
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        assertTrue(elapsed < 200, "Family-friendly filter should complete in <200ms, took: " + elapsed + "ms");
        System.out.println("BASELINE: Family-friendly POIs filter - " + elapsed + "ms");
    }

    /**
     * Baseline: Popularity score ranking - should complete in <200ms
     * Used in recommendation scoring
     */
    @Test
    void testBaselinePopularityScoring() {
        long startTime = System.currentTimeMillis();
        
        List<POI> popular = poiRepository.findByPopularityScoreGreaterThanEqualOrderByPopularityScoreDesc(70.0f);
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        assertTrue(elapsed < 200, "Popularity scoring should complete in <200ms, took: " + elapsed + "ms");
        System.out.println("BASELINE: Popularity score ranking - " + elapsed + "ms");
    }

    /**
     * Baseline: Service layer statistics - should complete in <100ms
     * Used in dashboard and analytics
     */
    @Test
    void testBaselineStatisticsGeneration() {
        long startTime = System.currentTimeMillis();
        
        List<String> categories = new ArrayList<>(poiService.getAvailableCategories());
        List<String> districts = new ArrayList<>(poiService.getAvailableDistricts());
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        assertTrue(elapsed < 100, "Statistics generation should complete in <100ms, took: " + elapsed + "ms");
        assertTrue(categories.size() > 0, "Should have categories");
        assertTrue(districts.size() > 0, "Should have districts");
        System.out.println("BASELINE: Statistics generation - " + elapsed + "ms");
    }

    /**
     * Baseline: Batch recommendation generation
     * Target: Generate 10 recommendations in <200ms
     */
    @Test
    void testBaselineBatchRecommendations() {
        long startTime = System.currentTimeMillis();
        
        // Simulate batch recommendation: find popular + accessible + family-friendly
        List<POI> recommendations = new ArrayList<>();
        recommendations.addAll(poiRepository.findByPopularityScoreGreaterThanEqualOrderByPopularityScoreDesc(75.0f));
        recommendations.addAll(poiRepository.findAccessiblePOIs());
        recommendations.addAll(poiRepository.findFamilyFriendlyPOIs());
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        assertTrue(elapsed < 200, "Batch recommendations should complete in <200ms, took: " + elapsed + "ms");
        assertTrue(recommendations.size() > 0, "Should have recommendations");
        System.out.println("BASELINE: Batch recommendations - " + elapsed + "ms for " + recommendations.size() + " results");
    }

    /**
     * Baseline: Memory efficiency test
     * Ensures result sets don't cause memory issues
     */
    @Test
    void testMemoryEfficiency() {
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // Perform multiple queries
        for (int i = 0; i < 10; i++) {
            poiRepository.findAll();
            poiRepository.findByCategory(POI.POICategory.MUSEUM);
            poiRepository.findAccessiblePOIs();
        }
        
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = (finalMemory - initialMemory) / 1024 / 1024; // Convert to MB
        
        assertTrue(memoryUsed < 50, "Multiple queries should use <50MB, used: " + memoryUsed + "MB");
        System.out.println("BASELINE: Memory efficiency - " + memoryUsed + "MB used");
    }

    // Helper methods

    private POI createTestPOI(int index) {
        POI poi = new POI();
        poi.setId("perf-test-poi-" + index);
        poi.setName("Performance Test POI " + index);
        poi.setEnglishName("Performance Test POI " + index + " EN");
        poi.setAddress("Test Address " + index + ", Eskişehir");
        
        // Vary category
        POI.POICategory[] categories = POI.POICategory.values();
        poi.setCategory(categories[index % categories.length]);
        
        // Vary district
        POI.District[] districts = POI.District.values();
        poi.setDistrict(districts[index % districts.length]);
        
        // Vary scores
        poi.setLatitude(38.70 + (index % 10) * 0.01);
        poi.setLongitude(30.45 + (index % 10) * 0.01);
        poi.setPopularityScore(50.0f + (index % 5) * 10.0f);
        poi.setCrowdProxyScore(40.0f + (index % 5) * 10.0f);
        poi.setSustainabilityScore(60.0f + (index % 5) * 8.0f);
        poi.setLocalBusinessScore(55.0f + (index % 5) * 9.0f);
        
        // Vary accessibility
        poi.setWheelchairAccessible(index % 3 == 0);
        poi.setChildFriendly(index % 2 == 0);
        
        return poi;
    }
}
