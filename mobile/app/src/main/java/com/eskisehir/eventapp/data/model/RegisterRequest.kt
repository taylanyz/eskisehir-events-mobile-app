package com.eskisehir.eventapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request DTO for user registration.
 */
data class RegisterRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("password")
    val password: String
)
