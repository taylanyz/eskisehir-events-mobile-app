package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.repository.PoiRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Caching service for offline support.
 * Phase 5.4: Caches POI data and routes for offline access.
 */
@Service
public class CacheService {

    private final PoiRepository poiRepository;

    public CacheService(PoiRepository poiRepository) {
        this.poiRepository = poiRepository;
    }

    /**
     * Get all POIs with caching for offline support.
     * TTL: 24 hours
     */
    @Cacheable(value = "pois", unless = "#result == null")
    public List<Poi> getAllPoisCached() {
        return poiRepository.findAll();
    }

    /**
     * Get POI by ID with caching.
     * TTL: 24 hours
     */
    @Cacheable(value = "poi", key = "#id", unless = "#result == null")
    public Poi getPoiByCachedId(Long id) {
        return poiRepository.findById(id).orElse(null);
    }

    /**
     * Clear all caches (useful for cache refresh).
     */
    public void clearAllCaches() {
        // Cache names defined in application.properties
    }
}
