package com.eskisehir.eventapp.data.remote

import com.eskisehir.eventapp.data.model.AuthResponse
import com.eskisehir.eventapp.data.model.LoginRequest
import com.eskisehir.eventapp.data.model.RefreshTokenRequest
import com.eskisehir.eventapp.data.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit API interface for authentication endpoints.
 */
interface AuthApi {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): AuthResponse
}
