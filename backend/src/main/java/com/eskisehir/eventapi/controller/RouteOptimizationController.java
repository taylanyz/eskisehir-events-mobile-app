package com.eskisehir.eventapi.controller;

import com.eskisehir.eventapi.dto.OptimizedRoute;
import com.eskisehir.eventapi.dto.RouteOptimizationRequest;
import com.eskisehir.eventapi.service.route.optimization.RouteOptimizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * REST Controller for route optimization endpoints.
 */
@RestController
@RequestMapping("/api/routes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RouteOptimizationController {
    
    private static final Logger logger = LoggerFactory.getLogger(RouteOptimizationController.class);
    
    private final RouteOptimizationService routeOptimizationService;
    
    public RouteOptimizationController(RouteOptimizationService routeOptimizationService) {
        this.routeOptimizationService = routeOptimizationService;
    }
    
    /**
     * POST /api/routes/optimize
     * 
     * Generates an optimized route based on user preferences and constraints.
     * 
     * Request body:
     * {
     *   "user_id": 123,
     *   "origin_lat": 39.77,
     *   "origin_lng": 30.53,
     *   "candidate_poi_ids": [1, 2, 3, ...],
     *   "time_budget_minutes": 240,
     *   "distance_budget_km": 20,
     *   "monetary_budget_tl": 500,
     *   "transport_mode": "public_transport",
     *   "weather": "sunny",
     *   "current_time": "14:30",
     *   "day_of_week": "Thursday",
     *   "weights_override": {"w_sustainability": 0.20}
     * }
     * 
     * Response (200 OK):
     * {
     *   "route_id": "route_abc123",
     *   "origin": {"name": "Başak Caddesi", "lat": 39.77, "lng": 30.53},
     *   "pois": [
     *     {"id": 1, "name": "Museum", "order": 1, "visit_duration_minutes": 60, "cost_tl": 50, "crowd_level": 0.6, "category": "Culture"}
     *   ],
     *   "metrics": {
     *     "total_distance_km": 8.5,
     *     "total_duration_minutes": 195,
     *     "total_cost_tl": 70,
     *     "co2_emissions_kg": 1.2,
     *     "local_poi_count": 1,
     *     "total_poi_count": 2
     *   },
     *   "scores": {
     *     "preference_fit": 0.82,
     *     "crowd_avoidance": 0.75,
     *     "budget_efficiency": 0.65,
     *     "sustainability": 0.88,
     *     "local_support": 0.5,
     *     "diversity": 0.7,
     *     "final_composite_score": 0.72
     *   },
     *   "explanation": "Harika bir rota! ..."
     * }
     */
    @PostMapping("/optimize")
    public ResponseEntity<OptimizedRoute> optimizeRoute(
        @Valid @RequestBody RouteOptimizationRequest request
    ) {
        logger.info("Received route optimization request for user {}", request.getUserId());
        
        try {
            OptimizedRoute optimizedRoute = routeOptimizationService.optimizeRoute(request);
            logger.info("Route optimization successful: route_id={}", optimizedRoute.getRouteId());
            return ResponseEntity.ok(optimizedRoute);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            logger.error("Route optimization error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/routes/health
     * 
     * Simple health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Route Optimization Service is running");
    }
}
