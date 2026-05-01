package com.eskisehir.eventapi.algorithm;

import com.eskisehir.eventapi.domain.model.Category;
import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.dto.RecommendationRequest;
import com.eskisehir.eventapi.repository.PoiRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ContentBasedCandidateGenerator implements RecommendationCandidateGenerator {

    private static final double MAX_DISTANCE_KM = 20.0;

    private final PoiRepository poiRepository;

    public ContentBasedCandidateGenerator(PoiRepository poiRepository) {
        this.poiRepository = poiRepository;
    }

    @Override
    public List<Poi> generateCandidates(RecommendationRequest request) {
        List<Poi> activePois = poiRepository.findByIsActiveTrue();

        return activePois.stream()
                .filter(poi -> matchesCategory(poi, request))
                .filter(poi -> matchesTags(poi, request))
                .filter(poi -> matchesBudget(poi, request))
                .filter(poi -> matchesLocation(poi, request))
                .collect(Collectors.toList());
    }

    private boolean matchesCategory(Poi poi, RecommendationRequest request) {
        if (request.getPreferredCategories() == null || request.getPreferredCategories().isEmpty()) {
            return true;
        }
        return request.getPreferredCategories().contains(poi.getCategory());
    }

    private boolean matchesTags(Poi poi, RecommendationRequest request) {
        if (request.getPreferredTags() == null || request.getPreferredTags().isEmpty()) {
            return true;
        }
        if (poi.getTags() == null || poi.getTags().isEmpty()) {
            return false;
        }
        return poi.getTags().stream()
                .anyMatch(tag -> request.getPreferredTags().stream()
                        .anyMatch(prefTag -> prefTag.equalsIgnoreCase(tag)));
    }

    private boolean matchesBudget(Poi poi, RecommendationRequest request) {
        if (request.getMaxPrice() == null || poi.getPrice() == null) {
            return true;
        }
        return poi.getPrice() <= request.getMaxPrice();
    }

    private boolean matchesLocation(Poi poi, RecommendationRequest request) {
        if (request.getLatitude() == null || request.getLongitude() == null) {
            return true;
        }
        return haversineDistance(request.getLatitude(), request.getLongitude(),
                poi.getLatitude(), poi.getLongitude()) <= MAX_DISTANCE_KM;
    }

    private double haversineDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (Objects.isNull(lat1) || Objects.isNull(lon1) || Objects.isNull(lat2) || Objects.isNull(lon2)) {
            return Double.MAX_VALUE;
        }

        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
