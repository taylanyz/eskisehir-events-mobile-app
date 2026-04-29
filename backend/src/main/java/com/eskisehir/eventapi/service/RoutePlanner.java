package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.domain.model.Poi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Route planning service using Nearest Neighbor heuristic for TSP.
 * Phase 4 V1: Greedy nearest-neighbor algorithm.
 * Can be extended with 2-opt optimization or exact TSP solvers in future phases.
 */
@Service
public class RoutePlanner {

    private static final Logger log = LoggerFactory.getLogger(RoutePlanner.class);
    private final GeoService geoService;

    public RoutePlanner(GeoService geoService) {
        this.geoService = geoService;
    }

    /**
     * Plan an optimized route using nearest-neighbor heuristic.
     * Start from given location, visit all POIs in order of proximity.
     *
     * @param startLat starting latitude
     * @param startLng starting longitude
     * @param pois list of POIs to visit
     * @return ordered list of POIs forming the route
     */
    public List<Poi> planNearestNeighbor(double startLat, double startLng, List<Poi> pois) {
        if (pois == null || pois.isEmpty()) {
            return new ArrayList<>();
        }
        if (pois.size() == 1) {
            return new ArrayList<>(pois);
        }

        List<Poi> route = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        double currentLat = startLat;
        double currentLng = startLng;

        while (route.size() < pois.size()) {
            Poi nearest = null;
            double nearestDistance = Double.MAX_VALUE;

            for (Poi poi : pois) {
                if (visited.contains(poi.getId())) {
                    continue;
                }

                double distance = geoService.getDistanceKm(currentLat, currentLng,
                        poi.getLatitude(), poi.getLongitude());

                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = poi;
                }
            }

            if (nearest != null) {
                route.add(nearest);
                visited.add(nearest.getId());
                currentLat = nearest.getLatitude();
                currentLng = nearest.getLongitude();
            }
        }

        log.info("Planned nearest-neighbor route for {} POIs", route.size());
        return route;
    }

    /**
     * Calculate total distance for a given route.
     */
    public double calculateTotalDistance(double startLat, double startLng, List<Poi> route) {
        if (route == null || route.isEmpty()) {
            return 0.0;
        }

        double totalDistance = 0.0;
        double currentLat = startLat;
        double currentLng = startLng;

        for (Poi poi : route) {
            totalDistance += geoService.getDistanceKm(currentLat, currentLng,
                    poi.getLatitude(), poi.getLongitude());
            currentLat = poi.getLatitude();
            currentLng = poi.getLongitude();
        }

        return totalDistance;
    }

    /**
     * Calculate total walking time for a given route.
     */
    public int calculateTotalWalkingTime(double startLat, double startLng, List<Poi> route) {
        if (route == null || route.isEmpty()) {
            return 0;
        }

        int totalTime = 0;
        double currentLat = startLat;
        double currentLng = startLng;

        for (Poi poi : route) {
            totalTime += geoService.getWalkingTimeMinutes(currentLat, currentLng,
                    poi.getLatitude(), poi.getLongitude());
            currentLat = poi.getLatitude();
            currentLng = poi.getLongitude();
        }

        return totalTime;
    }

    /**
     * Get the GeoService for distance/time calculations.
     * Used by RouteController for turn-by-turn navigation.
     */
    public GeoService getGeoService() {
        return geoService;
    }
}
