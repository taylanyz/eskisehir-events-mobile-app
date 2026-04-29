package com.eskisehir.eventapp.data.model

/**
 * Advanced filter request for mobile Phase 5.3.
 */
data class AdvancedFilterRequestDto(
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val maxDistance: Double? = null, // km
    val latitude: Double? = null,
    val longitude: Double? = null,
    val minRating: Double? = null,
    val categories: List<String>? = null,
    val tags: List<String>? = null,
    val onlyOpen: Boolean? = null
)
