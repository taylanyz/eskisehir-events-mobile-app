package com.eskisehir.eventapi.algorithm;

import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.dto.RecommendationRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecommendationRankerImpl implements RecommendationRanker {

    @Override
    public Map<Poi, Double> rankCandidates(RecommendationRequest request, List<Poi> candidates) {
        Map<Poi, Double> scored = candidates.stream()
                .collect(Collectors.toMap(
                        poi -> poi,
                        poi -> calculateScore(request, poi)
                ));

        return scored.entrySet().stream()
                .sorted(Map.Entry.<Poi, Double>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

    private double calculateScore(RecommendationRequest request, Poi poi) {
        double preferenceFit = calculatePreferenceFit(request, poi);
        double crowdFit = calculateCrowdFit(poi);
        double budgetFit = calculateBudgetFit(request, poi);
        double sustainabilityFit = calculateSustainabilityFit(poi);
        double localSupportFit = calculateLocalSupportFit(poi);
        double popularityFit = calculatePopularityFit(poi);
        double recencyFit = calculateRecencyFit(poi);

        double score = preferenceFit * 0.30
                + crowdFit * 0.20
                + budgetFit * 0.15
                + sustainabilityFit * 0.10
                + localSupportFit * 0.10
                + popularityFit * 0.10
                + recencyFit * 0.05;

        return Math.max(0.0, Math.min(1.0, score));
    }

    private double calculatePreferenceFit(RecommendationRequest request, Poi poi) {
        if ((request.getPreferredCategories() == null || request.getPreferredCategories().isEmpty()) &&
                (request.getPreferredTags() == null || request.getPreferredTags().isEmpty())) {
            return 0.35;
        }

        double categoryMatch = 0.0;
        if (request.getPreferredCategories() != null && request.getPreferredCategories().contains(poi.getCategory())) {
            categoryMatch = 1.0;
        }

        double tagMatch = 0.0;
        if (request.getPreferredTags() != null && poi.getTags() != null) {
            long matchingTags = poi.getTags().stream()
                    .filter(tag -> request.getPreferredTags().stream()
                            .anyMatch(prefTag -> prefTag.equalsIgnoreCase(tag)))
                    .count();
            tagMatch = Math.min(1.0, matchingTags / 3.0);
        }

        return Math.min(1.0, categoryMatch * 0.7 + tagMatch * 0.3);
    }

    private double calculateCrowdFit(Poi poi) {
        double crowdProxy = poi.getCrowdProxy() != null ? poi.getCrowdProxy() : 0.5;
        return 1.0 - crowdProxy;
    }

    private double calculateBudgetFit(RecommendationRequest request, Poi poi) {
        if (request.getMaxPrice() == null || poi.getPrice() == null) {
            return 0.5;
        }
        return poi.getPrice() <= request.getMaxPrice() ? 1.0 : 0.2;
    }

    private double calculateSustainabilityFit(Poi poi) {
        return poi.getSustainabilityScore() != null ? poi.getSustainabilityScore() : 0.5;
    }

    private double calculateLocalSupportFit(Poi poi) {
        return poi.getLocalBusinessScore() != null ? poi.getLocalBusinessScore() : 0.5;
    }

    private double calculatePopularityFit(Poi poi) {
        return poi.getPopularityScore() != null ? poi.getPopularityScore() : 0.5;
    }

    private double calculateRecencyFit(Poi poi) {
        if (poi.getDate() == null) {
            return 0.5;
        }
        long daysUntil = ChronoUnit.DAYS.between(LocalDateTime.now(), poi.getDate());
        if (daysUntil < 0) {
            return 0.2;
        }
        if (daysUntil <= 7) {
            return 1.0;
        }
        return 0.6;
    }
}
