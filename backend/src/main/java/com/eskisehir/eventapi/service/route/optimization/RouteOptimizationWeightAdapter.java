package com.eskisehir.eventapi.service.route.optimization;

import com.eskisehir.eventapi.domain.model.User;
import com.eskisehir.eventapi.domain.model.UserPreference;
import com.eskisehir.eventapi.domain.model.SensitivityLevel;
import com.eskisehir.eventapi.dto.RouteOptimizationRequest;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Dynamically adjusts optimization weights based on user profile and context.
 */
@Service
public class RouteOptimizationWeightAdapter {
    
    // Default weights (must sum to 1.0)
    public static final double DEFAULT_W_PREFERENCE = 0.35;
    public static final double DEFAULT_W_CROWD = 0.20;
    public static final double DEFAULT_W_BUDGET = 0.15;
    public static final double DEFAULT_W_SUSTAINABILITY = 0.12;
    public static final double DEFAULT_W_LOCAL = 0.10;
    public static final double DEFAULT_W_DIVERSITY = 0.08;
    
    /**
     * Creates base weight map from defaults.
     */
    public Map<String, Double> getDefaultWeights() {
        Map<String, Double> weights = new HashMap<>();
        weights.put("w_preference", DEFAULT_W_PREFERENCE);
        weights.put("w_crowd", DEFAULT_W_CROWD);
        weights.put("w_budget", DEFAULT_W_BUDGET);
        weights.put("w_sustainability", DEFAULT_W_SUSTAINABILITY);
        weights.put("w_local", DEFAULT_W_LOCAL);
        weights.put("w_diversity", DEFAULT_W_DIVERSITY);
        return weights;
    }
    
    /**
     * Adapts weights based on user profile and context.
     * Returns normalized weights (sum = 1.0).
     */
    public Map<String, Double> adaptWeights(
        User user,
        RouteOptimizationRequest request,
        Map<String, Double> baseWeights
    ) {
        Map<String, Double> adapted = new HashMap<>(baseWeights);
        
        UserPreference prefs = user.getPreference();
        if (prefs == null) {
            return normalizeWeights(adapted);
        }
        
        // Sustainability adjustment
        Double sustainabilityPref = prefs.getSustainabilityPreference();
        if (sustainabilityPref != null && sustainabilityPref > 0.7) {
            adapted.put("w_sustainability", adapted.get("w_sustainability") * 1.5);
            adapted.put("w_budget", adapted.get("w_budget") * 0.8);
        }
        
        // Crowd avoidance adjustment
        SensitivityLevel crowdTol = prefs.getCrowdTolerance();
        if (crowdTol != null && crowdTol == SensitivityLevel.LOW) {
            adapted.put("w_crowd", adapted.get("w_crowd") * 1.3);
        }
        
        // Budget sensitivity adjustment
        SensitivityLevel budgetSens = prefs.getBudgetSensitivity();
        if (budgetSens != null) {
            if (budgetSens == SensitivityLevel.HIGH) {
                adapted.put("w_budget", adapted.get("w_budget") * 1.4);
            } else if (budgetSens == SensitivityLevel.LOW) {
                adapted.put("w_budget", adapted.get("w_budget") * 0.5);
            }
        }
        
        // Time sensitivity: if user has low time (< 2 hours), increase duration efficiency
        if (request.getTimeBudgetMinutes() != null && request.getTimeBudgetMinutes() < 120) {
            // Implicit: distance and duration efficiency already matter; no explicit adjustment needed
        }
        
        // Apply user weight overrides if provided
        if (request.getWeightOverrides() != null) {
            adapted.putAll(request.getWeightOverrides());
        }
        
        // Normalize to sum = 1.0
        return normalizeWeights(adapted);
    }
    
    /**
     * Context-based modulation of weights.
     * Adjusts based on weather, time of day, etc.
     */
    public Map<String, Double> applyContextModulation(
        Map<String, Double> weights,
        RouteOptimizationRequest request
    ) {
        Map<String, Double> modulated = new HashMap<>(weights);
        
        // Weather-based adjustments
        String weather = request.getWeather();
        if ("rain".equalsIgnoreCase(weather)) {
            // Rain: prefer shorter routes, indoor POIs (implicit in filtering)
            // No explicit weight adjustment; manifest in distance/duration norms
        } else if ("hot".equalsIgnoreCase(weather)) {
            // Hot weather: increase crowd avoidance penalty
            modulated.put("w_crowd", modulated.get("w_crowd") * 1.2);
        }
        
        // Time-of-day adjustments
        String currentTime = request.getCurrentTime();
        if (currentTime != null && isRushHour(currentTime)) {
            // Peak times: increase crowd avoidance
            modulated.put("w_crowd", modulated.get("w_crowd") * 1.15);
        }
        
        // Renormalize after context adjustments
        return normalizeWeights(modulated);
    }
    
    /**
     * Combines user profile adaptation + context modulation.
     */
    public Map<String, Double> computeFinalWeights(
        User user,
        RouteOptimizationRequest request
    ) {
        Map<String, Double> baseWeights = getDefaultWeights();
        Map<String, Double> adapted = adaptWeights(user, request, baseWeights);
        Map<String, Double> contextModulated = applyContextModulation(adapted, request);
        return normalizeWeights(contextModulated);
    }
    
    /**
     * Normalizes weights to sum = 1.0.
     */
    private Map<String, Double> normalizeWeights(Map<String, Double> weights) {
        Double sum = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        if (sum <= 0) {
            return getDefaultWeights();  // Fallback if sum is zero/negative
        }
        
        Map<String, Double> normalized = new HashMap<>();
        for (Map.Entry<String, Double> entry : weights.entrySet()) {
            normalized.put(entry.getKey(), entry.getValue() / sum);
        }
        return normalized;
    }
    
    /**
     * Simple rush hour detection: 6-9am, 12-1pm, 6-7pm
     */
    private boolean isRushHour(String timeString) {
        try {
            // Parse "HH:mm" format
            int hour = Integer.parseInt(timeString.substring(0, 2));
            return (hour >= 6 && hour < 9) || (hour >= 12 && hour < 13) || (hour >= 18 && hour < 19);
        } catch (Exception e) {
            return false;
        }
    }
}
