package com.eskisehir.eventapp.domain.usecase

import com.eskisehir.eventapp.data.model.PoiResponse
import com.eskisehir.eventapp.data.model.RecommendationRequest
import com.eskisehir.eventapp.data.remote.RecommendationApi
import com.eskisehir.eventapp.domain.Result
import javax.inject.Inject

/**
 * Use case for fetching personalized POI recommendations from the backend.
 */
class GetRecommendationsUseCase @Inject constructor(
    private val recommendationApi: RecommendationApi
) {

    suspend operator fun invoke(request: RecommendationRequest): Result<List<PoiResponse>> {
        return try {
            val response = recommendationApi.getRecommendations(request)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e, "Failed to load recommendations: ${e.message}")
        }
    }
}
