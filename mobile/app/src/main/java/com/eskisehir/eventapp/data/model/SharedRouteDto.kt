package com.eskisehir.eventapp.data.model

/**
 * Data class representing a shared route with social metadata.
 */
data class SharedRouteDto(
    val id: Long,
    val name: String,
    val shareCode: String,
    val isPublic: Boolean,
    val averageRating: Double, // 0-5
    val totalRatings: Int,
    val shareCount: Int,
    val totalDistanceKm: Double,
    val totalDurationMinutes: Int,
    val estimatedBudgetTRY: Double
)

/**
 * Request DTO for sharing/unsharing a route.
 */
data class RouteShareRequestDto(
    val isPublic: Boolean,
    val shareMessage: String? = null
)
