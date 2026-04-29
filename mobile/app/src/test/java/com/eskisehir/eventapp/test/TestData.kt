package com.eskisehir.eventapp.test

import com.eskisehir.eventapp.data.model.AuthResponse

/**
 * Test utilities and mock data for testing.
 */
object TestData {

    fun createAuthResponse(
        accessToken: String = "test_access_token",
        refreshToken: String = "test_refresh_token",
        userId: Long = 1L,
        email: String = "test@example.com",
        displayName: String = "Test User"
    ): AuthResponse {
        return AuthResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            userId = userId,
            email = email,
            displayName = displayName
        )
    }
}
