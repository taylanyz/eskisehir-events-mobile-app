package com.eskisehir.eventapi.controller;

import com.eskisehir.eventapi.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Analytics endpoint for Phase 5.5.
 * Provides metrics and performance data.
 */
@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * GET /api/analytics/summary
     * Returns current analytics summary.
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getAnalyticsSummary() {
        return ResponseEntity.ok(analyticsService.getAnalyticsSummary());
    }

    /**
     * POST /api/analytics/reset
     * Reset analytics counters (admin only).
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetAnalytics() {
        analyticsService.reset();
        return ResponseEntity.ok("Analytics reset successfully");
    }
}
