package com.eskisehir.eventapp.data.remote

import com.eskisehir.eventapp.data.model.PreferenceUpdateRequest
import com.eskisehir.eventapp.data.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

/**
 * Retrofit API interface for user endpoints.
 */
interface UserApi {

    @GET("users/me")
    suspend fun getCurrentUser(): UserResponse

    @PUT("users/preferences")
    suspend fun updatePreferences(@Body request: PreferenceUpdateRequest): UserResponse
}
