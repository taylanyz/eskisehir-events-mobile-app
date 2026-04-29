package com.eskisehir.eventapi.controller;

import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.service.PoiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Health Check and Phase 8 API.
 */
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ApiHealthController {

    private final PoiService poiService;

    @Autowired
    public ApiHealthController(PoiService poiService) {
        this.poiService = poiService;
    }

    /**
     * API Health Check.
     * GET /api/v1/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "operational");
        response.put("service", "Eskisehir Event API - Phase 8");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * Get all POIs (Phase 8 Test Endpoint).
     * GET /api/v1/pois/all
     */
    @GetMapping("/pois/all")
    public ResponseEntity<Map<String, Object>> getAllPois() {
        List<Poi> pois = poiService.getAllPois();
        Map<String, Object> response = new HashMap<>();
        response.put("count", pois.size());
        response.put("pois", pois);
        return ResponseEntity.ok(response);
    }

    /**
     * Get POI statistics.
     * GET /api/v1/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        List<Poi> pois = poiService.getAllPois();
        Map<String, Object> response = new HashMap<>();
        response.put("total_pois", pois.size());
        response.put("active_pois", pois.stream().filter(Poi::getIsActive).count());
        response.put("database", "H2 In-Memory");
        response.put("phase", "8");
        return ResponseEntity.ok(response);
    }
}
