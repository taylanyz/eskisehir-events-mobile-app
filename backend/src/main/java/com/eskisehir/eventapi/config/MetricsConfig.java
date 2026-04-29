package com.eskisehir.eventapi.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Metrics configuration for Phase 8 monitoring.
 * Integrates Micrometer for application metrics collection.
 */
@Configuration
public class MetricsConfig {

    /**
     * Metrics for recommendation engine.
     */
    @Bean
    public Counter recommendationLatencyCounter(MeterRegistry meterRegistry) {
        return Counter.builder("recommendation.latency")
                .description("Number of recommendation requests")
                .tag("type", "latency")
                .register(meterRegistry);
    }

    /**
     * Metrics for route optimization.
     */
    @Bean
    public Counter routeOptimizationCounter(MeterRegistry meterRegistry) {
        return Counter.builder("route.optimization.count")
                .description("Number of route optimization requests")
                .register(meterRegistry);
    }

    /**
     * Metrics for cache operations.
     */
    @Bean
    public Counter cacheHitCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cache.hits")
                .description("Cache hit count")
                .register(meterRegistry);
    }

    @Bean
    public Counter cacheMissCounter(MeterRegistry meterRegistry) {
        return Counter.builder("cache.misses")
                .description("Cache miss count")
                .register(meterRegistry);
    }

    /**
     * Metrics for endpoint latency.
     */
    @Bean
    public Timer endpointLatencyTimer(MeterRegistry meterRegistry) {
        return Timer.builder("endpoint.latency")
                .description("Endpoint response time")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
    }

    /**
     * Metrics for error tracking.
     */
    @Bean
    public Counter errorCounter(MeterRegistry meterRegistry) {
        return Counter.builder("application.errors")
                .description("Application error count")
                .register(meterRegistry);
    }
}
