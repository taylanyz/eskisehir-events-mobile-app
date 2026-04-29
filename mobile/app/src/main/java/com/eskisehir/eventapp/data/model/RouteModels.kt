package com.eskisehir.eventapp.data.model

/**
 * Mobile route response DTO matching backend RouteResponse.
 */
data class RouteResponse(
    val orderedPois: List<PoiResponse>,
    val totalDistanceKm: Double,
    val totalWalkingMinutes: Int,
    val estimatedCostTRY: Double,
    val routeStatus: String  // "FEASIBLE", "PARTIAL", "NOT_FEASIBLE"
)

data class RouteRequest(
    val eventIds: List<Long>,
    val startLatitude: Double?,
    val startLongitude: Double?,
    val durationMinutes: Int?,
    val maxWalkingMinutes: Int?,
    val maxBudget: Double?,
    val preferredCategories: List<String>?,
    val mobilityPreference: String?
)
