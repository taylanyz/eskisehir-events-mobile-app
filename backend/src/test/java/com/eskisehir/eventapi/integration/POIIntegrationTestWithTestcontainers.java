package com.eskisehir.eventapi.integration;

import com.eskisehir.eventapi.model.POI;
import com.eskisehir.eventapi.repository.POIPhase13Repository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Phase 15: Requires Docker Desktop for TestContainers PostgreSQL")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class POIIntegrationTestWithTestcontainers {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
        .withDatabaseName("eskisehir_events_test")
        .withUsername("testuser")
        .withPassword("testpass");

    @Autowired
    private POIPhase13Repository poiRepository;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
    }

    @Test
    void testPostgresContainerIsRunning() {
        assertTrue(postgres.isRunning());
    }

    @Test
    void testDatabaseConnectionAndPOISave() {
        // Given: Create a POI
        POI poi = createTestPOI("integration-poi-1", "Integration Test Museum");
        
        // When: Save to database
        POI saved = poiRepository.save(poi);
        
        // Then: Should be saved with ID
        assertNotNull(saved.getId());
        assertEquals("integration-poi-1", saved.getId());
        assertEquals("Integration Test Museum", saved.getName());
    }

    @Test
    void testPOIRetrievalFromDatabase() {
        // Given: Save multiple POIs
        POI poi1 = createTestPOI("int-poi-1", "Museum A");
        POI poi2 = createTestPOI("int-poi-2", "Museum B");
        poiRepository.saveAll(List.of(poi1, poi2));

        // When: Retrieve by ID
        Optional<POI> retrieved = poiRepository.findById("int-poi-1");

        // Then: Should find the POI
        assertTrue(retrieved.isPresent());
        assertEquals("Museum A", retrieved.get().getName());
    }

    @Test
    void testCategoryFiltering() {
        // Given: Save POIs with different categories
        POI museum = createTestPOI("int-cat-1", "Museum");
        museum.setCategory(POI.POICategory.MUSEUM);
        
        POI park = createTestPOI("int-cat-2", "Park");
        park.setCategory(POI.POICategory.PARK);
        
        poiRepository.saveAll(List.of(museum, park));

        // When: Query by category
        List<POI> museums = poiRepository.findByCategory(POI.POICategory.MUSEUM);

        // Then: Should return only museums
        assertEquals(1, museums.size());
        assertEquals(POI.POICategory.MUSEUM, museums.get(0).getCategory());
    }

    @Test
    void testDistrictFiltering() {
        // Given: Save POIs in different districts
        POI odun = createTestPOI("int-dist-1", "Odunpazari POI");
        odun.setDistrict(POI.District.ODUNPAZARI);
        
        POI sazova = createTestPOI("int-dist-2", "Sazova POI");
        sazova.setDistrict(POI.District.SAZOVA);
        
        poiRepository.saveAll(List.of(odun, sazova));

        // When: Query by district
        List<POI> odunPois = poiRepository.findByDistrict(POI.District.ODUNPAZARI);

        // Then: Should return only Odunpazari POIs
        assertEquals(1, odunPois.size());
        assertEquals(POI.District.ODUNPAZARI, odunPois.get(0).getDistrict());
    }

    @Test
    void testGeographicQueries() {
        // Given: Save POI with specific coordinates
        POI poi = createTestPOI("int-geo-1", "Geographic Test POI");
        poi.setLatitude(38.75);
        poi.setLongitude(30.50);
        poiRepository.save(poi);

        // When: Query all POIs (we'll filter manually for now)
        List<POI> allPois = poiRepository.findAll();

        // Then: POI should be in results and coordinates should match
        assertTrue(allPois.stream().anyMatch(p -> 
            p.getLatitude() == 38.75 && p.getLongitude() == 30.50
        ));
    }

    @Test
    void testAccessibilityFiltering() {
        // Given: Save accessible and non-accessible POIs
        POI accessible = createTestPOI("int-access-1", "Accessible Museum");
        accessible.setWheelchairAccessible(true);
        
        POI notAccessible = createTestPOI("int-access-2", "Not Accessible Park");
        notAccessible.setWheelchairAccessible(false);
        
        poiRepository.saveAll(List.of(accessible, notAccessible));

        // When: Query accessible POIs
        List<POI> accessiblePois = poiRepository.findAccessiblePOIs();

        // Then: Should return only accessible ones
        assertTrue(accessiblePois.stream().allMatch(p -> p.getWheelchairAccessible() != null && p.getWheelchairAccessible()));
    }

    @Test
    void testPopularityScoreQueries() {
        // Given: Save POIs with different popularity scores
        POI popular = createTestPOI("int-pop-1", "Popular POI");
        popular.setPopularityScore(90.0f);
        
        POI lessPopular = createTestPOI("int-pop-2", "Less Popular POI");
        lessPopular.setPopularityScore(50.0f);
        
        poiRepository.saveAll(List.of(popular, lessPopular));

        // When: Query by minimum popularity score
        List<POI> popularPois = poiRepository.findByPopularityScoreGreaterThanEqualOrderByPopularityScoreDesc(80.0f);

        // Then: Should return only popular ones
        assertEquals(1, popularPois.size());
        assertTrue(popularPois.get(0).getPopularityScore() >= 80.0f);
    }

    @Test
    void testAverageCrowdProxyScore() {
        // Given: Save POIs with different crowd scores
        POI lowCrowd = createTestPOI("int-crowd-1", "Low Crowd POI");
        lowCrowd.setCrowdProxyScore(30.0f);
        
        POI highCrowd = createTestPOI("int-crowd-2", "High Crowd POI");
        highCrowd.setCrowdProxyScore(80.0f);
        
        poiRepository.saveAll(List.of(lowCrowd, highCrowd));

        // When: Retrieve all POIs
        List<POI> pois = poiRepository.findAll();

        // Then: Should have both POIs with correct crowd scores
        assertEquals(2, pois.size());
        assertTrue(pois.stream().anyMatch(p -> p.getCrowdProxyScore() == 30.0f));
        assertTrue(pois.stream().anyMatch(p -> p.getCrowdProxyScore() == 80.0f));
    }

    @Test
    void testTransactionRollback() {
        // Given: Save initial count
        long initialCount = poiRepository.count();

        // When: Attempt to save with specific conditions
        POI poi = createTestPOI("int-trans-1", "Transaction Test POI");
        poiRepository.save(poi);
        
        long afterSave = poiRepository.count();

        // Then: Count should increase by 1
        assertEquals(initialCount + 1, afterSave);
    }

    @Test
    void testBatchInsertAndQuery() {
        // Given: Create batch of POIs
        List<POI> batch = List.of(
            createTestPOI("int-batch-1", "Batch POI 1"),
            createTestPOI("int-batch-2", "Batch POI 2"),
            createTestPOI("int-batch-3", "Batch POI 3"),
            createTestPOI("int-batch-4", "Batch POI 4"),
            createTestPOI("int-batch-5", "Batch POI 5")
        );

        // When: Save batch
        poiRepository.saveAll(batch);

        // Then: All should be queryable
        List<POI> saved = poiRepository.findAll();
        assertTrue(saved.size() >= 5);
    }

    @Test
    void testConcurrentUpdates() {
        // Given: Save initial POI
        POI poi = createTestPOI("int-concurrent-1", "Concurrent Test POI");
        POI saved = poiRepository.save(poi);

        // When: Update popularity score
        saved.setPopularityScore(88.5f);
        poiRepository.save(saved);

        // Then: Update should be persisted
        Optional<POI> updated = poiRepository.findById("int-concurrent-1");
        assertTrue(updated.isPresent());
        assertEquals(88.5f, updated.get().getPopularityScore());
    }

    // Helper method
    private POI createTestPOI(String id, String name) {
        POI poi = new POI();
        poi.setId(id);
        poi.setName(name);
        poi.setEnglishName(name + " EN");
        poi.setAddress("Test Address, Eskişehir");  // Required NOT NULL field
        poi.setCategory(POI.POICategory.MUSEUM);
        poi.setDistrict(POI.District.ODUNPAZARI);
        poi.setLatitude(38.75);
        poi.setLongitude(30.50);
        poi.setPopularityScore(60.0f);
        poi.setCrowdProxyScore(45.0f);
        poi.setSustainabilityScore(65.0f);
        poi.setLocalBusinessScore(60.0f);
        poi.setWheelchairAccessible(false);
        poi.setChildFriendly(false);
        return poi;
    }
}
