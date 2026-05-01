package com.eskisehir.eventapi.service.route.optimization;

import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.dto.RouteOptimizationRequest;
import com.eskisehir.eventapi.service.route.optimization.RouteCalculationService.RouteCalculationResult;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Normalizes multi-criteria factors to [0, 1] range for fair weighting.
 */
@Service
public class RouteNormalizationService {
    
    // Reference values for normalization
    private static final double DISTANCE_REFERENCE_KM = 20.0;
    private static final double SUSTAINABILITY_REFERENCE_KG_CO2 = 5.0;
    
    /**
     * Normalizes distance: shorter is better.
     * norm_distance = 1 - min(distance / reference, 1)
     */
    public double normalizeDistance(double totalDistanceKm) {
        double ratio = Math.min(totalDistanceKm / DISTANCE_REFERENCE_KM, 1.0);
        return 1.0 - ratio;
    }
    
    /**
     * Normalizes duration based on budget.
     * norm_duration = 1 - min(duration / budget, 1)
     */
    public double normalizeDuration(double totalDurationMinutes, Integer timeBudgetMinutes) {
        if (timeBudgetMinutes == null || timeBudgetMinutes <= 0) {
            // Fallback: reference 240 minutes (4 hours)
            timeBudgetMinutes = 240;
        }
        double ratio = Math.min(totalDurationMinutes / timeBudgetMinutes, 1.0);
        return 1.0 - ratio;
    }
    
    /**
     * Normalizes budget usage.
     * norm_budget = 1 - budget_usage_ratio
     * Values: leaving budget is better (risk buffer)
     */
    public double normalizeBudget(double totalCostTl, Double monetaryBudgetTl) {
        if (monetaryBudgetTl == null || monetaryBudgetTl <= 0) {
            return 0.5;  // No budget specified, neutral score
        }
        double usageRatio = Math.min(totalCostTl / monetaryBudgetTl, 1.0);
        return 1.0 - usageRatio;
    }
    
    /**
     * Normalizes crowd exposure.
     * norm_crowd = 1 - weighted_avg_crowd_level
     * Lower crowds = higher score
     */
    public double normalizeCrowdExposure(List<Poi> poiSequence) {
        if (poiSequence == null || poiSequence.isEmpty()) {
            return 1.0;  // No POIs = no crowds
        }
        
        double totalCrowd = 0.0;
        for (Poi poi : poiSequence) {
            Double crowdLevel = poi.getCrowdProxy();
            if (crowdLevel != null) {
                totalCrowd += crowdLevel;
            }
        }
        
        double avgCrowdLevel = totalCrowd / poiSequence.size();
        return 1.0 - avgCrowdLevel;
    }
    
    /**
     * Normalizes sustainability (CO2 emissions).
     * norm_sustainability = 1 - min(co2 / reference, 1)
     * Lower emissions = higher score
     */
    public double normalizeSustainability(double co2EmissionsKg) {
        double ratio = Math.min(co2EmissionsKg / SUSTAINABILITY_REFERENCE_KG_CO2, 1.0);
        return 1.0 - ratio;
    }
    
    /**
     * Normalizes preference fit.
     * Already [0, 1] from Phase 9 feature extraction.
     */
    public double normalizePreferenceFit(List<Poi> poiSequence, Map<Long, Double> poiPreferenceScores) {
        if (poiSequence == null || poiSequence.isEmpty()) {
            return 0.5;  // No POIs, neutral
        }
        
        double totalScore = 0.0;
        for (Poi poi : poiSequence) {
            Double score = poiPreferenceScores.get(poi.getId());
            if (score != null) {
                totalScore += score;
            }
        }
        
        double avgScore = totalScore / poiSequence.size();
        return Math.max(0.0, Math.min(1.0, avgScore));  // Clamp to [0, 1]
    }
    
    /**
     * Normalizes local business support.
     * norm_local = count_local_pois / total_pois
     */
    public double normalizeLocalSupport(List<Poi> poiSequence) {
        if (poiSequence == null || poiSequence.isEmpty()) {
            return 0.5;  // No POIs, neutral
        }
        
        int localCount = 0;
        for (Poi poi : poiSequence) {
            // Check if local business score indicates local support (> 0.5)
            Double localScore = poi.getLocalBusinessScore();
            if (localScore != null && localScore > 0.5) {
                localCount++;
            }
        }
        
        return (double) localCount / poiSequence.size();
    }
    
    /**
     * Normalizes diversity (category diversity using Shannon entropy).
     * norm_diversity = H(categories) / H_max
     * where H = -sum(p_c * log(p_c))
     */
    public double normalizeDiversity(List<Poi> poiSequence) {
        if (poiSequence == null || poiSequence.isEmpty()) {
            return 0.5;  // No POIs, neutral
        }
        
        // Count category distribution
        Map<String, Integer> categoryCounts = new HashMap<>();
        for (Poi poi : poiSequence) {
            String category = poi.getCategory() != null ? poi.getCategory().toString() : "Other";
            categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
        }
        
        // Calculate Shannon entropy
        double entropy = 0.0;
        for (int count : categoryCounts.values()) {
            double probability = (double) count / poiSequence.size();
            if (probability > 0) {
                entropy -= probability * Math.log(probability);
            }
        }
        
        // Maximum entropy: log(number of categories)
        double maxEntropy = Math.log(categoryCounts.size());
        if (maxEntropy == 0) {
            return 0.5;  // Single category
        }
        
        return entropy / maxEntropy;  // Normalized to [0, 1]
    }
    
    /**
     * Combines all normalized objectives into a single composite score.
     * score = sum(w_i * obj_i) for all objectives
     */
    public double computeCompositeScore(
        Map<String, Double> weights,
        Map<String, Double> objectives
    ) {
        double score = 0.0;
        score += weights.getOrDefault("w_preference", 0.35) * objectives.getOrDefault("preference_fit", 0.5);
        score += weights.getOrDefault("w_crowd", 0.20) * objectives.getOrDefault("crowd_avoidance", 0.5);
        score += weights.getOrDefault("w_budget", 0.15) * objectives.getOrDefault("budget_efficiency", 0.5);
        score += weights.getOrDefault("w_sustainability", 0.12) * objectives.getOrDefault("sustainability", 0.5);
        score += weights.getOrDefault("w_local", 0.10) * objectives.getOrDefault("local_support", 0.5);
        score += weights.getOrDefault("w_diversity", 0.08) * objectives.getOrDefault("diversity", 0.5);
        return Math.max(0.0, Math.min(1.0, score));  // Clamp to [0, 1]
    }
    
    /**
     * Computes all normalized objectives for a route.
     */
    public Map<String, Double> computeAllObjectives(
        List<Poi> poiSequence,
        RouteCalculationResult metrics,
        RouteOptimizationRequest request,
        Map<Long, Double> poiPreferenceScores
    ) {
        Map<String, Double> objectives = new HashMap<>();
        
        objectives.put("preference_fit", normalizePreferenceFit(poiSequence, poiPreferenceScores));
        objectives.put("crowd_avoidance", normalizeCrowdExposure(poiSequence));
        objectives.put("budget_efficiency", normalizeBudget(metrics.getTotalCostTl(), request.getMonetaryBudgetTl()));
        objectives.put("sustainability", normalizeSustainability(metrics.getCo2EmissionsKg()));
        objectives.put("local_support", normalizeLocalSupport(poiSequence));
        objectives.put("diversity", normalizeDiversity(poiSequence));
        
        return objectives;
    }
}
