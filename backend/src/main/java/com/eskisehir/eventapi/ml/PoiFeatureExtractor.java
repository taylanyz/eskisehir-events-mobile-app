package com.eskisehir.eventapi.ml;

import com.eskisehir.eventapi.domain.model.Poi;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Extracts feature vector from POI attributes.
 * Features include: category, crowd level, budget level, sustainability score, local support score, recency.
 */
@Service
public class PoiFeatureExtractor {

    /**
     * Extract normalized POI feature vector [0, 1] for scoring.
     * Used by ranking and bandit algorithms.
     */
    public Map<String, Double> extractFeatures(Poi poi) {
        Map<String, Double> features = new HashMap<>();

        // Category is handled separately by recommendation service, included here for completeness
        features.put("category_encoded", 0.5);

        // Crowd proxy: [0, 1] where 0 = empty, 1 = very crowded
        features.put("crowd_level", poi.getCrowdProxy() != null ? poi.getCrowdProxy() : 0.5);

        // Budget level: normalize price to [0, 1]
        features.put("budget_level", normalizePrice(poi.getPrice()));

        // Sustainability score: [0, 1]
        features.put("sustainability_score", poi.getSustainabilityScore() != null ? poi.getSustainabilityScore() : 0.5);

        // Local business score: [0, 1]
        features.put("local_support_score", poi.getLocalBusinessScore() != null ? poi.getLocalBusinessScore() : 0.5);

        // Popularity score: [0, 1]
        features.put("popularity_score", poi.getPopularityScore() != null ? poi.getPopularityScore() : 0.5);

        // Recency: normalize days until event (for events only)
        features.put("recency_score", calculateRecencyScore(poi.getDate()));

        // Family friendly: binary encoded as 0.2 (not family friendly) or 1.0 (family friendly)
        features.put("family_friendly", poi.getFamilyFriendly() != null && poi.getFamilyFriendly() ? 1.0 : 0.2);

        // Indoor/outdoor preference encoded
        features.put("indoor_outdoor", encodeIndoorOutdoor(poi.getIndoorOutdoor()));

        return features;
    }

    private double normalizePrice(Double price) {
        if (price == null) {
            return 0.5;
        }
        // Normalize price: assume 0-500 TRY is typical range
        return Math.min(1.0, price / 500.0);
    }

    private double calculateRecencyScore(LocalDateTime date) {
        if (date == null) {
            return 0.5;
        }

        long daysUntil = ChronoUnit.DAYS.between(LocalDateTime.now(), date);

        if (daysUntil < 0) {
            return 0.1;
        }
        if (daysUntil <= 7) {
            return 1.0;
        }
        if (daysUntil <= 30) {
            return 0.7;
        }
        return 0.3;
    }

    private double encodeIndoorOutdoor(com.eskisehir.eventapi.domain.model.IndoorOutdoor indoorOutdoor) {
        if (indoorOutdoor == null) {
            return 0.5;
        }
        return switch (indoorOutdoor) {
            case INDOOR -> 0.2;
            case OUTDOOR -> 0.8;
            case BOTH -> 0.5;
        };
    }
}
