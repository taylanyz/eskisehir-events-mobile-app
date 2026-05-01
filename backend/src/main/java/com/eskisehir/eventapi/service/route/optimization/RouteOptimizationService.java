package com.eskisehir.eventapi.service.route.optimization;

import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.domain.model.User;
import com.eskisehir.eventapi.dto.OptimizedRoute;
import com.eskisehir.eventapi.dto.RouteMetrics;
import com.eskisehir.eventapi.dto.RouteOptimizationRequest;
import com.eskisehir.eventapi.dto.RouteScoreBreakdown;
import com.eskisehir.eventapi.repository.PoiRepository;
import com.eskisehir.eventapi.repository.UserRepository;
import com.eskisehir.eventapi.service.route.optimization.RouteCalculationService.RouteCalculationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main orchestrator for multi-criteria route optimization.
 * Coordinates candidate loading, weight adaptation, normalization, optimization, and explanation.
 */
@Service
@Transactional(readOnly = true)
public class RouteOptimizationService {
    
    private static final Logger logger = LoggerFactory.getLogger(RouteOptimizationService.class);
    private static final String FALLBACK_TRANSPORT_MODE = "public_transport";
    
    private final UserRepository userRepository;
    private final PoiRepository poiRepository;
    private final RouteOptimizationWeightAdapter weightAdapter;
    private final RouteCalculationService calculationService;
    private final RouteNormalizationService normalizationService;
    private final GreedyRouteOptimizer greedyOptimizer;
    private final RouteExplainer explainer;
    
    public RouteOptimizationService(
        UserRepository userRepository,
        PoiRepository poiRepository,
        RouteOptimizationWeightAdapter weightAdapter,
        RouteCalculationService calculationService,
        RouteNormalizationService normalizationService,
        GreedyRouteOptimizer greedyOptimizer,
        RouteExplainer explainer
    ) {
        this.userRepository = userRepository;
        this.poiRepository = poiRepository;
        this.weightAdapter = weightAdapter;
        this.calculationService = calculationService;
        this.normalizationService = normalizationService;
        this.greedyOptimizer = greedyOptimizer;
        this.explainer = explainer;
    }
    
    /**
     * Main entry point for route optimization.
     * Orchestrates full workflow: load data, adapt weights, optimize, score, explain.
     */
    public OptimizedRoute optimizeRoute(RouteOptimizationRequest request) {
        try {
            logger.info("Starting route optimization for user {}", request.getUserId());
            
            // Step 1: Load user and validate
            User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));
            
            // Step 2: Load candidate POIs
            List<Poi> candidatePois = poiRepository.findAllById(request.getCandidatePoiIds());
            if (candidatePois.isEmpty()) {
                throw new IllegalArgumentException("No POIs found for given IDs");
            }
            
            logger.debug("Loaded {} candidate POIs for optimization", candidatePois.size());
            
            // Step 3: Adapt weights based on user profile and context
            Map<String, Double> finalWeights = weightAdapter.computeFinalWeights(user, request);
            logger.debug("Computed final weights: {}", finalWeights);
            
            // Step 4: Run optimizer (fallback to greedy if needed)
            String transportMode = request.getTransportMode() != null ?
                request.getTransportMode() : FALLBACK_TRANSPORT_MODE;
            
            List<Poi> optimizedSequence = greedyOptimizer.optimizeRoute(
                request.getOriginLat(),
                request.getOriginLng(),
                candidatePois,
                request.getTimeBudgetMinutes(),
                request.getDistanceBudgetKm(),
                request.getMonetaryBudgetTl(),
                transportMode
            );
            
            if (optimizedSequence.isEmpty()) {
                logger.warn("Optimizer returned empty route; returning fallback");
                return createFallbackRoute(request);
            }
            
            logger.debug("Optimization produced route with {} POIs", optimizedSequence.size());
            
            // Step 5: Calculate metrics for optimized route
            RouteCalculationResult metrics = calculationService.calculateRouteMetrics(
                request.getOriginLat(),
                request.getOriginLng(),
                optimizedSequence,
                transportMode
            );
            
            // Step 6: Compute preference scores for each POI
            Map<Long, Double> poiPreferenceScores = computePoiPreferenceScores(optimizedSequence, user);
            
            // Step 7: Normalize all objectives
            Map<String, Double> objectives = normalizationService.computeAllObjectives(
                optimizedSequence,
                metrics,
                request,
                poiPreferenceScores
            );
            
            // Step 8: Compute composite score
            Double compositeScore = normalizationService.computeCompositeScore(finalWeights, objectives);
            
            // Step 9: Build score breakdown
            RouteScoreBreakdown scores = new RouteScoreBreakdown(
                objectives.get("preference_fit"),
                objectives.get("crowd_avoidance"),
                objectives.get("budget_efficiency"),
                objectives.get("sustainability"),
                objectives.get("local_support"),
                objectives.get("diversity"),
                compositeScore
            );
            
            // Step 10: Count local POIs and build metrics
            int localPoiCount = (int) optimizedSequence.stream()
                .filter(p -> p.getLocalBusinessScore() != null && p.getLocalBusinessScore() > 0.5)
                .count();
            
