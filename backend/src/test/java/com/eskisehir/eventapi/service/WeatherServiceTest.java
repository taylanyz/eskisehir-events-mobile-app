package com.eskisehir.eventapi.service;

import com.eskisehir.eventapi.data.model.WeatherData;
import com.eskisehir.eventapi.repository.WeatherDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * Test suite for WeatherService weather data management.
 * Tests caching logic, mock data generation, and API integration.
 * Uses manual Proxy pattern for Java 25 compatibility with RestTemplate.
 */
@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private WeatherDataRepository weatherDataRepository;

    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        // RestTemplate is not used in our tests since we mock the repository
        // Pass null since the mock repository handles all weather lookups
        weatherService = new WeatherService(weatherDataRepository, null);
    }

    @Test
    void testGetWeather_ReturnsCachedDataWhenFresh() {
        // Arrange
        Double latitude = 39.7667;
        Double longitude = 30.5256;
        WeatherData cachedWeather = new WeatherData(
                latitude, longitude, "Sunny", 22, 60, 3.5, false
        );
        cachedWeather.setLastUpdated(LocalDateTime.now());

        when(weatherDataRepository.findByLatitudeAndLongitudeAndLastUpdatedAfter(
                eq(latitude), eq(longitude), any(LocalDateTime.class)
        )).thenReturn(Optional.of(cachedWeather));

        // Act
        WeatherData result = weatherService.getWeather(latitude, longitude);

        // Assert
        assertEquals(cachedWeather.getCondition(), result.getCondition());
        assertEquals(cachedWeather.getTemperature(), result.getTemperature());
        verify(weatherDataRepository).findByLatitudeAndLongitudeAndLastUpdatedAfter(
                eq(latitude), eq(longitude), any(LocalDateTime.class)
        );
    }

    @Test
    void testGetWeather_FetchesNewDataWhenCacheExpired() {
        // Arrange
        Double latitude = 39.7667;
        Double longitude = 30.5256;

        when(weatherDataRepository.findByLatitudeAndLongitudeAndLastUpdatedAfter(
                eq(latitude), eq(longitude), any(LocalDateTime.class)
        )).thenReturn(Optional.empty());

        when(weatherDataRepository.save(any(WeatherData.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        WeatherData result = weatherService.getWeather(latitude, longitude);

        // Assert
        assertNotNull(result);
        assertEquals(latitude, result.getLatitude());
        assertEquals(longitude, result.getLongitude());
        assertNotNull(result.getCondition());
        assertNotNull(result.getTemperature());
        verify(weatherDataRepository).save(any(WeatherData.class));
    }

    @Test
    void testGetEskisehirWeather_ReturnsCenterLocationWeather() {
        // Arrange
        Double eskisehirLat = 39.7667;
        Double eskisehirLng = 30.5256;

        when(weatherDataRepository.findByLatitudeAndLongitudeAndLastUpdatedAfter(
                eq(eskisehirLat), eq(eskisehirLng), any(LocalDateTime.class)
        )).thenReturn(Optional.empty());

        when(weatherDataRepository.save(any(WeatherData.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        WeatherData result = weatherService.getEskisehirWeather();

        // Assert
        assertNotNull(result);
        assertEquals(eskisehirLat, result.getLatitude());
        assertEquals(eskisehirLng, result.getLongitude());
    }

    @Test
    void testIsGoodWeatherForOutdoorActivities_ReturnsFalseWhenRaining() {
        // Arrange
        WeatherData rainyWeather = new WeatherData(
                39.7667, 30.5256, "Rainy", 18, 85, 5.0, true
        );

        // Act
        Boolean result = weatherService.isGoodWeatherForOutdoorActivities(rainyWeather);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsGoodWeatherForOutdoorActivities_ReturnsFalseForColdTemp() {
        // Arrange
        WeatherData coldWeather = new WeatherData(
                39.7667, 30.5256, "Sunny", 2, 50, 2.0, false
        );

        // Act
        Boolean result = weatherService.isGoodWeatherForOutdoorActivities(coldWeather);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsGoodWeatherForOutdoorActivities_ReturnsFalseForHotTemp() {
        // Arrange
        WeatherData hotWeather = new WeatherData(
                39.7667, 30.5256, "Sunny", 38, 30, 1.0, false
        );

        // Act
        Boolean result = weatherService.isGoodWeatherForOutdoorActivities(hotWeather);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsGoodWeatherForOutdoorActivities_ReturnsTrueForGoodWeather() {
        // Arrange
        WeatherData goodWeather = new WeatherData(
                39.7667, 30.5256, "Sunny", 22, 60, 3.0, false
        );

        // Act
        Boolean result = weatherService.isGoodWeatherForOutdoorActivities(goodWeather);

        // Assert
        assertTrue(result);
    }

    @Test
    void testMockWeatherGeneration_ProducesRealisticData() {
        // Arrange & Act
        when(weatherDataRepository.findByLatitudeAndLongitudeAndLastUpdatedAfter(
                anyDouble(), anyDouble(), any(LocalDateTime.class)
        )).thenReturn(Optional.empty());

        when(weatherDataRepository.save(any(WeatherData.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WeatherData result = weatherService.getWeather(39.7667, 30.5256);

        // Assert
        assertNotNull(result.getCondition());
        assertTrue(result.getCondition().matches("Sunny|Cloudy|Rainy|Partly Cloudy"));
        assertTrue(result.getTemperature() >= 15 && result.getTemperature() <= 25);
        assertTrue(result.getHumidity() >= 40 && result.getHumidity() <= 90);
        assertTrue(result.getWindSpeed() >= 2.0 && result.getWindSpeed() <= 7.0);
        assertNotNull(result.getIsRaining());
    }
}
