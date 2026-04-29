package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.dto.AdvancedFilterRequest;
import com.eskisehir.eventapi.repository.PoiRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for advanced filtering of POIs with multiple criteria.
 * Phase 5.3: Supports price range, distance, ratings, categories, and tags.
 */
@Service
public class AdvancedFilterService {

    private final PoiRepository poiRepository;
    private final GeoService geoService;

    public AdvancedFilterService(PoiRepository poiRepository, GeoService geoService) {
        this.poiRepository = poiRepository;
        this.geoService = geoService;
    }

    /**
     * Apply advanced filters to POIs.
     */
    public List<Poi> applyFilters(AdvancedFilterRequest request) {
        List<Poi> allPois = poiRepository.findAll();

        return allPois.stream()
                .filter(poi -> filterByPrice(poi, request))
                .filter(poi -> filterByDistance(poi, request))
                .filter(poi -> filterByRating(poi, request))
                .filter(poi -> filterByCategory(poi, request))
                .filter(poi -> filterByTags(poi, request))
                .collect(Collectors.toList());
    }

    /**
     * Filter POIs by price range.
     */
    private boolean filterByPrice(Poi poi, AdvancedFilterRequest request) {
        if (poi.getPrice() == null) return false;
        
        if (request.getMinPrice() != null && poi.getPrice() < request.getMinPrice()) {
            return false;
        }
        if (request.getMaxPrice() != null && poi.getPrice() > request.getMaxPrice()) {
            return false;
        }
        return true;
    }

    /**
     * Filter POIs by distance from specified location.
     */
    private boolean filterByDistance(Poi poi, AdvancedFilterRequest request) {
        if (request.getMaxDistance() == null || request.getLatitude() == null || 
            request.getLongitude() == null) {
            return true;
        }

        double distance = geoService.getDistanceKm(
                request.getLatitude(),
                request.getLongitude(),
                poi.getLatitude(),
                poi.getLongitude()
        );

        return distance <= request.getMaxDistance();
    }

    /**
     * Filter POIs by minimum rating (not currently stored per POI, placeholder).
     */
    private boolean filterByRating(Poi poi, AdvancedFilterRequest request) {
        // In future: fetch POI ratings from analytics
        return true;
    }

    /**
     * Filter POIs by category.
     */
    private boolean filterByCategory(Poi poi, AdvancedFilterRequest request) {
        if (request.getCategories() == null || request.getCategories().isEmpty()) {
            return true;
        }
        return request.getCategories().contains(poi.getCategory().toString());
    }

    /**
     * Filter POIs by tags.
     */
    private boolean filterByTags(Poi poi, AdvancedFilterRequest request) {
        if (request.getTags() == null || request.getTags().isEmpty()) {
            return true;
        }
        if (poi.getTags() == null) return false;
        
        return poi.getTags().stream()
                .anyMatch(tag -> request.getTags().contains(tag));
    }
}
