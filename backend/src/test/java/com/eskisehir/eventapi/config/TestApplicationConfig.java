package com.eskisehir.eventapi.config;

import com.eskisehir.eventapi.repository.WeatherDataRepository;
import com.eskisehir.eventapi.service.WeatherService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

/**
 * Test configuration providing beans for integration tests.
 * Provides non-mocked WeatherService to avoid Java 25 + Mockito incompatibility.
 */
@TestConfiguration
public class TestApplicationConfig {

    @Bean
    @Primary
    public WeatherService weatherService(WeatherDataRepository weatherDataRepository) {
        // Return WeatherService with API disabled (uses mock weather generation)
        return new WeatherService(weatherDataRepository, null, "test-key", false);
    }

    @Bean
    public RestTemplate testRestTemplate() {
        return new RestTemplate();
    }
}
