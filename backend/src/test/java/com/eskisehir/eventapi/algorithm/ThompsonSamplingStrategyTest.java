package com.eskisehir.eventapi.algorithm;

import com.eskisehir.eventapi.domain.model.BanditArmStat;
import com.eskisehir.eventapi.domain.model.Category;
import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.dto.RecommendationRequest;
import com.eskisehir.eventapi.repository.BanditEventRepository;
import com.eskisehir.eventapi.repository.BanditStatsRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThompsonSamplingStrategyTest {

    @Test
    void scorePois_PrefersPersistedUserStatsWhenAvailable() {
        Poi preferred = new Poi();
        preferred.setId(1L);
        preferred.setCategory(Category.CAFE);

        Poi weaker = new Poi();
        weaker.setId(2L);
        weaker.setCategory(Category.CAFE);

        RecommendationRequest request = new RecommendationRequest();
        request.setUserId(9L);
        request.setPreferredCategories(List.of(Category.CAFE));

        BanditArmStat strongStats = new BanditArmStat();
        strongStats.setAlpha(9.0);
        strongStats.setBeta(1.0);

        BanditArmStat weakStats = new BanditArmStat();
        weakStats.setAlpha(1.0);
        weakStats.setBeta(9.0);

        BanditStatsRepository statsRepository = (BanditStatsRepository) Proxy.newProxyInstance(
                BanditStatsRepository.class.getClassLoader(),
                new Class[]{BanditStatsRepository.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("findByUserIdAndPoiId")) {
                        Long poiId = (Long) args[1];
                        return poiId == 1L ? Optional.of(strongStats) : Optional.of(weakStats);
                    }
                    if (method.getName().equals("findByUserId")) {
                        return List.of(strongStats, weakStats);
                    }
                    return null;
                }
        );

        BanditEventRepository eventRepository = (BanditEventRepository) Proxy.newProxyInstance(
                BanditEventRepository.class.getClassLoader(),
                new Class[]{BanditEventRepository.class},
                (proxy, method, args) -> Collections.emptyList()
        );

        ThompsonSamplingStrategy strategy = new ThompsonSamplingStrategy(eventRepository, statsRepository);

        Map<Poi, Double> scores = strategy.scorePois(request, List.of(preferred, weaker));

        assertEquals(List.of(preferred, weaker), scores.keySet().stream().toList());
    }
}