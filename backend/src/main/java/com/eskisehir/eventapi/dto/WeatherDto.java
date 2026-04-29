package com.eskisehir.eventapi.dto;

/**
 * DTO for weather information included in API responses.
 * Sent with recommendation cards and POI details.
 */
public class WeatherDto {
    private String condition;
    private Integer temperature;
    private Integer humidity;
    private Double windSpeed;
    private Boolean isRaining;

    public WeatherDto() {
    }

    public WeatherDto(String condition, Integer temperature, Integer humidity,
                      Double windSpeed, Boolean isRaining) {
        this.condition = condition;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.isRaining = isRaining;
    }

    // Getters and Setters
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
}
