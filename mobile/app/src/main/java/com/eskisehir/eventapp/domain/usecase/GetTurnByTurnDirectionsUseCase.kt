package com.eskisehir.eventapp.domain.usecase

import com.eskisehir.eventapp.data.model.TurnByTurnNavigationResponse
import com.eskisehir.eventapp.data.remote.NavigationApi
import javax.inject.Inject

/**
 * Use case for getting turn-by-turn navigation directions.
 * Phase 4.6 mobile implementation.
 */
class GetTurnByTurnDirectionsUseCase @Inject constructor(
    private val navigationApi: NavigationApi
) {

    /**
     * Get turn-by-turn directions for a route.
     *
     * @param eventIds list of POI IDs to visit in order
     * @param startLatitude starting latitude (optional, uses Eskişehir center if null)
     * @param startLongitude starting longitude (optional, uses Eskişehir center if null)
     * @return Result containing navigation steps and metadata
     */
    suspend fun invoke(
        eventIds: List<Long>,
        startLatitude: Double? = null,
        startLongitude: Double? = null
    ): Result<TurnByTurnNavigationResponse> {
        return try {
            if (eventIds.isEmpty()) {
                return Result.failure(IllegalArgumentException("Event IDs cannot be empty"))
            }

            val eventIdsString = eventIds.joinToString(",")
            val response = navigationApi.getTurnByTurnDirections(
                eventIds = eventIdsString,
                startLatitude = startLatitude,
                startLongitude = startLongitude
            )

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
