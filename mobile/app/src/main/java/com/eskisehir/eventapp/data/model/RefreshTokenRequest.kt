package com.eskisehir.eventapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * DTO for refresh token requests.
 * Used to exchange a refresh token for a new access token.
 */
data class RefreshTokenRequest(
    @SerializedName("refreshToken")
    val refreshToken: String
)