            RouteMetrics routeMetrics = new RouteMetrics(
                metrics.getTotalDistanceKm(),
                (int) metrics.getTotalDurationMinutes(),
                metrics.getTotalCostTl(),
                metrics.getCo2EmissionsKg(),
                localPoiCount,
                optimizedSequence.size()
            );
            
            // Step 11: Generate explanation
            double budgetBuffer = (request.getMonetaryBudgetTl() != null) ?
                (request.getMonetaryBudgetTl() - metrics.getTotalCostTl()) : 0;
            
            String explanation = explainer.generateExplanation(
                scores,
                optimizedSequence.size(),
                metrics.getTotalDistanceKm(),
                metrics.getTotalDurationMinutes(),
                metrics.getTotalCostTl(),
                metrics.getCo2EmissionsKg(),
                localPoiCount,
                budgetBuffer
            );
            
            // Step 12: Build and return OptimizedRoute
            OptimizedRoute route = buildOptimizedRoute(
                request,
                optimizedSequence,
                routeMetrics,
                scores,
                explanation
            );
            
            logger.info("Route optimization completed successfully for user {}", request.getUserId());
            return route;
            
        } catch (Exception e) {
            logger.error("Route optimization failed", e);
            throw new RuntimeException("Route optimization failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Computes preference fit score for each POI based on user profile.
     * Mock implementation; in real scenario would call Phase 9 feature extraction.
     */
    private Map<Long, Double> computePoiPreferenceScores(List<Poi> pois, User user) {
        Map<Long, Double> scores = new HashMap<>();
        
        // Mock: assign random preference scores in [0, 1]
        // In real implementation: use Phase 9 UserFeatureExtractor + PoiFeatureExtractor
        for (Poi poi : pois) {
            // For now, use POI popularity as proxy
            Double popularity = poi.getPopularityScore() != null ? poi.getPopularityScore() : 0.5;
            scores.put(poi.getId(), Math.min(1.0, popularity * 1.1));
        }
        
        return scores;
    }
    
    /**
     * Builds OptimizedRoute response object.
     */
    private OptimizedRoute buildOptimizedRoute(
        RouteOptimizationRequest request,
        List<Poi> poiSequence,
        RouteMetrics metrics,
        RouteScoreBreakdown scores,
        String explanation
    ) {
        String routeId = "route_" + UUID.randomUUID().toString().substring(0, 8);
        
        OptimizedRoute.RouteLocation origin = new OptimizedRoute.RouteLocation(
            "İlk Konum",
            request.getOriginLat(),
            request.getOriginLng()
        );
        
        List<OptimizedRoute.RouteStop> stops = new ArrayList<>();
        for (int i = 0; i < poiSequence.size(); i++) {
            Poi poi = poiSequence.get(i);
            OptimizedRoute.RouteStop stop = new OptimizedRoute.RouteStop(
                poi.getId(),
                poi.getName(),
                i + 1,
                poi.getEstimatedVisitMinutes() != null ?
                    poi.getEstimatedVisitMinutes() : 60,
                poi.getPrice() != null ? poi.getPrice() : 0.0,
                poi.getCrowdProxy() != null ? poi.getCrowdProxy() : 0.5,
                poi.getCategory() != null ? poi.getCategory().toString() : "Other"
            );
            stops.add(stop);
        }
        
        return new OptimizedRoute(
            routeId,
            origin,
            stops,
            metrics,
            scores,
            explanation
        );
    }
    
    /**
     * Fallback route creation when optimization fails.
     * Returns simple "best available" route.
     */
    private OptimizedRoute createFallbackRoute(RouteOptimizationRequest request) {
        logger.warn("Creating fallback route for user {}", request.getUserId());
        
        String routeId = "route_fallback_" + UUID.randomUUID().toString().substring(0, 8);
        
        OptimizedRoute.RouteLocation origin = new OptimizedRoute.RouteLocation(
            "İlk Konum",
            request.getOriginLat(),
            request.getOriginLng()
        );
        
        // Fallback: return first POI from candidate list
        List<Poi> candidatePois = poiRepository.findAllById(request.getCandidatePoiIds());
        List<OptimizedRoute.RouteStop> stops = new ArrayList<>();
        
        if (!candidatePois.isEmpty()) {
            Poi poi = candidatePois.get(0);
            OptimizedRoute.RouteStop stop = new OptimizedRoute.RouteStop(
                poi.getId(),
                poi.getName(),
                1,
                poi.getEstimatedVisitMinutes() != null ?
                    poi.getEstimatedVisitMinutes() : 60,
                poi.getPrice() != null ? poi.getPrice() : 0.0,
                poi.getCrowdProxy() != null ? poi.getCrowdProxy() : 0.5,
                poi.getCategory() != null ? poi.getCategory().toString() : "Other"
            );
            stops.add(stop);
        }
        
        RouteMetrics metrics = new RouteMetrics(
            5.0, 90, 50.0, 0.75, 1, 1
        );
        
        RouteScoreBreakdown scores = new RouteScoreBreakdown(
            0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5
        );
        
        String explanation = "Hazır rota oluşturuldu. (Quick fallback route created.)";
        
        return new OptimizedRoute(routeId, origin, stops, metrics, scores, explanation);
    }
}
