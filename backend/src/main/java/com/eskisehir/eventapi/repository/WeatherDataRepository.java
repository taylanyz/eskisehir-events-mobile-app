package com.eskisehir.eventapi.repository;

import com.eskisehir.eventapi.data.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for accessing WeatherData entities.
 * Supports caching and time-based queries.
 */
@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    /**
     * Find weather data for a specific location.
     * Returns the most recent entry if multiple exist.
     */
    Optional<WeatherData> findByLatitudeAndLongitudeOrderByLastUpdatedDesc(Double latitude, Double longitude);

    /**
     * Find weather data updated after a specific time.
     * Used to check cache freshness.
     */
    Optional<WeatherData> findByLatitudeAndLongitudeAndLastUpdatedAfter(
            Double latitude, Double longitude, LocalDateTime threshold
    );

    /**
     * Find Eskişehir center weather (used for default when no location specified).
     */
    Optional<WeatherData> findByLatitudeAndLongitude(Double latitude, Double longitude);
}
