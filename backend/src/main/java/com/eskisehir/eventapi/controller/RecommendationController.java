package com.eskisehir.eventapi.controller;

import com.eskisehir.eventapi.domain.model.Category;
import com.eskisehir.eventapi.dto.PoiResponse;
import com.eskisehir.eventapi.dto.RecommendationRequest;
import com.eskisehir.eventapi.dto.WeatherDto;
import com.eskisehir.eventapi.service.RecommendationEngine;
import com.eskisehir.eventapi.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {

    private final RecommendationEngine recommendationEngine;
    private final WeatherService weatherService;

    public RecommendationController(RecommendationEngine recommendationEngine, WeatherService weatherService) {
        this.recommendationEngine = recommendationEngine;
        this.weatherService = weatherService;
    }

    @PostMapping
    public ResponseEntity<List<PoiResponse>> getRecommendations(
            @Valid @RequestBody RecommendationRequest request) {
        return ResponseEntity.ok(toResponseList(recommendationEngine.getRecommendationScores(request)));
    }

    @GetMapping
    public ResponseEntity<List<PoiResponse>> getRecommendationsByQuery(
            @RequestParam(required = false, defaultValue = "10") Integer count,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Category categoryFilter,
            @RequestParam(required = false) Long userId) {
        RecommendationRequest request = new RecommendationRequest();
        request.setLimit(count);
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setUserId(userId);
        if (categoryFilter != null) {
            request.setPreferredCategories(Collections.singletonList(categoryFilter));
        }

        return ResponseEntity.ok(toResponseList(recommendationEngine.getRecommendationScores(request)));
    }

    @GetMapping("/trending")
    public ResponseEntity<List<PoiResponse>> getTrending(
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<PoiResponse> trending = recommendationEngine.getTrending(limit).stream()
                .map(poi -> {
                    PoiResponse response = PoiResponse.fromEntity(poi);
                    addWeatherData(response, poi.getLatitude(), poi.getLongitude());
                    return response;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(trending);
    }

    /**
     * Add weather data to POI response based on its location.
     */
    private void addWeatherData(PoiResponse poi, Double latitude, Double longitude) {
        try {
            var weather = weatherService.getWeather(latitude, longitude);
            poi.setWeather(new WeatherDto(
                    weather.getCondition(),
                    weather.getTemperature(),
                    weather.getHumidity(),
                    weather.getWindSpeed(),
                    weather.getIsRaining()
            ));
        } catch (Exception e) {
            // If weather service fails, continue without weather data
        }
    }

    private List<PoiResponse> toResponseList(Map<com.eskisehir.eventapi.domain.model.Poi, Double> scoredRecommendations) {
        return scoredRecommendations.entrySet().stream()
                .map(entry -> {
                    PoiResponse response = PoiResponse.fromEntity(entry.getKey(), entry.getValue());
                    addWeatherData(response, entry.getKey().getLatitude(), entry.getKey().getLongitude());
                    return response;
                })
                .collect(Collectors.toList());
    }
}
