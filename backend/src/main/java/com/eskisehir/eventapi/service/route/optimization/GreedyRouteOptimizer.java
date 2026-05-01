package com.eskisehir.eventapi.service.route.optimization;

import com.eskisehir.eventapi.domain.model.Poi;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Fallback heuristic optimizer: Nearest Neighbor construction + 2-opt local search.
 * Used when OR-Tools unavailable or needs quick response.
 */
@Service
public class GreedyRouteOptimizer {
    
    private final RouteCalculationService calculationService;
    private static final long TIMEOUT_MS = 500L;  // 500ms hard timeout
    
    public GreedyRouteOptimizer(RouteCalculationService calculationService) {
        this.calculationService = calculationService;
    }
    
    /**
     * Constructs and optimizes route using nearest neighbor + 2-opt.
     */
    public List<Poi> optimizeRoute(
        Double originLat,
        Double originLng,
        List<Poi> candidatePois,
        Integer timeBudgetMinutes,
        Double distanceBudgetKm,
        Double monetaryBudgetTl,
        String transportMode
    ) {
        long startTime = System.currentTimeMillis();
        
        // Step 1: Nearest Neighbor construction
        List<Poi> route = greedyNearestNeighbor(
            originLat, originLng, candidatePois,
            timeBudgetMinutes, distanceBudgetKm, monetaryBudgetTl,
            transportMode
        );
        
        // Step 2: 2-opt local search (if time permits)
        if (System.currentTimeMillis() - startTime < TIMEOUT_MS / 2) {
            route = localSearch2Opt(
                route, originLat, originLng,
                timeBudgetMinutes, distanceBudgetKm, monetaryBudgetTl,
                transportMode,
                startTime
            );
        }
        
        return route;
    }
    
    /**
     * Nearest Neighbor construction: Start at origin, greedily add nearest unvisited POI within budget.
     */
    private List<Poi> greedyNearestNeighbor(
        Double originLat,
        Double originLng,
        List<Poi> candidatePois,
        Integer timeBudgetMinutes,
        Double distanceBudgetKm,
        Double monetaryBudgetTl,
        String transportMode
    ) {
        List<Poi> route = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        
        Double currentLat = originLat;
        Double currentLng = originLng;
        double remainingTime = timeBudgetMinutes != null ? timeBudgetMinutes : 240;
        double remainingDistance = distanceBudgetKm != null ? distanceBudgetKm : 20;
        double remainingBudget = monetaryBudgetTl != null ? monetaryBudgetTl : 500;
        
        // Greedy construction
        while (visited.size() < candidatePois.size()) {
            Poi bestPoi = null;
            double bestDistance = Double.MAX_VALUE;
            
            // Find nearest unvisited POI within budget
            for (Poi poi : candidatePois) {
                if (visited.contains(poi.getId())) {
                    continue;  // Already visited
                }
                
                double distanceToPoi = calculationService.calculateHaversineDistance(
                    currentLat, currentLng, poi.getLatitude(), poi.getLongitude()
                );
                
                // Check if POI is within budget
                double travelTime = calculationService.calculateTravelTime(distanceToPoi, transportMode);
                Integer visitDuration = poi.getEstimatedVisitMinutes();
                if (visitDuration == null) visitDuration = 60;
                double totalTime = travelTime + visitDuration + 5;  // +5 min overhead
                
                Double cost = poi.getPrice() != null ? poi.getPrice() : 0.0;
                
                if (distanceToPoi <= remainingDistance &&
                    totalTime <= remainingTime &&
                    cost <= remainingBudget &&
                    distanceToPoi < bestDistance) {
                    
                    bestPoi = poi;
                    bestDistance = distanceToPoi;
                }
            }
            
            if (bestPoi == null) {
                break;  // No more POIs can be added within budget
            }
            
            // Add best POI to route
            route.add(bestPoi);
            visited.add(bestPoi.getId());
            
            // Update remaining budget
            double travelTime = calculationService.calculateTravelTime(bestDistance, transportMode);
            Integer visitDuration = bestPoi.getEstimatedVisitMinutes();
            if (visitDuration == null) visitDuration = 60;
            remainingTime -= (travelTime + visitDuration + 5);
            remainingDistance -= bestDistance;
            Double cost = bestPoi.getPrice();
            if (cost != null) {
                remainingBudget -= cost;
            }
            
            // Update current position
            currentLat = bestPoi.getLatitude();
            currentLng = bestPoi.getLongitude();
        }
        
        return route;
    }
    
    /**
     * 2-opt local search: Repeatedly try reversing route segments to improve objective.
     */
    private List<Poi> localSearch2Opt(
        List<Poi> route,
        Double originLat,
        Double originLng,
        Integer timeBudgetMinutes,
        Double distanceBudgetKm,
        Double monetaryBudgetTl,
        String transportMode,
        long startTime
    ) {
        List<Poi> bestRoute = new ArrayList<>(route);
        boolean improved = true;
        
        while (improved && System.currentTimeMillis() - startTime < TIMEOUT_MS) {
            improved = false;
            
            for (int i = 0; i < bestRoute.size() - 1; i++) {
                for (int j = i + 2; j < bestRoute.size(); j++) {
                    if (System.currentTimeMillis() - startTime > TIMEOUT_MS) {
                        return bestRoute;  // Timeout
                    }
                    
                    // Try reversing segment [i+1, j]
                    List<Poi> candidate = new ArrayList<>(bestRoute);
                    Collections.reverse(candidate.subList(i + 1, j + 1));
                    
                    // Check if candidate is better and feasible
                    RouteCalculationService.RouteCalculationResult candidateMetrics =
                        calculationService.calculateRouteMetrics(originLat, originLng, candidate, transportMode);
                    
                    if (calculationService.isRouteValid(candidateMetrics, timeBudgetMinutes, distanceBudgetKm, monetaryBudgetTl)) {
                        // Candidate is feasible; accept if distance improved
                        RouteCalculationService.RouteCalculationResult currentMetrics =
                            calculationService.calculateRouteMetrics(originLat, originLng, bestRoute, transportMode);
                        
                        if (candidateMetrics.getTotalDistanceKm() < currentMetrics.getTotalDistanceKm()) {
                            bestRoute = candidate;
                            improved = true;
                            break;  // Start over from beginning
                        }
                    }
                }
                if (improved) break;
            }
        }
        
        return bestRoute;
    }
}
