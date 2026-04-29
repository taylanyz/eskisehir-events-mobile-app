package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.domain.model.Route;
import com.eskisehir.eventapi.domain.model.RouteRating;
import com.eskisehir.eventapi.domain.model.User;
import com.eskisehir.eventapi.dto.RouteRatingRequest;
import com.eskisehir.eventapi.dto.RouteRatingResponse;
import com.eskisehir.eventapi.repository.RouteRepository;
import com.eskisehir.eventapi.repository.RouteRatingRepository;
import com.eskisehir.eventapi.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RouteService {

    private final RouteRepository routeRepository;
    private final RouteRatingRepository ratingRepository;
    private final UserRepository userRepository;

    public RouteService(RouteRepository routeRepository, RouteRatingRepository ratingRepository, UserRepository userRepository) {
        this.routeRepository = routeRepository;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
    }

    /**
     * Make a route public and generate a share code.
     */
    public Route shareRoute(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route not found"));
        
        route.setIsPublic(true);
        if (route.getShareCode() == null) {
            route.generateShareCode();
        }
        return routeRepository.save(route);
    }

    /**
     * Make a route private (no longer shared).
     */
    public Route unshareRoute(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route not found"));
        route.setIsPublic(false);
        return routeRepository.save(route);
    }

    /**
     * Get a public route by share code.
     */
    public Optional<Route> getRouteByShareCode(String shareCode) {
        return routeRepository.findByShareCodeAndIsPublicTrue(shareCode);
    }

    /**
     * Increment share count for a route.
     */
    public void incrementRouteShare(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route not found"));
        route.incrementShareCount();
        routeRepository.save(route);
    }

    /**
     * Add or update a rating for a route.
     */
    public RouteRatingResponse rateRoute(Long routeId, Long userId, RouteRatingRequest request) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if user already rated this route
        Optional<RouteRating> existingRating = ratingRepository.findByRouteIdAndUserId(routeId, userId);

        RouteRating rating;
        if (existingRating.isPresent()) {
            // Update existing rating
            rating = existingRating.get();
            Double oldRating = rating.getRating();
            rating.setRating(request.getRating());
            rating.setComment(request.getComment());
            
            // Update route's average rating
            if (!oldRating.equals(request.getRating())) {
                updateRouteAverageRating(route);
            }
        } else {
            // Create new rating
            rating = new RouteRating(route, user, request.getRating(), request.getComment());
            route.addRating(request.getRating());
        }

        rating = ratingRepository.save(rating);
        route = routeRepository.save(route);
        
        return new RouteRatingResponse(rating);
    }

    /**
     * Get all ratings for a route.
     */
    public List<RouteRatingResponse> getRouteRatings(Long routeId) {
        return ratingRepository.findByRouteIdOrderByCreatedAtDesc(routeId).stream()
                .map(RouteRatingResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Get trending routes based on ratings and shares.
     */
    public List<Route> getTrendingRoutes(Integer limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return routeRepository.findTrendingRoutes(pageable);
    }

    /**
     * Get public routes (most recent).
     */
    public List<Route> getPublicRoutes(Integer limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return routeRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);
    }

    /**
     * Recalculate and update route's average rating from all ratings.
     */
    private void updateRouteAverageRating(Route route) {
        Double avgRating = ratingRepository.getAverageRatingByRouteId(route.getId());
        Long count = ratingRepository.countByRouteId(route.getId());
        
        if (avgRating != null && count > 0) {
            route.setAverageRating(avgRating);
            route.setTotalRatings(count.intValue());
        }
    }
}
