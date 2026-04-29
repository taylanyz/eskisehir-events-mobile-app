package com.eskisehir.eventapp.data.model

/**
 * Weather data DTO matching backend response structure.
 * Displayed with recommendation cards and event details.
 */
data class WeatherDto(
    val condition: String,
    val temperature: Int,
    val humidity: Int,
    val windSpeed: Double,
    val isRaining: Boolean
)
