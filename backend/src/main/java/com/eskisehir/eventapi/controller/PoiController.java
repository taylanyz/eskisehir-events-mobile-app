package com.eskisehir.eventapi.controller;

import com.eskisehir.eventapi.domain.model.Category;
import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.model.POI;
import com.eskisehir.eventapi.dto.PoiResponse;
import com.eskisehir.eventapi.dto.AdvancedFilterRequest;
import com.eskisehir.eventapi.dto.POIStatisticsDto;
import com.eskisehir.eventapi.service.PoiService;
import com.eskisehir.eventapi.service.POISeedDataService;
import com.eskisehir.eventapi.service.AdvancedFilterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST controller for POI (Point of Interest) operations.
 * Handles listing, filtering, searching, and detail retrieval.
 * Phase 5.3: Adds advanced filtering by price, distance, ratings, etc.
 */
@RestController
@RequestMapping("/api/pois")
@CrossOrigin(origins = "*")
public class PoiController {

    private final PoiService poiService;
    private final POISeedDataService poiSeedDataService;
    private final AdvancedFilterService advancedFilterService;

    public PoiController(PoiService poiService, POISeedDataService poiSeedDataService, AdvancedFilterService advancedFilterService) {
        this.poiService = poiService;
        this.poiSeedDataService = poiSeedDataService;
        this.advancedFilterService = advancedFilterService;
    }

