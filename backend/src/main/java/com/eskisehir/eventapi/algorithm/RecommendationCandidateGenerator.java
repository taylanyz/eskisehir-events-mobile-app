package com.eskisehir.eventapi.algorithm;

import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.dto.RecommendationRequest;

import java.util.List;

public interface RecommendationCandidateGenerator {

    List<Poi> generateCandidates(RecommendationRequest request);
}
