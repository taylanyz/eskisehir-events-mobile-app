package com.eskisehir.eventapi.algorithm;

import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.dto.RecommendationRequest;

import java.util.List;
import java.util.Map;

public interface RecommendationStrategy {

    /**
     * Scores candidate POIs based on request context and historical data.
     * The returned map contains POIs and their computed scores.
     */
    Map<Poi, Double> scorePois(RecommendationRequest request, List<Poi> candidates);
}
