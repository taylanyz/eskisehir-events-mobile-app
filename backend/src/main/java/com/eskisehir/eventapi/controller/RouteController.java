package com.eskisehir.eventapi.controller;

import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.domain.model.Route;
import com.eskisehir.eventapi.dto.RouteRequest;
import com.eskisehir.eventapi.dto.RouteResponse;
import com.eskisehir.eventapi.dto.PoiResponse;
import com.eskisehir.eventapi.dto.TurnByTurnNavigationResponse;
import com.eskisehir.eventapi.dto.RouteRatingRequest;
import com.eskisehir.eventapi.dto.RouteRatingResponse;
import com.eskisehir.eventapi.repository.PoiRepository;
import com.eskisehir.eventapi.repository.RouteRepository;
import com.eskisehir.eventapi.service.RoutePlanner;
import com.eskisehir.eventapi.service.RouteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Route planning endpoint for Phase 4.
 * Provides route generation using nearest-neighbor + constraint satisfaction.
 * Phase 5.2: Adds social features (sharing, ratings, trending).
 */
@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private static final Logger log = LoggerFactory.getLogger(RouteController.class);

    private final RoutePlanner routePlanner;
    private final PoiRepository poiRepository;
    private final RouteRepository routeRepository;
    private final RouteService routeService;

    public RouteController(RoutePlanner routePlanner, PoiRepository poiRepository, 
                          RouteRepository routeRepository, RouteService routeService) {
        this.routePlanner = routePlanner;
        this.poiRepository = poiRepository;
        this.routeRepository = routeRepository;
        this.routeService = routeService;
    }

    /**
     * Generate an optimized route based on request constraints.
     * POST /api/routes/generate
     * 
     * @param request contains POI IDs, start location, duration, budget, constraints
     * @return optimized route with POIs ordered and metadata (distance, time, cost)
     */
    @PostMapping("/generate")
    public ResponseEntity<RouteResponse> generateRoute(@RequestBody RouteRequest request) {
        log.info("Generating route for {} POIs with constraints", 
                request.getEventIds() != null ? request.getEventIds().size() : 0);

        // Resolve POIs from event IDs
        List<Poi> pois = resolvePois(request.getEventIds());
        if (pois.isEmpty()) {
            log.warn("No valid POIs found for route generation");
            return ResponseEntity.badRequest().build();
        }

        // Get start location (default to Eskişehir center if not provided)
        double startLat = request.getStartLatitude() != null ? request.getStartLatitude() : 39.7667;
        double startLng = request.getStartLongitude() != null ? request.getStartLongitude() : 30.5256;

        // Plan nearest-neighbor route
        List<Poi> orderedRoute = routePlanner.planNearestNeighbor(startLat, startLng, pois);

        // Calculate route metrics
        double totalDistanceKm = routePlanner.calculateTotalDistance(startLat, startLng, orderedRoute);
        int totalWalkingMinutes = routePlanner.calculateTotalWalkingTime(startLat, startLng, orderedRoute);
        double estimatedCostTRY = calculateEstimatedCost(orderedRoute);

        // Check feasibility
        String status = "FEASIBLE";
        if (request.getMaxWalkingMinutes() != null && totalWalkingMinutes > request.getMaxWalkingMinutes()) {
            status = "PARTIAL";
            log.warn("Route exceeds max walking time: {} > {}", totalWalkingMinutes, request.getMaxWalkingMinutes());
        }
        if (request.getMaxBudget() != null && estimatedCostTRY > request.getMaxBudget()) {
            status = "PARTIAL";
            log.warn("Route exceeds max budget: {} > {}", estimatedCostTRY, request.getMaxBudget());
        }

        // Convert to response DTOs
        List<PoiResponse> responseList = orderedRoute.stream()
                .map(PoiResponse::fromEntity)
                .collect(Collectors.toList());

        RouteResponse response = new RouteResponse(responseList, totalDistanceKm, 
                totalWalkingMinutes, estimatedCostTRY, status);

        log.info("Route generated: {} POIs, {} km, {} min, {} TRY, status: {}",
                orderedRoute.size(), String.format("%.2f", totalDistanceKm), totalWalkingMinutes,
                String.format("%.2f", estimatedCostTRY), status);

        return ResponseEntity.ok(response);
    }

    /**
     * Get turn-by-turn navigation directions for a route.
     * GET /api/routes/directions
     *
     * @param eventIds comma-separated list of POI IDs in order
     * @param startLatitude start latitude
     * @param startLongitude start longitude
     * @return step-by-step navigation directions
     */
    @GetMapping("/directions")
    public ResponseEntity<TurnByTurnNavigationResponse> getTurnByTurnDirections(
            @RequestParam String eventIds,
            @RequestParam(required = false) Double startLatitude,
            @RequestParam(required = false) Double startLongitude) {
        
        log.info("Generating turn-by-turn directions for route: {}", eventIds);

        // Parse event IDs
        List<Long> poiIds = new ArrayList<>();
        for (String id : eventIds.split(",")) {
            try {
                poiIds.add(Long.parseLong(id.trim()));
            } catch (NumberFormatException e) {
                log.error("Invalid POI ID: {}", id);
            }
        }

        // Resolve POIs
        List<Poi> route = resolvePois(poiIds);
        if (route.isEmpty()) {
            log.warn("No valid POIs found for directions");
            return ResponseEntity.badRequest().build();
        }

        // Get start location
        double startLat = startLatitude != null ? startLatitude : 39.7667;
        double startLng = startLongitude != null ? startLongitude : 30.5256;

        // Generate navigation steps
        List<TurnByTurnNavigationResponse.NavigationStepDto> steps = new ArrayList<>();
        double currentLat = startLat;
        double currentLng = startLng;
        int stepNumber = 1;

        for (Poi poi : route) {
            double distance = routePlanner.getGeoService().getDistanceKm(currentLat, currentLng, poi.getLatitude(), poi.getLongitude());
            int duration = routePlanner.getGeoService().getWalkingTimeMinutes(distance);

            String instruction = String.format("Walk to %s (%.1f km, ~%d min)", poi.getName(), distance, duration);

            TurnByTurnNavigationResponse.NavigationStepDto step = 
                    new TurnByTurnNavigationResponse.NavigationStepDto(
                            stepNumber,
                            null,  // fromPoiId (null for starting point)
                            poi.getId(),
                            null,  // fromPoiName
                            poi.getName(),
                            instruction,
                            distance,
                            duration,
                            currentLat,
                            currentLng,
                            poi.getLatitude(),
                            poi.getLongitude()
                    );
            steps.add(step);

            currentLat = poi.getLatitude();
            currentLng = poi.getLongitude();
            stepNumber++;
        }

        // Calculate totals
        double totalDistance = routePlanner.calculateTotalDistance(startLat, startLng, route);
        int totalDuration = routePlanner.calculateTotalWalkingTime(startLat, startLng, route);

        TurnByTurnNavigationResponse response = new TurnByTurnNavigationResponse(
                null,  // routeId (can be assigned if routes are persisted)
                steps,
                totalDistance,
                totalDuration,
                0  // currentStepIndex starts at 0
        );

        log.info("Turn-by-turn directions generated: {} steps, {} km, {} min", 
                steps.size(), String.format("%.2f", totalDistance), totalDuration);

        return ResponseEntity.ok(response);
    }

    /**
     * Share a route by making it public.
     * PUT /api/routes/{routeId}/share
     */
    @PutMapping("/{routeId}/share")
    public ResponseEntity<Void> shareRoute(@PathVariable Long routeId) {
        log.info("Sharing route: {}", routeId);
        Route route = routeService.shareRoute(routeId);
        routeService.incrementRouteShare(routeId);
        return ResponseEntity.ok().build();
    }

    /**
     * Unshare a route by making it private.
     * PUT /api/routes/{routeId}/unshare
     */
    @PutMapping("/{routeId}/unshare")
    public ResponseEntity<Void> unshareRoute(@PathVariable Long routeId) {
        log.info("Unsharing route: {}", routeId);
        routeService.unshareRoute(routeId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get a public route by share code.
     * GET /api/routes/share/{shareCode}
     */
    @GetMapping("/share/{shareCode}")
    public ResponseEntity<RouteResponse> getSharedRoute(@PathVariable String shareCode) {
        log.info("Retrieving shared route: {}", shareCode);
        Optional<Route> route = routeService.getRouteByShareCode(shareCode);
        if (route.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // Convert route to response (would need RouteResponse implementation)
        return ResponseEntity.ok().build();
    }

    /**
     * Rate a route.
     * POST /api/routes/{routeId}/ratings
     */
    @PostMapping("/{routeId}/ratings")
    public ResponseEntity<RouteRatingResponse> rateRoute(
            @PathVariable Long routeId,
            @RequestParam Long userId,
            @RequestBody RouteRatingRequest request) {
        log.info("Adding rating for route {}", routeId);
        RouteRatingResponse response = routeService.rateRoute(routeId, userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all ratings for a route.
     * GET /api/routes/{routeId}/ratings
     */
    @GetMapping("/{routeId}/ratings")
    public ResponseEntity<List<RouteRatingResponse>> getRouteRatings(@PathVariable Long routeId) {
        log.info("Retrieving ratings for route: {}", routeId);
        List<RouteRatingResponse> ratings = routeService.getRouteRatings(routeId);
        return ResponseEntity.ok(ratings);
    }

    /**
     * Get trending routes.
     * GET /api/routes/trending
     */
    @GetMapping("/trending")
    public ResponseEntity<List<Route>> getTrendingRoutes(
            @RequestParam(defaultValue = "10") Integer limit) {
        log.info("Retrieving {} trending routes", limit);
        List<Route> trendingRoutes = routeService.getTrendingRoutes(limit);
        return ResponseEntity.ok(trendingRoutes);
    }

    /**
     * Get public routes (most recent).
     * GET /api/routes/public
     */
    @GetMapping("/public")
    public ResponseEntity<List<Route>> getPublicRoutes(
            @RequestParam(defaultValue = "20") Integer limit) {
        log.info("Retrieving {} public routes", limit);
        List<Route> publicRoutes = routeService.getPublicRoutes(limit);
        return ResponseEntity.ok(publicRoutes);
    }

    /**
     * Resolve list of POI IDs to actual POI entities.
     */
    private List<Poi> resolvePois(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return List.of();
        }

        return eventIds.stream()
                .map(poiRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Estimate total cost of visiting all POIs in route.
     * Simple sum of individual POI prices.
     */
    private double calculateEstimatedCost(List<Poi> route) {
        return route.stream()
                .mapToDouble(poi -> poi.getPrice() != null ? poi.getPrice() : 0.0)
                .sum();
    }
}
