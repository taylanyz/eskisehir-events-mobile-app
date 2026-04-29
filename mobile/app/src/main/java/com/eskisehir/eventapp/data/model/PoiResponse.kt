package com.eskisehir.eventapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO for POI recommendation responses from the backend.
 */
data class PoiResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val category: Category,
    val district: String?,
    val latitude: Double,
    val longitude: Double,
    val venue: String?,
    val date: String?,
    val price: Double?,
    val budgetLevel: String?,
    val imageUrl: String?,
    val tags: List<String>?,
    val estimatedVisitMinutes: Int?,
    val indoorOutdoor: String?,
    val familyFriendly: Boolean?,
    val sustainabilityScore: Double?,
    val localBusinessScore: Double?,
    val crowdProxy: Double?,
    val popularityScore: Double?,
    val rankingScore: Double?,
    val weather: WeatherDto?
)
