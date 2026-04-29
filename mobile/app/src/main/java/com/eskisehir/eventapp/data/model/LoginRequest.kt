package com.eskisehir.eventapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request DTO for user login.
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)
