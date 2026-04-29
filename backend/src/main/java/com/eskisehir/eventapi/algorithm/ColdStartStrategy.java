package com.eskisehir.eventapi.algorithm;

import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.dto.RecommendationRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("coldStartStrategy")
public class ColdStartStrategy implements RecommendationStrategy {

    @Override
    public Map<Poi, Double> scorePois(RecommendationRequest request, List<Poi> candidates) {
        Map<Poi, Double> scored = new HashMap<>();

        for (Poi poi : candidates) {
            double score = basePopularityScore(poi) * 0.65;
            score += categoryBonus(poi, request) * 0.20;
            score += tagBonus(poi, request) * 0.10;
            score += budgetBonus(poi, request) * 0.05;
            score += recencyBonus(poi);
            scored.put(poi, Math.min(1.0, Math.max(0.0, score)));
        }

        return scored.entrySet().stream()
                .sorted(Map.Entry.<Poi, Double>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private double basePopularityScore(Poi poi) {
        return poi.getPopularityScore() != null ? poi.getPopularityScore() : 0.1;
    }

    private double categoryBonus(Poi poi, RecommendationRequest request) {
        if (request.getPreferredCategories() == null) {
            return 0.0;
        }
        return request.getPreferredCategories().contains(poi.getCategory()) ? 0.20 : 0.0;
    }

    private double tagBonus(Poi poi, RecommendationRequest request) {
        if (request.getPreferredTags() == null || poi.getTags() == null) {
            return 0.0;
        }
        long matches = poi.getTags().stream()
                .filter(tag -> request.getPreferredTags().stream()
                        .anyMatch(pref -> pref.equalsIgnoreCase(tag)))
                .count();
        return Math.min(0.10, matches * 0.02);
    }

    private double budgetBonus(Poi poi, RecommendationRequest request) {
        if (poi.getPrice() == null || request.getMaxPrice() == null) {
            return 0.0;
        }
        return poi.getPrice() <= request.getMaxPrice() ? 0.05 : 0.0;
    }

    private double recencyBonus(Poi poi) {
        if (poi.getDate() == null) {
            return 0.0;
        }
        long daysUntil = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDateTime.now(), poi.getDate());
        return (daysUntil >= 0 && daysUntil <= 14) ? 0.05 : 0.0;
    }
}
