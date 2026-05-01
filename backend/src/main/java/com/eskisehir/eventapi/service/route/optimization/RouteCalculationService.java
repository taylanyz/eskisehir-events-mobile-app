package com.eskisehir.eventapi.service.route.optimization;

import com.eskisehir.eventapi.domain.model.Poi;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Utility for route calculations: distance, duration, cost, carbon emissions.
 */
@Service
public class RouteCalculationService {
    
    // Constants for calculation
    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double TRANSITION_OVERHEAD_MINUTES = 5.0;
    private static final double DEFAULT_VISIT_DURATION_MINUTES = 60.0;
    private static final double CARBON_FACTOR_KG_PER_KM = 0.15;  // Average carbon per km
    
    // Transport mode speeds (km/h)
    private static final double WALKING_SPEED_KMH = 5.0;
    private static final double PUBLIC_TRANSPORT_SPEED_KMH = 15.0;
    private static final double DRIVING_SPEED_KMH = 30.0;
    
    /**
     * Calculates Haversine distance between two coordinates (km).
     */
    public double calculateHaversineDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        if (lat1 == null || lng1 == null || lat2 == null || lng2 == null) {
            return 0.0;
        }
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
    
    /**
     * Calculates travel time (minutes) for a given distance and transport mode.
     */
    public double calculateTravelTime(double distanceKm, String transportMode) {
        double speedKmh = getTransportModeSpeed(transportMode);
        return (distanceKm / speedKmh) * 60.0;  // Convert hours to minutes
    }
    
    /**
     * Calculates total route metrics for a sequence of POIs.
     */
    public RouteCalculationResult calculateRouteMetrics(
        Double originLat,
        Double originLng,
        List<Poi> poiSequence,
        String transportMode
    ) {
        double totalDistance = 0.0;
        double totalDuration = 0.0;
        double totalCost = 0.0;
        int visitCount = 0;
        
        // Start from origin
        Double currentLat = originLat;
        Double currentLng = originLng;
        
        for (Poi poi : poiSequence) {
            // Distance from current location to POI
            double distanceToNextPoi = calculateHaversineDistance(currentLat, currentLng, 
                                                                  poi.getLatitude(), poi.getLongitude());
            totalDistance += distanceToNextPoi;
            
            // Travel time
            double travelTime = calculateTravelTime(distanceToNextPoi, transportMode);
            totalDuration += travelTime;
            
            // Visit duration
            Integer visitDuration = poi.getEstimatedVisitMinutes();
            if (visitDuration == null) {
                visitDuration = (int) DEFAULT_VISIT_DURATION_MINUTES;
            }
            totalDuration += visitDuration;
            
            // Transition overhead
            if (visitCount < poiSequence.size() - 1) {
                totalDuration += TRANSITION_OVERHEAD_MINUTES;
            }
            
            // Cost
            Double cost = poi.getPrice();
            if (cost != null) {
                totalCost += cost;
            }
            
            // Update current location
            currentLat = poi.getLatitude();
            currentLng = poi.getLongitude();
            visitCount++;
        }
        
        // Carbon emissions: distance * factor
        double co2Emissions = totalDistance * getCarbonFactor(transportMode);
        
        return new RouteCalculationResult(totalDistance, totalDuration, totalCost, co2Emissions, visitCount);
    }
    
    /**
     * Distance matrix between all pairs of POIs (including origin).
     */
    public double[][] buildDistanceMatrix(Double originLat, Double originLng, List<Poi> pois) {
        int n = pois.size() + 1;  // +1 for origin (index 0)
        double[][] matrix = new double[n][n];
        
        // Origin to POIs and back
        for (int i = 0; i < pois.size(); i++) {
            Poi poi = pois.get(i);
            matrix[0][i + 1] = calculateHaversineDistance(originLat, originLng, 
                                                          poi.getLatitude(), poi.getLongitude());
            matrix[i + 1][0] = matrix[0][i + 1];  // Symmetric
        }
        
        // POI to POI distances
        for (int i = 0; i < pois.size(); i++) {
            for (int j = i + 1; j < pois.size(); j++) {
                Poi poi1 = pois.get(i);
                Poi poi2 = pois.get(j);
                double dist = calculateHaversineDistance(poi1.getLatitude(), poi1.getLongitude(),
                                                        poi2.getLatitude(), poi2.getLongitude());
                matrix[i + 1][j + 1] = dist;
                matrix[j + 1][i + 1] = dist;  // Symmetric
            }
        }
        
        return matrix;
    }
    
