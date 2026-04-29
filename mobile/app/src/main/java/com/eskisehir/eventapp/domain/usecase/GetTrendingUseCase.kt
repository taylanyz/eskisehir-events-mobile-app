package com.eskisehir.eventapp.domain.usecase

import com.eskisehir.eventapp.data.model.PoiResponse
import com.eskisehir.eventapp.data.remote.RecommendationApi
import com.eskisehir.eventapp.domain.Result
import javax.inject.Inject

class GetTrendingUseCase @Inject constructor(
    private val recommendationApi: RecommendationApi
) {

    suspend operator fun invoke(limit: Int): Result<List<PoiResponse>> {
        return try {
            val response = recommendationApi.getTrending(limit)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e, "Failed to load trending recommendations: ${e.message}")
        }
    }
}
