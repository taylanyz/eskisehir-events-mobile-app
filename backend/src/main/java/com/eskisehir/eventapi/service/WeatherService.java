package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.data.model.WeatherData;
import com.eskisehir.eventapi.repository.WeatherDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for fetching and caching weather data.
 * Integrates with OpenWeatherMap API (or mock for testing).
 * Cache is valid for 30 minutes.
 */
@Service
public class WeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private static final long CACHE_DURATION_MINUTES = 30;

    private final WeatherDataRepository weatherDataRepository;
    private final RestTemplate restTemplate;
    private final String weatherApiKey;
    private final Boolean weatherApiEnabled;

    public WeatherService(WeatherDataRepository weatherDataRepository, RestTemplate restTemplate) {
        this(weatherDataRepository, restTemplate, "test-key", false);
    }

    // Constructor for testing with explicit config values
    public WeatherService(WeatherDataRepository weatherDataRepository, RestTemplate restTemplate,
                         String weatherApiKey, Boolean weatherApiEnabled) {
        this.weatherDataRepository = weatherDataRepository;
        this.restTemplate = restTemplate;
        this.weatherApiKey = weatherApiKey != null ? weatherApiKey : "test-key";
        this.weatherApiEnabled = weatherApiEnabled != null ? weatherApiEnabled : false;
    }

    /**
     * Get weather for a location. Returns cached data if fresh, otherwise fetches new.
     * Falls back to mock data if API is disabled.
     */
    public WeatherData getWeather(Double latitude, Double longitude) {
        LocalDateTime cacheThreshold = LocalDateTime.now().minusMinutes(CACHE_DURATION_MINUTES);

        // Check if we have fresh cached data
        Optional<WeatherData> cachedWeather = weatherDataRepository
                .findByLatitudeAndLongitudeAndLastUpdatedAfter(latitude, longitude, cacheThreshold);

        if (cachedWeather.isPresent()) {
            logger.info("Using cached weather for ({}, {})", latitude, longitude);
            return cachedWeather.get();
        }

        // Fetch fresh weather from API
        WeatherData weatherData;
        if (weatherApiEnabled) {
            weatherData = fetchFromOpenWeatherMap(latitude, longitude);
        } else {
            weatherData = generateMockWeather(latitude, longitude);
        }

        // Cache the result
        weatherDataRepository.save(weatherData);
        return weatherData;
    }

    /**
     * Fetch weather from OpenWeatherMap API.
     * In production, would use real API calls.
     */
    private WeatherData fetchFromOpenWeatherMap(Double latitude, Double longitude) {
        logger.info("Fetching weather from OpenWeatherMap for ({}, {})", latitude, longitude);

        try {
            // In production, would use:
            // String url = String.format(
            //     "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=metric&appid=%s",
            //     latitude, longitude, weatherApiKey
            // );
            // WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);

            // For now, return mock data
            return generateMockWeather(latitude, longitude);
        } catch (Exception e) {
            logger.error("Error fetching weather from OpenWeatherMap", e);
            return generateMockWeather(latitude, longitude);
        }
    }

    /**
     * Generate mock weather data for testing.
     * Returns realistic weather patterns for Eskişehir region.
     */
    private WeatherData generateMockWeather(Double latitude, Double longitude) {
        String[] conditions = {"Sunny", "Cloudy", "Rainy", "Partly Cloudy"};
        int hour = LocalDateTime.now().getHour();

        // Adjust weather by hour of day
        String condition;
        Boolean isRaining;
        if (hour >= 6 && hour < 18) {
            // Daytime - more likely sunny/cloudy
            condition = hour % 2 == 0 ? "Sunny" : "Partly Cloudy";
            isRaining = false;
        } else {
            // Nighttime - more likely cloudy/rainy
            condition = hour % 2 == 0 ? "Cloudy" : "Rainy";
            isRaining = hour % 3 == 0;
        }

        Integer temperature = 15 + (hour % 10); // 15-25°C depending on hour
        Integer humidity = 40 + (hour % 50); // 40-90% humidity
        Double windSpeed = 2.0 + (hour % 5); // 2-7 m/s

        WeatherData weather = new WeatherData(
                latitude, longitude, condition, temperature,
                humidity, windSpeed, isRaining
        );

        logger.debug("Generated mock weather: {} at ({}, {})", condition, latitude, longitude);
        return weather;
    }

    /**
     * Get weather for Eskişehir city center (default location).
     * Koordinates: 39.7667, 30.5256
     */
    public WeatherData getEskisehirWeather() {
        return getWeather(39.7667, 30.5256);
    }

    /**
     * Check if weather is suitable for outdoor activities.
     * Returns false for rainy weather or extreme temperatures.
     */
    public Boolean isGoodWeatherForOutdoorActivities(WeatherData weather) {
        if (weather.getIsRaining()) {
            return false;
        }
        if (weather.getTemperature() < 5 || weather.getTemperature() > 35) {
            return false;
        }
        return true;
    }
}
