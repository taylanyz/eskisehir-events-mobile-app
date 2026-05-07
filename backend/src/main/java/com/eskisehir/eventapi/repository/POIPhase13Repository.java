package com.eskisehir.eventapi.repository;

import com.eskisehir.eventapi.model.POI;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for Phase 13 POI entities
 * Provides CRUD and complex query operations for Eskişehir POIs
 * 
 * NOTE: This repository uses the new POI entity (com.eskisehir.eventapi.model.POI)
 * For legacy operations, use PoiRepository instead.
 */
@Repository("poiPhase13Repository")
public interface POIPhase13Repository extends JpaRepository<POI, String> {
    
    // Category queries
    List<POI> findByCategory(POI.POICategory category);
    
    // District queries
    List<POI> findByDistrict(POI.District district);
    
    // Count queries
    @Query("SELECT p FROM POI p WHERE p.latitude BETWEEN :minLat AND :maxLat " +
           "AND p.longitude BETWEEN :minLon AND :maxLon")
    List<POI> findByGeographicBounds(
        @Param("minLat") Double minLat,
        @Param("maxLat") Double maxLat,
        @Param("minLon") Double minLon,
        @Param("maxLon") Double maxLon
    );
    
    // Score-based queries
    List<POI> findByPopularityScoreGreaterThanEqualOrderByPopularityScoreDesc(Float score);
    
    List<POI> findBySustainabilityScoreGreaterThanEqualOrderBySustainabilityScoreDesc(Float score);
    
    List<POI> findByLocalBusinessScoreGreaterThanEqualOrderByLocalBusinessScoreDesc(Float score);
    
    @Query("SELECT p FROM POI p ORDER BY p.popularityScore DESC LIMIT :limit")
    List<POI> findTopPopularPOIs(@Param("limit") Integer limit);
    
    // Accessibility queries
    @Query("SELECT p FROM POI p WHERE p.wheelchairAccessible = true ORDER BY p.averageScore DESC")
    List<POI> findAccessiblePOIs();
    
    @Query("SELECT p FROM POI p WHERE p.childFriendly = true ORDER BY p.averageScore DESC")
    List<POI> findFamilyFriendlyPOIs();
    
    @Query("SELECT p FROM POI p WHERE LOWER(p.priceLevel) = 'free' ORDER BY p.averageScore DESC")
    List<POI> findFreePOIs();
    
    // Search query
    @Query("SELECT p FROM POI p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.englishName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.address) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<POI> searchPOIs(@Param("query") String query);
    
    // Statistics
    Long countByCategory(POI.POICategory category);
    
    Long countByDistrict(POI.District district);
}
