package com.eskisehir.eventapp.data.model

import java.time.LocalDateTime

/**
 * Data class representing a rating given to a route.
 */
data class RouteRatingDto(
    val id: Long,
    val rating: Double, // 1-5 stars
    val comment: String?,
    val userName: String,
    val createdAt: String
)

/**
 * Request DTO for submitting/updating a route rating.
 */
data class RouteRatingRequestDto(
    val rating: Double, // 1-5 stars
    val comment: String? = null
)
