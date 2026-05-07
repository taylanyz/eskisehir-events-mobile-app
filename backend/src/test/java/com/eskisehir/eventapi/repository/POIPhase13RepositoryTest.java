package com.eskisehir.eventapi.repository;

import com.eskisehir.eventapi.model.POI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("h2")
public class POIPhase13RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private POIPhase13Repository poiRepository;

    private POI testPOI;

    @BeforeEach
    void setUp() {
        testPOI = new POI();
        testPOI.setId("poi-test-1");
        testPOI.setName("Test POI");
        testPOI.setEnglishName("Test POI");
        testPOI.setCategory(POI.POICategory.MUSEUM);
        testPOI.setDistrict(POI.District.ODUNPAZARI);
        testPOI.setAddress("Test Address, Eskişehir");
        testPOI.setLatitude(38.75);
        testPOI.setLongitude(30.50);
        testPOI.setPopularityScore(75.0f);
        testPOI.setCrowdProxyScore(45.0f);
        testPOI.setSustainabilityScore(68.0f);
        testPOI.setLocalBusinessScore(55.0f);
        testPOI.setWheelchairAccessible(true);
        testPOI.setChildFriendly(true);
    }

    @Test
    void testSaveAndFindById() {
        // Given: POI entity
        // When: save it
        POI saved = poiRepository.save(testPOI);
        entityManager.flush();
        entityManager.clear();

        // Then: should retrieve it by ID
        Optional<POI> retrieved = poiRepository.findById(saved.getId());
        assertTrue(retrieved.isPresent());
        assertEquals("Test POI", retrieved.get().getName());
        assertEquals(POI.POICategory.MUSEUM, retrieved.get().getCategory());
    }

    @Test
    void testFindByCategory() {
        // Given: Multiple POIs with different categories
        POI poi1 = createTestPOI("poi-cat-1", POI.POICategory.MUSEUM);
        POI poi2 = createTestPOI("poi-cat-2", POI.POICategory.MUSEUM);
        POI poi3 = createTestPOI("poi-cat-3", POI.POICategory.PARK);

        poiRepository.saveAll(List.of(poi1, poi2, poi3));
        entityManager.flush();
        entityManager.clear();

        // When: find by category
        List<POI> museums = poiRepository.findByCategory(POI.POICategory.MUSEUM);

        // Then: should return only museums
        assertEquals(2, museums.size());
        assertTrue(museums.stream().allMatch(p -> p.getCategory() == POI.POICategory.MUSEUM));
    }

    @Test
    void testFindByDistrict() {
        // Given: POIs in different districts
        POI odun = createTestPOI("poi-dist-1", POI.District.ODUNPAZARI);
        POI sazova = createTestPOI("poi-dist-2", POI.District.SAZOVA);

        poiRepository.saveAll(List.of(odun, sazova));
        entityManager.flush();
        entityManager.clear();

        // When: find by district
        List<POI> odunPois = poiRepository.findByDistrict(POI.District.ODUNPAZARI);

        // Then: should return only Odunpazari POIs
        assertEquals(1, odunPois.size());
        assertEquals(POI.District.ODUNPAZARI, odunPois.get(0).getDistrict());
    }

    @Test
    void testFindAccessiblePOIs() {
        // Given: Mix of accessible and non-accessible POIs
        POI accessible = createTestPOI("poi-access-1", POI.POICategory.MUSEUM);
        accessible.setWheelchairAccessible(true);

        POI notAccessible = createTestPOI("poi-access-2", POI.POICategory.PARK);
        notAccessible.setWheelchairAccessible(false);

        poiRepository.saveAll(List.of(accessible, notAccessible));
        entityManager.flush();
        entityManager.clear();

        // When: find accessible POIs
        List<POI> accessiblePois = poiRepository.findAccessiblePOIs();

        // Then: should return only accessible POIs
        assertTrue(accessiblePois.stream().allMatch(p -> p.getWheelchairAccessible() != null && p.getWheelchairAccessible()));
    }

    @Test
    void testFindFamilyFriendlyPOIs() {
        // Given: Family-friendly and non-family POIs
        POI familyFriendly = createTestPOI("poi-family-1", POI.POICategory.PARK);
        familyFriendly.setChildFriendly(true);

        POI notFamily = createTestPOI("poi-family-2", POI.POICategory.CAFE);
        notFamily.setChildFriendly(false);

        poiRepository.saveAll(List.of(familyFriendly, notFamily));
        entityManager.flush();
        entityManager.clear();

        // When: find family-friendly POIs
        List<POI> familyPois = poiRepository.findFamilyFriendlyPOIs();

        // Then: should return only family-friendly POIs
        assertTrue(familyPois.stream().allMatch(p -> p.getChildFriendly() != null && p.getChildFriendly()));
    }

    @Test
    void testFindFreePOIs() {
        // Given: Free and paid POIs
        POI free = createTestPOI("poi-free-1", POI.POICategory.PARK);
        free.setPriceLevel(POI.PriceLevel.FREE);

        POI paid = createTestPOI("poi-free-2", POI.POICategory.MUSEUM);
        paid.setPriceLevel(POI.PriceLevel.MODERATE);

        poiRepository.saveAll(List.of(free, paid));
        entityManager.flush();
        entityManager.clear();

        // When: find free POIs
        List<POI> freePois = poiRepository.findFreePOIs();

        // Then: should return only free POIs
        assertTrue(freePois.stream().allMatch(p -> p.getPriceLevel() == POI.PriceLevel.FREE));
    }

    @Test
    void testFindByPopularityScoreGreaterThanEqual() {
        // Given: POIs with different popularity scores
        POI popular = createTestPOI("poi-pop-1", POI.POICategory.MUSEUM);
        popular.setPopularityScore(80.0f);

        POI lessPopular = createTestPOI("poi-pop-2", POI.POICategory.PARK);
        lessPopular.setPopularityScore(40.0f);

        poiRepository.saveAll(List.of(popular, lessPopular));
        entityManager.flush();
        entityManager.clear();

        // When: find POIs with popularity >= 70
        List<POI> popularPois = poiRepository.findByPopularityScoreGreaterThanEqualOrderByPopularityScoreDesc(70.0f);

        // Then: should return only popular POIs
        assertEquals(1, popularPois.size());
        assertTrue(popularPois.get(0).getPopularityScore() >= 70.0f);
    }

    @Test
    void testCountByCategory() {
        // Given: Multiple POIs by category
        poiRepository.saveAll(List.of(
            createTestPOI("poi-cnt-1", POI.POICategory.MUSEUM),
            createTestPOI("poi-cnt-2", POI.POICategory.MUSEUM),
            createTestPOI("poi-cnt-3", POI.POICategory.PARK)
        ));
        entityManager.flush();
        entityManager.clear();

        // When: count by category
        Long museumCount = poiRepository.countByCategory(POI.POICategory.MUSEUM);

        // Then: should return correct count
        assertEquals(2L, museumCount);
    }

    @Test
    void testCountByDistrict() {
        // Given: Multiple POIs by district
        poiRepository.saveAll(List.of(
            createTestPOI("poi-dist-cnt-1", POI.District.ODUNPAZARI),
            createTestPOI("poi-dist-cnt-2", POI.District.ODUNPAZARI),
            createTestPOI("poi-dist-cnt-3", POI.District.SAZOVA)
        ));
        entityManager.flush();
        entityManager.clear();

        // When: count by district
        Long odunCount = poiRepository.countByDistrict(POI.District.ODUNPAZARI);

        // Then: should return correct count
        assertEquals(2L, odunCount);
    }

    @Test
    void testDeleteById() {
        // Given: Saved POI
        POI saved = poiRepository.save(testPOI);
        entityManager.flush();

        // When: delete by ID
        poiRepository.deleteById(saved.getId());
        entityManager.flush();

        // Then: should not exist
        assertTrue(poiRepository.findById(saved.getId()).isEmpty());
    }

    // Helper method to create test POI with category
    private POI createTestPOI(String id, POI.POICategory category) {
        POI poi = new POI();
        poi.setId(id);
        poi.setName("Test POI: " + id);
        poi.setEnglishName("Test POI: " + id);
        poi.setCategory(category);
        poi.setDistrict(POI.District.ODUNPAZARI);
        poi.setAddress("Test Address " + id + ", Eskişehir");
        poi.setLatitude(38.75);
        poi.setLongitude(30.50);
        poi.setPopularityScore(50.0f);
        poi.setCrowdProxyScore(45.0f);
        poi.setSustainabilityScore(60.0f);
        poi.setLocalBusinessScore(55.0f);
        poi.setWheelchairAccessible(false);
        poi.setChildFriendly(false);
        return poi;
    }

    // Helper method to create test POI with district
    private POI createTestPOI(String id, POI.District district) {
        POI poi = createTestPOI(id, POI.POICategory.MUSEUM);
        poi.setDistrict(district);
        return poi;
    }
}
