package com.eskisehir.eventapp.data.remote

import com.eskisehir.eventapp.data.model.InteractionRequest
import com.eskisehir.eventapp.data.model.InteractionResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface InteractionApi {

    @POST("interactions")
    suspend fun logInteraction(@Body request: InteractionRequest): InteractionResponse
}