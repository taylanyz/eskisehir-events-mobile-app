package com.eskisehir.eventapi.ml;

import com.eskisehir.eventapi.domain.model.User;
import com.eskisehir.eventapi.dto.RecommendationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Builds context vector for contextual bandit learning.
 * Context = current user state + environment state for decision-making.
 */
@Service
public class ContextVectorBuilder {

    private final ObjectMapper objectMapper;
    private final UserFeatureExtractor userFeatureExtractor;
    private final PoiFeatureExtractor poiFeatureExtractor;

    public ContextVectorBuilder(ObjectMapper objectMapper, UserFeatureExtractor userFeatureExtractor,
                                PoiFeatureExtractor poiFeatureExtractor) {
        this.objectMapper = objectMapper;
        this.userFeatureExtractor = userFeatureExtractor;
        this.poiFeatureExtractor = poiFeatureExtractor;
    }

    /**
     * Build context vector JSON string for storing in bandit_events.
     *
     * Context includes:
     * - weather (placeholder for now, can integrate with weather service)
     * - time of day
     * - day of week
     * - user budget level
     * - user transportation mode
     * - user crowd preference
     * - user sustainability preference
     */
    public String buildContextVectorJson(User user, RecommendationRequest request) {
        Map<String, Object> context = new HashMap<>();

        // Time context
        context.put("timeOfDay", request.getTimeOfDay() != null ? request.getTimeOfDay() : "AFTERNOON");
        context.put("dayOfWeek", request.getDayOfWeek() != null ? request.getDayOfWeek() : "SATURDAY");

        // User preference context
        Map<String, Double> userFeatures = userFeatureExtractor.extractFeatures(user, request);
        context.put("userFeatures", userFeatures);

        // Weather (placeholder - can be extended with actual weather service)
        context.put("weather", "SUNNY");

        // Location context (if provided)
        if (request.getLatitude() != null && request.getLongitude() != null) {
            Map<String, Double> location = new HashMap<>();
            location.put("latitude", request.getLatitude());
            location.put("longitude", request.getLongitude());
            context.put("location", location);
        }

        try {
            return objectMapper.writeValueAsString(context);
        } catch (Exception e) {
            return "{}";
        }
    }

    /**
     * Extract context features for similarity calculations in learning algorithms.
     */
    public Map<String, Double> extractContextFeatures(String contextVectorJson) {
        Map<String, Double> features = new HashMap<>();
        try {
            Map<String, Object> context = objectMapper.readValue(contextVectorJson, Map.class);

            // Extract time features
            String timeOfDay = (String) context.get("timeOfDay");
            features.put("time_of_day", normalizeTimeOfDay(timeOfDay));

            String dayOfWeek = (String) context.get("dayOfWeek");
            features.put("day_of_week", normalizeDayOfWeek(dayOfWeek));

            // Extract user features if present
            if (context.get("userFeatures") instanceof Map userFeaturesMap) {
                userFeaturesMap.forEach((key, value) -> {
                    if (value instanceof Number) {
                        features.put("user_" + key, ((Number) value).doubleValue());
                    }
                });
            }

            // Weather encoding
            String weather = (String) context.get("weather");
            features.put("weather", normalizeWeather(weather));

        } catch (Exception e) {
            // Return default features on parse error
        }

        return features;
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

    private double normalizeWeather(String weather) {
        if (weather == null) return 0.5;
        return switch (weather) {
            case "SUNNY" -> 0.8;
            case "CLOUDY" -> 0.5;
            case "RAINY" -> 0.2;
            case "SNOWY" -> 0.1;
            default -> 0.5;
        };
    }
}
