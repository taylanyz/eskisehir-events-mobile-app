package com.eskisehir.eventapp.data.model

/**
 * Error response from API.
 * Used for handling HTTP error responses.
 */
data class ErrorResponse(
    val timestamp: String? = null,
    val status: Int? = null,
    val error: String? = null,
    val message: String? = null,
    val path: String? = null
)
