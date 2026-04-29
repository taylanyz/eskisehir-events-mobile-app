package com.eskisehir.eventapi.data.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Weather data entity for caching weather information.
 * Updated periodically (every 30 minutes) via scheduled task.
 */
@Entity
@Table(name = "weather_data")
public class WeatherData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String condition; // "Sunny", "Rainy", "Cloudy", "Snowy", etc.

    @Column(nullable = false)
    private Integer temperature; // Celsius

    @Column(nullable = false)
    private Integer humidity; // 0-100%

    @Column(nullable = false)
    private Double windSpeed; // m/s

    @Column(nullable = false)
    private Boolean isRaining;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    public WeatherData() {
    }

    public WeatherData(Double latitude, Double longitude, String condition, Integer temperature,
                       Integer humidity, Double windSpeed, Boolean isRaining) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.condition = condition;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.isRaining = isRaining;
        this.timestamp = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Integer getHumidity() {
        return humidity;
    }

    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Boolean getIsRaining() {
        return isRaining;
    }

    public void setIsRaining(Boolean isRaining) {
        this.isRaining = isRaining;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
