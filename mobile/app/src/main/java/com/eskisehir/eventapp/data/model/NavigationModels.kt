package com.eskisehir.eventapp.data.model

/**
 * DTO for navigation step response.
 */
data class NavigationStepDto(
    val stepNumber: Int,
    val fromPoiId: Long?,
    val toPoiId: Long?,
    val fromPoiName: String?,
    val toPoiName: String?,
    val instruction: String,
    val distanceKm: Double,
    val durationMinutes: Int,
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double
)

/**
 * DTO for turn-by-turn navigation response.
 */
data class TurnByTurnNavigationResponse(
    val routeId: Long?,
    val steps: List<NavigationStepDto>,
    val totalDistanceKm: Double,
    val totalDurationMinutes: Int,
    val currentStepIndex: Int
)
