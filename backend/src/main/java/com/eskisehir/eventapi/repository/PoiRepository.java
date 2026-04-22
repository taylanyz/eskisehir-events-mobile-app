package com.eskisehir.eventapi.repository;

import com.eskisehir.eventapi.domain.model.Category;
import com.eskisehir.eventapi.domain.model.Poi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for Poi entities.
 * Provides CRUD, category filtering, search, and date-based lookups.
 */
@Repository
public interface PoiRepository extends JpaRepository<Poi, Long> {

    List<Poi> findByCategory(Category category);

    List<Poi> findByIsActiveTrue();

    /** Find event-type POIs occurring after a given date */
    List<Poi> findByDateAfterAndIsActiveTrue(LocalDateTime date);

    List<Poi> findByCategoryAndIsActiveTrue(Category category);

    List<Poi> findByCategoryAndDateAfter(Category category, LocalDateTime date);

    /** Find POIs by district (e.g., "Odunpazarı") */
    List<Poi> findByDistrictAndIsActiveTrue(String district);

    @Query("SELECT p FROM Poi p WHERE p.isActive = true AND (" +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.venue) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.district) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Poi> searchPois(@Param("query") String query);

    List<Poi> findAllByIsActiveTrueOrderByDateAsc();

    /** Find all active POIs (both events and permanent attractions) */
    @Query("SELECT p FROM Poi p WHERE p.isActive = true ORDER BY " +
           "CASE WHEN p.date IS NOT NULL THEN p.date ELSE CURRENT_TIMESTAMP END ASC")
    List<Poi> findAllActiveOrdered();
}
