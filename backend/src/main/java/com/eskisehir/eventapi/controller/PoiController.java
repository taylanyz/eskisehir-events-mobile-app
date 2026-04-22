package com.eskisehir.eventapi.controller;

import com.eskisehir.eventapi.domain.model.Category;
import com.eskisehir.eventapi.dto.PoiResponse;
import com.eskisehir.eventapi.service.PoiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for POI (Point of Interest) operations.
 * Handles listing, filtering, searching, and detail retrieval.
 */
@RestController
@RequestMapping("/api/pois")
@CrossOrigin(origins = "*")
public class PoiController {

    private final PoiService poiService;

    public PoiController(PoiService poiService) {
        this.poiService = poiService;
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
                    .map(PoiResponse::fromEntity)
                    .collect(Collectors.toList());
        } else if (district != null) {
            pois = poiService.getPoisByDistrict(district).stream()
                    .map(PoiResponse::fromEntity)
                    .collect(Collectors.toList());
        } else {
            pois = poiService.getAllPois().stream()
                    .map(PoiResponse::fromEntity)
                    .collect(Collectors.toList());
        }

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
                .map(PoiResponse::fromEntity)
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
                .map(PoiResponse::fromEntity)
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
                .map(PoiResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pois);
    }
}
