package com.eskisehir.eventapi.repository;

import com.eskisehir.eventapi.domain.model.Route;
import com.eskisehir.eventapi.domain.model.RouteRating;
import com.eskisehir.eventapi.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRatingRepository extends JpaRepository<RouteRating, Long> {

    /**
     * Find all ratings for a specific route, ordered by most recent.
     */
    List<RouteRating> findByRouteIdOrderByCreatedAtDesc(Long routeId);

    /**
     * Find a user's rating for a specific route.
     */
    Optional<RouteRating> findByRouteIdAndUserId(Long routeId, Long userId);

    /**
     * Count total ratings for a route.
     */
    Long countByRouteId(Long routeId);

    /**
     * Get average rating for a route.
     */
    @Query("SELECT AVG(r.rating) FROM RouteRating r WHERE r.route.id = :routeId")
    Double getAverageRatingByRouteId(@Param("routeId") Long routeId);

    /**
     * Find highest-rated routes (trending).
     */
    @Query("SELECT r.route FROM RouteRating r WHERE r.route.isPublic = true " +
           "GROUP BY r.route.id " +
           "HAVING COUNT(r) > 0 " +
           "ORDER BY AVG(r.rating) DESC LIMIT :limit")
    List<Route> findTrendingRoutes(@Param("limit") Integer limit);
}
