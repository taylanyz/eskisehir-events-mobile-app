package com.eskisehir.eventapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for successful login/authentication.
 * Contains JWT access and refresh tokens along with user info.
 */
data class AuthResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("tokenType")
    val tokenType: String = "Bearer",
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("email")
    val email: String,
    @SerializedName("displayName")
    val displayName: String
)
