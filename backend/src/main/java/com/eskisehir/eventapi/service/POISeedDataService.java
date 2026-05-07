package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.model.POI;
import com.eskisehir.eventapi.repository.POIPhase13Repository;
import com.eskisehir.eventapi.dto.POIStatisticsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * POI Service for Phase 13 - Eskişehir Point of Interest Management
 * Provides business logic for POI operations including:
 * - CRUD operations
 * - Search and filtering by category, district, scores
 * - Proxy score based recommendations
 * - Geographic queries
 */
@Service
@Transactional
public class POISeedDataService {
    
    @Autowired
    private POIPhase13Repository poiRepository;
    /**
     * Get all POIs
     */
    public List<POI> getAllPOIs() {
        return poiRepository.findAll();
    }
    
    /**
     * Get POI by ID
     */
    public Optional<POI> getPOIById(String id) {
        return poiRepository.findById(id);
    }
    
    /**
     * Create new POI
     */
    public POI createPOI(POI poi) {
        return poiRepository.save(poi);
    }
    
    /**
     * Update existing POI
     */
    public POI updatePOI(String id, POI poiDetails) {
        return poiRepository.findById(id)
            .map(poi -> {
                poi.setName(poiDetails.getName());
                poi.setEnglishName(poiDetails.getEnglishName());
                poi.setDescription(poiDetails.getDescription());
                poi.setEnglishDescription(poiDetails.getEnglishDescription());
                poi.setCategory(poiDetails.getCategory());
                poi.setDistrict(poiDetails.getDistrict());
                poi.setLatitude(poiDetails.getLatitude());
                poi.setLongitude(poiDetails.getLongitude());
                poi.setAddress(poiDetails.getAddress());
                poi.setPriceLevel(poiDetails.getPriceLevel());
                poi.setEstimatedCost(poiDetails.getEstimatedCost());
                poi.setTags(poiDetails.getTags());
                poi.setLocationType(poiDetails.getLocationType());
                poi.setWheelchairAccessible(poiDetails.getWheelchairAccessible());
                poi.setChildFriendly(poiDetails.getChildFriendly());
                poi.setUpdatedAt(LocalDateTime.now());
                return poiRepository.save(poi);
            })
            .orElse(null);
    }
    
    /**
     * Delete POI
     */
    public void deletePOI(String id) {
        poiRepository.deleteById(id);
    }
    
    /**
     * Find POIs by category
     */
    public List<POI> findByCategory(String category) {
        return poiRepository.findByCategory(POI.POICategory.valueOf(category));
    }
    
    /**
     * Find active POIs (all POIs are considered active in Phase 13)
     */
    public List<POI> findActivePOIs() {
        return poiRepository.findAll();
    }
    
    /**
     * Search POIs by name
     */
    public List<POI> searchPOIs(String searchTerm) {
        // Can implement full-text search here
        return poiRepository.findAll()
            .stream()
            .filter(poi -> poi.getName().toLowerCase().contains(searchTerm.toLowerCase()))
            .toList();
    }
    
    /**
     * Get POIs paginated
     */
    public Page<POI> getPOIsPaginated(Pageable pageable) {
        return poiRepository.findAll(pageable);
    }
    
    /**
     * Batch create POIs (for seed data)
     */
    public List<POI> createMultiplePOIs(List<POI> pois) {
        return poiRepository.saveAll(pois);
    }
    
    /**
     * Get total POI count
     */
    public Long getTotalPOICount() {
        return poiRepository.count();
    }
    
    /**
     * Get POI count by category
     */
    public Long getPOICountByCategory(String category) {
        return (long) poiRepository.findByCategory(POI.POICategory.valueOf(category)).size();
    }
    
    /**
     * Get category distribution
     */
    public Map<String, Long> getCategoryDistribution() {
        return poiRepository.findAll()
            .stream()
            .collect(Collectors.groupingBy(
                poi -> String.valueOf(poi.getCategory()),
                Collectors.counting()
            ));
    }
    
    // ===== PHASE 13 METHODS =====
    
