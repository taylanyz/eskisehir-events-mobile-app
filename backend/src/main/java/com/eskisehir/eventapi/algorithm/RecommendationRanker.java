package com.eskisehir.eventapi.algorithm;

import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.dto.RecommendationRequest;

import java.util.List;
import java.util.Map;

public interface RecommendationRanker {

    Map<Poi, Double> rankCandidates(RecommendationRequest request, List<Poi> candidates);
}
