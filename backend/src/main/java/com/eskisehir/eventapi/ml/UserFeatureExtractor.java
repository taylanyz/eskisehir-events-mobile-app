package com.eskisehir.eventapi.ml;

import com.eskisehir.eventapi.domain.model.User;
import com.eskisehir.eventapi.domain.model.UserPreference;
import com.eskisehir.eventapi.dto.RecommendationRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Extracts feature vector from user preferences and context.
 * Features include: category affinity, temporal preferences, budget level, mobility preference.
 */
@Service
public class UserFeatureExtractor {

    /**
     * Extract normalized user feature vector [0, 1] for scoring.
     * Used by ranking and bandit algorithms.
     */
    public Map<String, Double> extractFeatures(User user, RecommendationRequest request) {
        Map<String, Double> features = new HashMap<>();

        UserPreference preference = user != null ? user.getPreference() : null;

        // Budget level normalization: LOW=0.2, MEDIUM=0.5, HIGH=0.8, LUXURY=1.0
        if (preference != null && preference.getBudgetSensitivity() != null) {
            features.put("budget_level", normalizeBudgetLevel(preference.getBudgetSensitivity().name()));
        } else {
            features.put("budget_level", 0.5);
        }

        // Mobility preference: WALKING=0.3, PUBLIC_TRANSPORT=0.6, DRIVING=1.0
        if (preference != null && preference.getMobilityPreference() != null) {
            features.put("mobility_preference", normalizeMobilityPreference(preference.getMobilityPreference().name()));
        } else if (request.getMobilityPreference() != null) {
            features.put("mobility_preference", normalizeMobilityPreference(request.getMobilityPreference().name()));
        } else {
            features.put("mobility_preference", 0.5);
        }

        // Crowd preference: LOVES_CROWDED=1.0, NEUTRAL=0.5, AVOIDS_CROWDS=0.0
        if (preference != null && preference.getCrowdTolerance() != null) {
            features.put("crowd_tolerance", normalizeCrowdPreference(preference.getCrowdTolerance().name()));
        } else {
            features.put("crowd_tolerance", 0.5);
        }

        // Sustainability preference: 0-1 scale directly
        if (preference != null && preference.getSustainabilityPreference() != null) {
            features.put("sustainability_focus", preference.getSustainabilityPreference());
        } else {
            features.put("sustainability_focus", 0.5);
        }

        // Time of day preference
        if (request.getTimeOfDay() != null) {
            features.put("time_of_day", normalizeTimeOfDay(request.getTimeOfDay()));
        } else {
            features.put("time_of_day", 0.5);
        }

        // Day of week preference
        if (request.getDayOfWeek() != null) {
            features.put("day_of_week", normalizeDayOfWeek(request.getDayOfWeek()));
        } else {
            features.put("day_of_week", 0.5);
        }

        return features;
    }

    private double normalizeBudgetLevel(String budget) {
        if (budget == null) return 0.5;
        return switch (budget) {
            case "LOW" -> 0.2;
            case "MEDIUM" -> 0.5;
            case "HIGH" -> 0.8;
            case "VERY_HIGH" -> 1.0;
            default -> 0.5;
        };
    }

    private double normalizeMobilityPreference(String mobility) {
        if (mobility == null) return 0.5;
        return switch (mobility) {
            case "WALKING" -> 0.3;
            case "PUBLIC_TRANSPORT" -> 0.6;
            case "DRIVING" -> 1.0;
            default -> 0.5;
        };
    }

    private double normalizeCrowdPreference(String crowd) {
        if (crowd == null) return 0.5;
        return switch (crowd) {
            case "HIGH" -> 1.0;
            case "MEDIUM" -> 0.5;
            case "LOW" -> 0.0;
            default -> 0.5;
        };
    }

    private double normalizeTimeOfDay(String timeOfDay) {
        if (timeOfDay == null) return 0.5;
        return switch (timeOfDay) {
            case "MORNING" -> 0.25;
            case "AFTERNOON" -> 0.5;
            case "EVENING" -> 0.75;
            case "NIGHT" -> 1.0;
            default -> 0.5;
        };
    }

    private double normalizeDayOfWeek(String dayOfWeek) {
        if (dayOfWeek == null) return 0.5;
        return switch (dayOfWeek) {
            case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" -> 0.3;
            case "SATURDAY" -> 0.7;
            case "SUNDAY" -> 0.9;
            default -> 0.5;
        };
    }
}
