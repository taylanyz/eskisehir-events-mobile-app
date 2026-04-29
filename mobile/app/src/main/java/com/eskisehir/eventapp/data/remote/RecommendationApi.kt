package com.eskisehir.eventapp.data.remote

import com.eskisehir.eventapp.data.model.PoiResponse
import com.eskisehir.eventapp.data.model.RecommendationRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RecommendationApi {

    @POST("recommendations")
    suspend fun getRecommendations(@Body request: RecommendationRequest): List<PoiResponse>

    @GET("recommendations/trending")
    suspend fun getTrending(@Query("limit") limit: Int = 10): List<PoiResponse>
}