    /**
     * Duration matrix between all pairs of POIs (travel time only, no visit duration).
     */
    public double[][] buildDurationMatrix(Double originLat, Double originLng, List<Poi> pois, String transportMode) {
        double[][] distMatrix = buildDistanceMatrix(originLat, originLng, pois);
        double[][] durationMatrix = new double[distMatrix.length][distMatrix[0].length];
        
        for (int i = 0; i < distMatrix.length; i++) {
            for (int j = 0; j < distMatrix[i].length; j++) {
                durationMatrix[i][j] = calculateTravelTime(distMatrix[i][j], transportMode);
            }
        }
        
        return durationMatrix;
    }
    
    /**
     * Checks if route violates hard constraints.
     */
    public boolean isRouteValid(
        RouteCalculationResult metrics,
        Integer timeBudgetMinutes,
        Double distanceBudgetKm,
        Double monetaryBudgetTl
    ) {
        if (timeBudgetMinutes != null && metrics.getTotalDurationMinutes() > timeBudgetMinutes) {
            return false;
        }
        if (distanceBudgetKm != null && metrics.getTotalDistanceKm() > distanceBudgetKm) {
            return false;
        }
        if (monetaryBudgetTl != null && metrics.getTotalCostTl() > monetaryBudgetTl) {
            return false;
        }
        return true;
    }
    
    /**
     * Gets transport mode speed (km/h).
     */
    private double getTransportModeSpeed(String transportMode) {
        if ("walking".equalsIgnoreCase(transportMode)) {
            return WALKING_SPEED_KMH;
        } else if ("public_transport".equalsIgnoreCase(transportMode)) {
            return PUBLIC_TRANSPORT_SPEED_KMH;
        } else if ("driving".equalsIgnoreCase(transportMode)) {
            return DRIVING_SPEED_KMH;
        }
        return PUBLIC_TRANSPORT_SPEED_KMH;  // Default
    }
    
    /**
     * Gets carbon factor (kg CO2/km) based on transport mode.
     */
    private double getCarbonFactor(String transportMode) {
        if ("walking".equalsIgnoreCase(transportMode)) {
            return 0.0;  // Walking = no emissions
        } else if ("public_transport".equalsIgnoreCase(transportMode)) {
            return 0.05;  // Public transport: ~50g CO2/km
        } else if ("driving".equalsIgnoreCase(transportMode)) {
            return 0.25;  // Car: ~250g CO2/km
        }
        return CARBON_FACTOR_KG_PER_KM;
    }
    
    /**
     * Result object for route calculations.
     */
    public static class RouteCalculationResult {
        private final double totalDistanceKm;
        private final double totalDurationMinutes;
        private final double totalCostTl;
        private final double co2EmissionsKg;
        private final int poiCount;
        
        public RouteCalculationResult(
            double totalDistanceKm,
            double totalDurationMinutes,
            double totalCostTl,
            double co2EmissionsKg,
            int poiCount
        ) {
            this.totalDistanceKm = totalDistanceKm;
            this.totalDurationMinutes = totalDurationMinutes;
            this.totalCostTl = totalCostTl;
            this.co2EmissionsKg = co2EmissionsKg;
            this.poiCount = poiCount;
        }
        
        public double getTotalDistanceKm() {
            return totalDistanceKm;
        }
        
        public double getTotalDurationMinutes() {
            return totalDurationMinutes;
        }
        
        public double getTotalCostTl() {
            return totalCostTl;
        }
        
        public double getCo2EmissionsKg() {
            return co2EmissionsKg;
        }
        
        public int getPoiCount() {
            return poiCount;
        }
    }
}
