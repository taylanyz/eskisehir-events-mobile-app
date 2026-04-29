package com.eskisehir.eventapi.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for Phase 8.
 * Configures in-memory caching with Spring Cache abstraction.
 * For production, replace with Redis backend.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Cache manager using in-memory ConcurrentHashMap.
     * Caches:
     * - poi::poiId (1 hour TTL)
     * - recommendation::userId::contextHash (30 min TTL)
     * - weather::lat::lon (30 min TTL)
     * - user::userId (1 hour TTL)
     * - route::routeId (1 hour TTL)
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "poi",
                "recommendation",
                "weather",
                "user",
                "route",
                "interaction",
                "analytics"
        );
    }

    /**
     * Note: For production deployment with distributed caching:
     * 1. Add spring-boot-starter-data-redis dependency
     * 2. Configure Redis connection properties
     * 3. Return RedisCacheManager instead of ConcurrentMapCacheManager
     *
     * Production Redis Configuration Example:
     * @Bean
     * public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
     *     RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
     *         .entryTtl(Duration.ofMinutes(10))
     *         .serializeValuesWith(jackson2JsonRedisSerializer());
     *     return RedisCacheManager.create(factory);
     * }
     */
}
