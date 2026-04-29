package com.eskisehir.eventapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO used to request recommendations from the backend.
 */
data class RecommendationRequest(
    val preferredCategories: List<Category>,
    val preferredTags: List<String>? = null,
    val maxPrice: Double? = null,
    val limit: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timeOfDay: String? = null,
    val dayOfWeek: String? = null,
    val mobilityPreference: MobilityPreference? = null
)
