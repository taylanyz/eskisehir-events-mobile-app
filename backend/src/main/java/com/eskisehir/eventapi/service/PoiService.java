package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.domain.model.Category;
import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.exception.PoiNotFoundException;
import com.eskisehir.eventapi.repository.PoiRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PoiService {

    private final PoiRepository poiRepository;

    public PoiService(PoiRepository poiRepository) {
        this.poiRepository = poiRepository;
    }

    /**
     * Retrieves all active POIs ordered by date.
     */
    public List<Poi> getAllPois() {
        return poiRepository.findAllActiveOrdered();
    }

    /**
     * Retrieves a single POI by ID.
     *
     * @throws PoiNotFoundException if no POI exists with the given ID
     */
    @Cacheable(value = "poi", key = "#id")
    public Poi getPoiById(Long id) {
        return poiRepository.findById(id)
                .orElseThrow(() -> new PoiNotFoundException(id));
    }

    /**
     * Retrieves active POIs filtered by category.
     */
    public List<Poi> getPoisByCategory(Category category) {
        return poiRepository.findByCategoryAndIsActiveTrue(category);
    }

    /**
     * Retrieves active POIs filtered by district.
     */
    public List<Poi> getPoisByDistrict(String district) {
        return poiRepository.findByDistrictAndIsActiveTrue(district);
    }

    /**
     * Retrieves only upcoming event-type POIs.
     */
    public List<Poi> getUpcomingEvents() {
        return poiRepository.findByDateAfterAndIsActiveTrue(LocalDateTime.now());
    }

    /**
     * Searches POIs by name, description, venue, or district.
     */
    public List<Poi> searchPois(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllPois();
        }
        return poiRepository.searchPois(query.trim());
    }
}