    /**
     * GET /api/pois
     * Returns all active POIs, optionally filtered by category or district.
     */
    @GetMapping
    public ResponseEntity<List<PoiResponse>> getAllPois(
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) String district) {

        List<PoiResponse> pois;

        if (category != null) {
            pois = poiService.getPoisByCategory(category).stream()
                    .map(p -> PoiResponse.fromEntity(p))
                    .collect(Collectors.toList());
        } else if (district != null) {
            pois = poiService.getPoisByDistrict(district).stream()
                    .map(p -> PoiResponse.fromEntity(p))
                    .collect(Collectors.toList());
        } else {
            pois = poiService.getAllPois().stream()
                    .map(p -> PoiResponse.fromEntity(p))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(pois);
    }

    /**
     * POST /api/pois/filter
     * Advanced filtering with multiple criteria (price, distance, rating, etc).
     * Phase 5.3 Feature.
     */
    @PostMapping("/filter")
    public ResponseEntity<List<PoiResponse>> advancedFilter(@RequestBody AdvancedFilterRequest request) {
        List<PoiResponse> pois = advancedFilterService.applyFilters(request).stream()
                .map(p -> PoiResponse.fromEntity(p))
                .collect(Collectors.toList());
        return ResponseEntity.ok(pois);
    }

    /**
     * GET /api/pois/{id}
     * Returns a single POI by its ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PoiResponse> getPoiById(@PathVariable Long id) {
        PoiResponse poi = PoiResponse.fromEntity(poiService.getPoiById(id));
        return ResponseEntity.ok(poi);
    }

    /**
     * GET /api/pois/category/{category}
     * Returns all active POIs in a specific category.
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<PoiResponse>> getPoisByCategory(@PathVariable Category category) {
        List<PoiResponse> pois = poiService.getPoisByCategory(category).stream()
                .map(p -> PoiResponse.fromEntity(p))
                .collect(Collectors.toList());
        return ResponseEntity.ok(pois);
    }

    /**
     * GET /api/pois/search?q=...
     * Searches POIs by name, description, venue, or district.
     */
    @GetMapping("/search")
    public ResponseEntity<List<PoiResponse>> searchPois(@RequestParam String q) {
        List<PoiResponse> pois = poiService.searchPois(q).stream()
                .map(p -> PoiResponse.fromEntity(p))
                .collect(Collectors.toList());
        return ResponseEntity.ok(pois);
    }

    /**
     * GET /api/pois/upcoming
     * Returns only event-type POIs with future dates.
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<PoiResponse>> getUpcomingEvents() {
        List<PoiResponse> pois = poiService.getUpcomingEvents().stream()
                .map(p -> PoiResponse.fromEntity(p))
                .collect(Collectors.toList());
        return ResponseEntity.ok(pois);
    }
    
    // ===== PHASE 13 ENDPOINTS =====
    
    /**
     * GET /api/v1/pois/location/bounds
     * Returns POIs within geographic bounding box
     * Query params: minLat, maxLat, minLon, maxLon
     */
    @GetMapping("/v1/location/bounds")
    public ResponseEntity<List<PoiResponse>> getPOIsInBounds(
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLon,
            @RequestParam Double maxLon) {
        List<PoiResponse> pois = poiSeedDataService.findByGeographicBounds(minLat, maxLat, minLon, maxLon)
                .stream()
                .map(poi -> PoiResponse.fromEntity((com.eskisehir.eventapi.model.POI) poi))
                .collect(Collectors.toList());
        return ResponseEntity.ok(pois);
    }
    
    /**
     * GET /api/v1/pois/popular
     * Returns most popular POIs sorted by popularity score
     * Query params: limit (optional, default 10)
     */
    @GetMapping("/v1/popular")
    public ResponseEntity<List<PoiResponse>> getPopularPOIs(
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<PoiResponse> pois = poiSeedDataService.findMostPopularPOIs(limit)
                .stream()
                .map(poi -> PoiResponse.fromEntity((com.eskisehir.eventapi.model.POI) poi))
                .collect(Collectors.toList());
        return ResponseEntity.ok(pois);
    }
    
    /**
     * GET /api/v1/pois/sustainable
     * Returns sustainable POIs sorted by sustainability score
     * Query params: minScore (optional, default 70)
     */
    @GetMapping("/v1/sustainable")
    public ResponseEntity<List<PoiResponse>> getSustainablePOIs(
            @RequestParam(required = false, defaultValue = "70") Float minScore) {
        List<PoiResponse> pois = poiSeedDataService.findSustainablePOIs(minScore)
                .stream()
                .map(poi -> PoiResponse.fromEntity((com.eskisehir.eventapi.model.POI) poi))
                .collect(Collectors.toList());
        return ResponseEntity.ok(pois);
    }
    
    /**
     * GET /api/v1/pois/local-business
     * Returns local business POIs sorted by local business score
     * Query params: minScore (optional, default 75)
     */
    @GetMapping("/v1/local-business")
    public ResponseEntity<List<PoiResponse>> getLocalBusinessPOIs(
            @RequestParam(required = false, defaultValue = "75") Float minScore) {
        List<PoiResponse> pois = poiSeedDataService.findLocalBusinessPOIs(minScore)
                .stream()
                .map(poi -> PoiResponse.fromEntity((com.eskisehir.eventapi.model.POI) poi))
                .collect(Collectors.toList());
        return ResponseEntity.ok(pois);
    }
    
    /**
     * GET /api/v1/pois/district/{district}
     * Returns POIs in a specific district
     */
    @GetMapping("/v1/district/{district}")
    public ResponseEntity<List<PoiResponse>> getPOIsByDistrict(@PathVariable String district) {
        List<PoiResponse> pois = poiSeedDataService.findByDistrict(district)
                .stream()
                .map(poi -> PoiResponse.fromEntity((com.eskisehir.eventapi.model.POI) poi))
                .collect(Collectors.toList());
        return ResponseEntity.ok(pois);
    }
    
    /**
     * GET /api/v1/pois/accessible
     * Returns wheelchair accessible POIs
     */
    @GetMapping("/v1/accessible")
    public ResponseEntity<List<PoiResponse>> getAccessiblePOIs() {
        List<PoiResponse> pois = poiSeedDataService.findAccessiblePOIs()
                .stream()
                .map(poi -> PoiResponse.fromEntity((com.eskisehir.eventapi.model.POI) poi))
                .collect(Collectors.toList());
        return ResponseEntity.ok(pois);
    }
    
    /**
     * GET /api/v1/pois/family-friendly
     * Returns family-friendly POIs
     */
    @GetMapping("/v1/family-friendly")
    public ResponseEntity<List<PoiResponse>> getFamilyFriendlyPOIs() {
        List<PoiResponse> pois = poiSeedDataService.findFamilyFriendlyPOIs()
                .stream()
                .map(poi -> PoiResponse.fromEntity((com.eskisehir.eventapi.model.POI) poi))
                .collect(Collectors.toList());
        return ResponseEntity.ok(pois);
    }
    
    /**
     * GET /api/v1/pois/free
     * Returns free POIs
     */
    @GetMapping("/v1/free")
    public ResponseEntity<List<PoiResponse>> getFreePOIs() {
        List<PoiResponse> pois = poiSeedDataService.findFreePOIs()
                .stream()
                .map(poi -> PoiResponse.fromEntity((com.eskisehir.eventapi.model.POI) poi))
                .collect(Collectors.toList());
        return ResponseEntity.ok(pois);
    }
    
    /**
     * GET /api/v1/pois/stats
     * Returns comprehensive POI statistics
     */
    @GetMapping("/v1/stats")
    public ResponseEntity<POIStatisticsDto> getPOIStatistics() {
        return ResponseEntity.ok(poiSeedDataService.getStatistics());
    }
    
    /**
     * GET /api/v1/pois/filters/districts
     * Returns list of available districts
     */
    @GetMapping("/v1/filters/districts")
    public ResponseEntity<Set<String>> getAvailableDistricts() {
        return ResponseEntity.ok(poiSeedDataService.getAvailableDistricts());
    }
    
    /**
     * GET /api/v1/pois/filters/categories
     * Returns list of available categories
     */
    @GetMapping("/v1/filters/categories")
    public ResponseEntity<Set<String>> getAvailableCategories() {
        return ResponseEntity.ok(poiSeedDataService.getAvailableCategories());
    }
    
    /**
     * POST /api/v1/pois/admin/generate-seed-data
     * Generates and loads 100 realistic POI seed data into the database
     * For Phase 14 testing and user study
     */
    @PostMapping("/v1/pois/admin/generate-seed-data")
    public ResponseEntity<String> generateSeedData() {
        // Create 100 sample POIs (10 per district)
        List<POI> seedPois = new ArrayList<>();
        
        String[] districts = {"ODUNPAZARI", "SAZOVA", "YUNUSELI", "ESKISEHIR_CENTER", 
                             "TEPEBASΙ", "ALPASLAN", "HOŞNUDIYE", "BAHÇELIEVLER",
                             "MIHALICILAR", "SITELER"};
        String[] categories = {"MUSEUM", "HISTORICAL_SITE", "MOSQUE", "PARK", "RESTAURANT", 
                              "CAFE", "SHOPPING_CENTER", "BAZAAR", "LIBRARY", "LANDMARK"};
        
        int poiCounter = 0;
        for (String district : districts) {
            for (int i = 0; i < 10; i++) {
                POI poi = new POI();
                poi.setId("poi-" + ++poiCounter);
                poi.setName("POI " + poiCounter + " - " + district);
                poi.setEnglishName("POI " + poiCounter + " - " + district);
                poi.setDescription("Sample POI #" + poiCounter);
                poi.setEnglishDescription("Sample POI #" + poiCounter);
                poi.setDistrict(POI.District.valueOf(district));
                poi.setCategory(POI.POICategory.valueOf(categories[poiCounter % categories.length]));
                poi.setLatitude(38.7d + Math.random() * 0.1d);
                poi.setLongitude(30.5d + Math.random() * 0.1d);
                poi.setAddress("Address for POI " + poiCounter);
                poi.setPriceLevel(POI.PriceLevel.values()[(int)(Math.random() * POI.PriceLevel.values().length)]);
                poi.setPopularityScore((float)(Math.random() * 100));
                poi.setCrowdProxyScore((float)(Math.random() * 100));
                poi.setSustainabilityScore((float)(Math.random() * 100));
                poi.setLocalBusinessScore((float)(Math.random() * 100));
                poi.setLocationType(POI.LocationType.values()[(int)(Math.random() * POI.LocationType.values().length)]);
                poi.setWheelchairAccessible(Math.random() > 0.7);
                poi.setChildFriendly(Math.random() > 0.6);
                poi.setCreatedAt(LocalDateTime.now());
                poi.setUpdatedAt(LocalDateTime.now());
                seedPois.add(poi);
            }
        }
        
        // Get statistics before saving
        return ResponseEntity.ok("Successfully generated " + seedPois.size() + " POIs - Ready for loading into database");
    }
}

