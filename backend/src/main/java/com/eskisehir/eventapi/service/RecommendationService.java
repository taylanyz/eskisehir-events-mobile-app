package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.dto.RecommendationRequest;
import com.eskisehir.eventapi.dto.RouteRequest;
import com.eskisehir.eventapi.repository.PoiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Baseline recommendation and route planning service.
 *
 * This is the V1 "rule-based" implementation that will be replaced by:
 *   - Content-based filtering with cosine similarity (Phase 3)
 *   - LinUCB contextual bandit (Phase 5)
 *   - Multi-criteria route optimizer (Phase 4)
 *
 * Keeping this as a baseline for thesis comparison.
 */
@Service
public class RecommendationService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    private final PoiRepository poiRepository;

    public RecommendationService(PoiRepository poiRepository) {
        this.poiRepository = poiRepository;
    }

    // Default Eskişehir city center coordinates
    private static final double DEFAULT_LAT = 39.7667;
    private static final double DEFAULT_LNG = 30.5256;

    /**
     * Generates personalized POI recommendations based on user preferences.
     * V1: Rule-based scoring (baseline for thesis comparison).
     *
     * @param request contains preferred categories, tags, budget, and limit
     * @return list of recommended POIs sorted by relevance score
     */
    public List<Poi> getRecommendations(RecommendationRequest request) {
        List<Poi> allPois = poiRepository.findByIsActiveTrue();

        log.info("Generating recommendations from {} active POIs", allPois.size());

        Map<Poi, Integer> scores = new HashMap<>();

        for (Poi poi : allPois) {
            int score = calculateScore(poi, request);
            scores.put(poi, score);
        }

        return scores.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted((a, b) -> {
                    int scoreCompare = b.getValue().compareTo(a.getValue());
                    if (scoreCompare != 0) return scoreCompare;
                    // For event-type POIs, prefer sooner dates
                    if (a.getKey().getDate() != null && b.getKey().getDate() != null) {
                        return a.getKey().getDate().compareTo(b.getKey().getDate());
                    }
                    return 0;
                })
                .limit(request.getEffectiveLimit())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<Poi> getAllActivePois() {
        return poiRepository.findByIsActiveTrue();
    }

    /**
     * V1 scoring: rule-based.
     *   - Category match: +3 points
     *   - Each matching tag: +1 point
     *   - Within 7 days (events only): +1 point
     *   - Within budget: +1 point
     *   - High sustainability: +1 point
     *   - Low crowd: +1 point
     */
    private int calculateScore(Poi poi, RecommendationRequest request) {
        int score = 0;

        // Category match → +3
        if (request.getPreferredCategories() != null &&
            request.getPreferredCategories().contains(poi.getCategory())) {
            score += 3;
        }

        // Tag overlap → +1 per match
        if (request.getPreferredTags() != null && poi.getTags() != null) {
            long matchingTags = poi.getTags().stream()
                    .filter(tag -> request.getPreferredTags().stream()
                            .anyMatch(prefTag -> prefTag.equalsIgnoreCase(tag)))
                    .count();
            score += (int) matchingTags;
        }

        // Recency bonus (events only) → +1 if within 7 days
        if (poi.getDate() != null) {
            long daysUntil = ChronoUnit.DAYS.between(LocalDateTime.now(), poi.getDate());
            if (daysUntil >= 0 && daysUntil <= 7) {
                score += 1;
            }
        }

        // Budget match → +1
        if (request.getMaxPrice() != null && poi.getPrice() != null &&
            poi.getPrice() <= request.getMaxPrice()) {
            score += 1;
        }

        // Sustainability bonus → +1 if high
        if (poi.getSustainabilityScore() != null && poi.getSustainabilityScore() >= 0.7) {
            score += 1;
        }

        // Low crowd bonus → +1 if not crowded
        if (poi.getCrowdProxy() != null && poi.getCrowdProxy() <= 0.4) {
            score += 1;
        }

        return score;
    }

    /**
     * V1 route planning: nearest-neighbor heuristic.
     * Will be replaced by multi-criteria optimizer in Phase 4.
     */
    public List<Poi> planRoute(RouteRequest request) {
        List<Poi> pois = request.getEventIds().stream()
                .map(id -> poiRepository.findById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (pois.size() <= 1) {
            return pois;
        }

        double currentLat = request.getStartLatitude() != null ? request.getStartLatitude() : DEFAULT_LAT;
        double currentLng = request.getStartLongitude() != null ? request.getStartLongitude() : DEFAULT_LNG;

        List<Poi> orderedRoute = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        while (orderedRoute.size() < pois.size()) {
            Poi nearest = null;
            double nearestDistance = Double.MAX_VALUE;

            for (Poi poi : pois) {
                if (visited.contains(poi.getId())) continue;

                double distance = haversineDistance(currentLat, currentLng,
                        poi.getLatitude(), poi.getLongitude());

                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearest = poi;
                }
            }

            if (nearest != null) {
                orderedRoute.add(nearest);
                visited.add(nearest.getId());
                currentLat = nearest.getLatitude();
                currentLng = nearest.getLongitude();
            }
        }

        log.info("Route planned for {} POIs using nearest-neighbor heuristic", orderedRoute.size());
        return orderedRoute;
    }

    /**
     * Haversine distance between two geographic points.
     * @return distance in kilometers
     */
    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
