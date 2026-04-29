package com.eskisehir.eventapi.algorithm;

import com.eskisehir.eventapi.domain.model.BanditEvent;
import com.eskisehir.eventapi.domain.model.BanditArmStat;
import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.dto.RecommendationRequest;
import com.eskisehir.eventapi.repository.BanditEventRepository;
import com.eskisehir.eventapi.repository.BanditStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("thompsonSamplingStrategy")
public class ThompsonSamplingStrategy implements RecommendationStrategy {

    private static final Logger log = LoggerFactory.getLogger(ThompsonSamplingStrategy.class);
    private final BanditEventRepository banditEventRepository;
    private final BanditStatsRepository banditStatsRepository;
    private final Random random;

    public ThompsonSamplingStrategy(
            BanditEventRepository banditEventRepository,
            BanditStatsRepository banditStatsRepository) {
        this.banditEventRepository = banditEventRepository;
        this.banditStatsRepository = banditStatsRepository;
        this.random = new Random();
    }

    @Override
    public Map<Poi, Double> scorePois(RecommendationRequest request, List<Poi> candidates) {
        Map<Poi, Double> scores = new HashMap<>();

        for (Poi poi : candidates) {
            double banditScore = calculateThompsonScore(poi, request);
            double contextualBonus = calculateContextualBonus(poi, request);
            double score = banditScore + contextualBonus;
            scores.put(poi, Math.max(0.0, Math.min(1.5, score)));
        }

        return scores.entrySet().stream()
                .sorted(Map.Entry.<Poi, Double>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    private double calculateThompsonScore(Poi poi, RecommendationRequest request) {
        if (request.getUserId() != null) {
            Optional<BanditArmStat> stats = banditStatsRepository.findByUserIdAndPoiId(request.getUserId(), poi.getId());
            if (stats.isPresent()) {
                BanditArmStat stat = stats.get();
                return sampledScore(stat.getAlpha(), stat.getBeta(), poi.getId());
            }
        }

        List<BanditEvent> events = banditEventRepository.findByPoiId(poi.getId());
        long positive = events.stream()
                .filter(event -> event.getReward() != null && event.getReward() >= 0.75)
                .count();
        long negative = events.stream()
                .filter(event -> event.getReward() != null && event.getReward() < 0.75)
                .count();

        double alpha = 1.0 + positive;
        double beta = 1.0 + negative;

        return sampledScore(alpha, beta, poi.getId());
    }

    private double sampledScore(double alpha, double beta, Long poiId) {
        double sample = alpha / (alpha + beta);
        double noise = (random.nextDouble() - 0.5) * 0.1;
        double result = sample + noise;

        log.debug("Thompson sample for poi {}: alpha={}, beta={}, sample={}, noise={}",
            poiId, alpha, beta, sample, noise);

        return Math.max(0.0, Math.min(1.0, result));
    }

    private double calculateContextualBonus(Poi poi, RecommendationRequest request) {
        double bonus = 0.0;

        if (request.getPreferredCategories() != null && request.getPreferredCategories().contains(poi.getCategory())) {
            bonus += 0.25;
        }

        if (request.getPreferredTags() != null && poi.getTags() != null) {
            long matchingTags = poi.getTags().stream()
                    .filter(tag -> request.getPreferredTags().stream()
                            .anyMatch(prefTag -> prefTag.equalsIgnoreCase(tag)))
                    .count();
            bonus += Math.min(0.25, matchingTags * 0.05);
        }

        if (request.getMaxPrice() != null && poi.getPrice() != null && poi.getPrice() <= request.getMaxPrice()) {
            bonus += 0.15;
        }

        if (request.getMobilityPreference() != null && request.getMobilityPreference().name().equalsIgnoreCase("WALKING")) {
            if (poi.getCrowdProxy() != null && poi.getCrowdProxy() <= 0.5) {
                bonus += 0.10;
            }
        }

        if (poi.getSustainabilityScore() != null && poi.getSustainabilityScore() >= 0.7) {
            bonus += 0.10;
        }

        return Math.min(0.5, bonus);
    }
}
