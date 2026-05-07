package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.model.POI;
import com.eskisehir.eventapi.repository.POIPhase13Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * POI Seed Data Loader for Phase 13
 * Loads initial Eskişehir POI dataset into the database
 * 
 * Can be triggered:
 * 1. On application startup (if data/pois-seed.json exists)
 * 2. Via API endpoint /api/admin/pois/load-seed-data
 * 3. Manually via CLI: ./gradlew loadPOISeedData
 */
@Service
public class POISeedDataLoaderService implements ApplicationRunner {
    
    private static final Logger log = LoggerFactory.getLogger(POISeedDataLoaderService.class);
    @Autowired
    private POIPhase13Repository poiRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Load seed data from JSON file on application startup
     * This runs once when the application starts
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Check if seed data should be loaded on startup
        String loadOnStartup = System.getenv("LOAD_POI_SEED_DATA_ON_STARTUP");
        
        if ("true".equalsIgnoreCase(loadOnStartup)) {
            loadSeedDataFromFile("data/pois-seed.json");
        }
    }
    
    /**
     * Load POI seed data from JSON file
     */
    @Transactional
    public void loadSeedDataFromFile(String filePath) {
        try {
            log.info("Loading POI seed data from: {}", filePath);
            
            // Check if data already exists
            Long existingCount = poiRepository.count();
            if (existingCount > 100) {
                log.warn("Database already contains {} POIs. Skipping seed data load.", existingCount);
                return;
            }
            
            // Load JSON file from resources
            ClassPathResource resource = new ClassPathResource(filePath);
            InputStream inputStream = resource.getInputStream();
            
            // Parse JSON to POI array
            POI[] poisArray = objectMapper.readValue(inputStream, POI[].class);
            List<POI> pois = Arrays.asList(poisArray);
            
            log.info("Parsed {} POIs from seed data file", pois.size());
            
            // Save to database
            List<POI> savedPois = poiRepository.saveAll(pois);
            
            log.info("Successfully loaded {} POIs to database", savedPois.size());
            
            // Print statistics
            printLoadingStatistics(savedPois);
            
        } catch (Exception e) {
            log.error("Error loading POI seed data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load POI seed data", e);
        }
    }
    
    /**
     * Load POI seed data from JSON string
     */
    @Transactional
    public void loadSeedDataFromString(String jsonData) {
        try {
            log.info("Loading POI seed data from JSON string");
            
            // Parse JSON
            POI[] poisArray = objectMapper.readValue(jsonData, POI[].class);
            List<POI> pois = Arrays.asList(poisArray);
            
            // Save to database
            List<POI> savedPois = poiRepository.saveAll(pois);
            
            log.info("Successfully loaded {} POIs to database", savedPois.size());
            printLoadingStatistics(savedPois);
            
        } catch (Exception e) {
            log.error("Error loading POI seed data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load POI seed data", e);
        }
    }
    
    /**
     * Load POI seed data directly from list
     */
    @Transactional
    public void loadSeedDataFromList(List<POI> pois) {
        try {
            log.info("Loading {} POIs to database", pois.size());
            
            // Save to database
            List<POI> savedPois = poiRepository.saveAll(pois);
            
            log.info("Successfully loaded {} POIs to database", savedPois.size());
            printLoadingStatistics(savedPois);
            
        } catch (Exception e) {
            log.error("Error loading POI seed data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load POI seed data", e);
        }
    }
    
    /**
     * Clear all POI data (use with caution!)
     */
    @Transactional
    public void clearAllPOIs() {
        try {
            Long count = poiRepository.count();
            poiRepository.deleteAll();
            log.warn("Deleted {} POIs from database", count);
        } catch (Exception e) {
            log.error("Error clearing POI data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to clear POI data", e);
        }
    }
    
    /**
     * Reload seed data (clears and reloads)
     */
    @Transactional
    public void reloadSeedData(String filePath) {
        log.info("Reloading seed data. Clearing existing POIs and loading from: {}", filePath);
        clearAllPOIs();
        loadSeedDataFromFile(filePath);
    }
    
    /**
     * Print loading statistics
     */
    private void printLoadingStatistics(List<POI> pois) {
        if (pois.isEmpty()) {
            return;
        }
        
        // Calculate statistics
        long categoryCount = pois.stream().map(poi -> String.valueOf(poi.getCategory())).distinct().count();
        long districtCount = pois.stream()
            .map(poi -> String.valueOf(poi.getDistrict()))
            .distinct()
            .count();
        
        double avgPopularityScore = pois.stream()
            .filter(p -> p.getPopularityScore() != null)
            .mapToDouble(p -> p.getPopularityScore())
            .average()
            .orElse(0.0);
        
        long wheelchairAccessibleCount = pois.stream()
            .filter(p -> p.getWheelchairAccessible() != null && p.getWheelchairAccessible())
            .count();
        
        long childFriendlyCount = pois.stream()
            .filter(p -> p.getChildFriendly() != null && p.getChildFriendly())
            .count();
        
        log.info("=== POI Seed Data Load Summary ===");
        log.info("Total POIs loaded: {}", pois.size());
        log.info("Categories: {}", categoryCount);
        log.info("Districts: {}", districtCount);
        log.info("Average Popularity Score: {}", String.format("%.2f", avgPopularityScore));
        log.info("Wheelchair Accessible: {}", wheelchairAccessibleCount);
        log.info("Child Friendly: {}", childFriendlyCount);
        log.info("====================================");
    }
    
    /**
     * Validate loaded data
     */
    public ValidationResult validateLoadedData() {
        try {
            List<POI> allPois = poiRepository.findAll();
            int totalCount = allPois.size();
            int validCount = 0;
            int invalidCount = 0;
            
            for (POI poi : allPois) {
                if (validatePOI(poi)) {
                    validCount++;
                } else {
                    invalidCount++;
                    log.warn("Invalid POI: {}", poi.getName());
                }
            }
            
            return new ValidationResult(
                totalCount,
                validCount,
                invalidCount,
                validCount == totalCount
            );
            
        } catch (Exception e) {
            log.error("Error validating loaded data: {}", e.getMessage(), e);
            return new ValidationResult(0, 0, 0, false);
        }
    }
    
    /**
     * Validate a single POI
     */
    private boolean validatePOI(POI poi) {
        return poi.getName() != null && !poi.getName().isEmpty() &&
               poi.getCategory() != null &&
               poi.getLatitude() != null &&
               poi.getLongitude() != null &&
               poi.getLatitude() >= -90 && poi.getLatitude() <= 90 &&
               poi.getLongitude() >= -180 && poi.getLongitude() <= 180;
    }
    
    /**
     * Validation result DTO
     */
    public static class ValidationResult {
        public int total;
        public int valid;
        public int invalid;
        public boolean isValid;
        
        public ValidationResult(int total, int valid, int invalid, boolean isValid) {
            this.total = total;
            this.valid = valid;
            this.invalid = invalid;
            this.isValid = isValid;
        }
        
        @Override
        public String toString() {
            return "ValidationResult{" +
                    "total=" + total +
                    ", valid=" + valid +
                    ", invalid=" + invalid +
                    ", isValid=" + isValid +
                    '}';
        }
    }
}
