package com.eskisehir.eventapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Analytics service for tracking application metrics and performance.
 * Phase 5.5: Logs requests, tracks errors, and monitors performance.
 */
@Service
public class AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);

    private final Map<String, AtomicInteger> endpointHits = new HashMap<>();
    private final Map<String, Long> endpointResponseTimes = new HashMap<>();
    private final AtomicInteger errorCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);

    /**
     * Track API endpoint hit.
     */
    public void trackEndpointHit(String endpoint) {
        endpointHits.computeIfAbsent(endpoint, k -> new AtomicInteger(0)).incrementAndGet();
        log.info("Endpoint hit: {} (total: {})", endpoint, endpointHits.get(endpoint).get());
    }

    /**
     * Track endpoint response time.
     */
    public void trackResponseTime(String endpoint, long responseTimeMs) {
        endpointResponseTimes.put(endpoint, responseTimeMs);
        log.debug("Response time for {}: {} ms", endpoint, responseTimeMs);
    }

    /**
     * Track successful request.
     */
    public void trackSuccess() {
        successCount.incrementAndGet();
    }

    /**
     * Track failed request.
     */
    public void trackError(String errorMessage) {
        errorCount.incrementAndGet();
        log.error("Error tracked: {} (total errors: {})", errorMessage, errorCount.get());
    }

    /**
     * Get analytics summary.
     */
    public Map<String, Object> getAnalyticsSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("timestamp", LocalDateTime.now());
        summary.put("totalRequests", successCount.get() + errorCount.get());
        summary.put("successfulRequests", successCount.get());
        summary.put("failedRequests", errorCount.get());
        summary.put("successRate", 
            (successCount.get() + errorCount.get()) > 0 ? 
            (double) successCount.get() / (successCount.get() + errorCount.get()) * 100 : 0);
        summary.put("topEndpoints", endpointHits);
        return summary;
    }

    /**
     * Reset analytics counters.
     */
    public void reset() {
        endpointHits.clear();
        endpointResponseTimes.clear();
        errorCount.set(0);
        successCount.set(0);
        log.info("Analytics counters reset");
    }
}
