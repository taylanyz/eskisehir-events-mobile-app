package com.eskisehir.eventapp.data.remote

import com.eskisehir.eventapp.data.model.TurnByTurnNavigationResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for turn-by-turn navigation endpoints.
 * Phase 4.6 mobile API integration.
 */
interface NavigationApi {

    /**
     * Get turn-by-turn directions for a route.
     * GET /api/routes/directions
     *
     * @param eventIds comma-separated list of POI IDs
     * @param startLatitude optional start latitude (defaults to Eskişehir center)
     * @param startLongitude optional start longitude (defaults to Eskişehir center)
     * @return step-by-step navigation instructions
     */
    @GET("routes/directions")
    suspend fun getTurnByTurnDirections(
        @Query("eventIds") eventIds: String,
        @Query("startLatitude") startLatitude: Double? = null,
        @Query("startLongitude") startLongitude: Double? = null
    ): TurnByTurnNavigationResponse
}
