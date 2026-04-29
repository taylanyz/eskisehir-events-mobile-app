package com.eskisehir.eventapi.repository;

import com.eskisehir.eventapi.domain.model.Route;
import com.eskisehir.eventapi.domain.model.RouteStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    List<Route> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Route> findByUserIdAndStatus(Long userId, RouteStatus status);

    /**
     * Find a public route by its share code.
     */
    Optional<Route> findByShareCodeAndIsPublicTrue(String shareCode);

    /**
     * Find all public routes, sorted by newest first.
     */
    List<Route> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find trending routes (highest rated and shared).
     */
    @Query("SELECT r FROM Route r WHERE r.isPublic = true " +
           "ORDER BY r.averageRating DESC, r.shareCount DESC")
    List<Route> findTrendingRoutes(Pageable pageable);
}