    /**
     * Find POIs by geographic bounds
     */
    public List<POI> findByGeographicBounds(Double minLat, Double maxLat, Double minLon, Double maxLon) {
        return poiRepository.findAll()
            .stream()
            .filter(poi -> poi.getLatitude() >= minLat && poi.getLatitude() <= maxLat &&
                           poi.getLongitude() >= minLon && poi.getLongitude() <= maxLon)
            .sorted(Comparator.comparingDouble(POI::getAverageScore).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Find most popular POIs
     */
    public List<POI> findMostPopularPOIs(Integer limit) {
        return poiRepository.findAll()
            .stream()
            .filter(poi -> poi.getPopularityScore() != null)
            .sorted((p1, p2) -> Float.compare(p2.getPopularityScore(), p1.getPopularityScore()))
            .limit(limit != null ? limit : 10)
            .collect(Collectors.toList());
    }
    
    /**
     * Find sustainable POIs
     */
    public List<POI> findSustainablePOIs(Float minScore) {
        return poiRepository.findAll()
            .stream()
            .filter(poi -> poi.getSustainabilityScore() != null && 
                          poi.getSustainabilityScore() >= (minScore != null ? minScore : 70f))
            .sorted((p1, p2) -> Float.compare(p2.getSustainabilityScore(), p1.getSustainabilityScore()))
            .collect(Collectors.toList());
    }
    
    /**
     * Find local business POIs
     */
    public List<POI> findLocalBusinessPOIs(Float minScore) {
        return poiRepository.findAll()
            .stream()
            .filter(poi -> poi.getLocalBusinessScore() != null && 
                          poi.getLocalBusinessScore() >= (minScore != null ? minScore : 75f))
            .sorted((p1, p2) -> Float.compare(p2.getLocalBusinessScore(), p1.getLocalBusinessScore()))
            .collect(Collectors.toList());
    }
    
    /**
     * Find POIs by district
     */
    public List<POI> findByDistrict(String district) {
        return poiRepository.findByDistrict(POI.District.valueOf(district));
    }
    
    /**
     * Find wheelchair accessible POIs
     */
    public List<POI> findAccessiblePOIs() {
        return poiRepository.findAccessiblePOIs();
    }
    
    /**
     * Find family-friendly POIs
     */
    public List<POI> findFamilyFriendlyPOIs() {
        return poiRepository.findFamilyFriendlyPOIs();
    }
    
    /**
     * Find free POIs
     */
    public List<POI> findFreePOIs() {
        return poiRepository.findFreePOIs();
    }
    
    /**
     * Get available districts
     */
    public Set<String> getAvailableDistricts() {
        return poiRepository.findAll()
            .stream()
            .map(poi -> poi.getDistrict() != null ? String.valueOf(poi.getDistrict()) : null)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
    
    /**
     * Get available categories
     */
    public Set<String> getAvailableCategories() {
        return poiRepository.findAll()
            .stream()
            .map(poi -> poi.getCategory() != null ? String.valueOf(poi.getCategory()) : null)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
    
    /**
     * Get POI statistics
     */
    public POIStatisticsDto getStatistics() {
        List<POI> allPois = poiRepository.findAll();
        
        POIStatisticsDto stats = new POIStatisticsDto();
        stats.setTotalPOIs((long) allPois.size());
        stats.setTotalCategories(getAvailableCategories().size());
        stats.setTotalDistricts(getAvailableDistricts().size());
        
        // Category distribution
        Map<String, Long> categoryDistribution = allPois.stream()
            .collect(Collectors.groupingBy(
                poi -> poi.getCategory() != null ? String.valueOf(poi.getCategory()) : "UNKNOWN",
                Collectors.counting()
            ));
        stats.setCategoryDistribution(categoryDistribution);
        
        // District distribution
        Map<String, Long> districtDistribution = allPois.stream()
            .collect(Collectors.groupingBy(
                poi -> poi.getDistrict() != null ? String.valueOf(poi.getDistrict()) : "UNKNOWN",
                Collectors.counting()
            ));
        stats.setDistrictDistribution(districtDistribution);
        
        // Average scores
        if (!allPois.isEmpty()) {
            double avgPopularity = allPois.stream()
                .filter(p -> p.getPopularityScore() != null)
                .mapToDouble(POI::getPopularityScore)
                .average()
                .orElse(0.0);
            
            double avgCrowd = allPois.stream()
                .filter(p -> p.getCrowdProxyScore() != null)
                .mapToDouble(POI::getCrowdProxyScore)
                .average()
                .orElse(0.0);
            
            double avgSustainability = allPois.stream()
                .filter(p -> p.getSustainabilityScore() != null)
                .mapToDouble(POI::getSustainabilityScore)
                .average()
                .orElse(0.0);
            
            double avgLocalBusiness = allPois.stream()
                .filter(p -> p.getLocalBusinessScore() != null)
                .mapToDouble(POI::getLocalBusinessScore)
                .average()
                .orElse(0.0);
            
            stats.setAveragePopularityScore((float) avgPopularity);
            stats.setAverageCrowdScore((float) avgCrowd);
            stats.setAverageSustainabilityScore((float) avgSustainability);
            stats.setAverageLocalBusinessScore((float) avgLocalBusiness);
        }
        
        // Accessibility counts
        long wheelchairAccessible = allPois.stream()
            .filter(p -> p.getWheelchairAccessible() != null && p.getWheelchairAccessible())
            .count();
        stats.setWheelchairAccessibleCount((int) wheelchairAccessible);
        
        long childFriendly = allPois.stream()
            .filter(p -> p.getChildFriendly() != null && p.getChildFriendly())
            .count();
        stats.setChildFriendlyCount((int) childFriendly);
        
        return stats;
    }
}
