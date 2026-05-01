package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.algorithm.ColdStartStrategy;
import com.eskisehir.eventapi.algorithm.RecommendationCandidateGenerator;
import com.eskisehir.eventapi.algorithm.RecommendationRanker;
import com.eskisehir.eventapi.algorithm.RecommendationStrategy;
import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.dto.RecommendationRequest;
import com.eskisehir.eventapi.repository.BanditEventRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecommendationEngine {

    private final RecommendationService recommendationService;
    private final RecommendationCandidateGenerator candidateGenerator;
    private final RecommendationRanker recommendationRanker;
    private final RecommendationStrategy recommendationStrategy;
    private final ColdStartStrategy coldStartStrategy;
    private final BanditEventRepository banditEventRepository;

    public RecommendationEngine(
            RecommendationService recommendationService,
            RecommendationCandidateGenerator candidateGenerator,
            RecommendationRanker recommendationRanker,
            @Qualifier("thompsonSamplingStrategy") RecommendationStrategy recommendationStrategy,
            ColdStartStrategy coldStartStrategy,
            BanditEventRepository banditEventRepository) {
        this.recommendationService = recommendationService;
        this.candidateGenerator = candidateGenerator;
        this.recommendationRanker = recommendationRanker;
        this.recommendationStrategy = recommendationStrategy;
        this.coldStartStrategy = coldStartStrategy;
        this.banditEventRepository = banditEventRepository;
    }

    public List<Poi> getRecommendations(RecommendationRequest request) {
        return getRecommendationScores(request).keySet().stream()
                .collect(Collectors.toList());
    }

    public Map<Poi, Double> getRecommendationScores(RecommendationRequest request) {
        List<Poi> candidates = candidateGenerator.generateCandidates(request);
        if (candidates.isEmpty()) {
            return Map.of();
        }

        boolean useColdStart = request.getUserId() == null;
        if (!useColdStart) {
            useColdStart = banditEventRepository.findByUserId(request.getUserId()).size() < 3;
        }

        Map<Poi, Double> scored = useColdStart
                ? coldStartStrategy.scorePois(request, candidates)
                : recommendationRanker.rankCandidates(request, candidates);

        return scored.entrySet().stream()
                .limit(request.getEffectiveLimit())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

    public List<Poi> getTrending(int limit) {
        return recommendationService.getAllActivePois().stream()
                .sorted((a, b) -> Double.compare(
                        b.getPopularityScore() != null ? b.getPopularityScore() : 0.0,
                        a.getPopularityScore() != null ? a.getPopularityScore() : 0.0))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
