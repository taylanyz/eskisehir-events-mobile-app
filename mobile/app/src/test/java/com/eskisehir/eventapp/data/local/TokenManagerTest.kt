package com.eskisehir.eventapp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.eskisehir.eventapp.data.model.AuthResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for TokenManager.
 * Tests token storage and retrieval operations.
 */
class TokenManagerTest {

    private lateinit var tokenStore: TokenStore
    private lateinit var tokenManager: TokenManager

    @Before
    fun setUp() {
        tokenStore = mockk(relaxed = true)
        tokenManager = TokenManager(tokenStore)
    }

    @Test
    fun testSaveTokens() {
        // Arrange
        val authResponse = AuthResponse(
            accessToken = "access_123",
            refreshToken = "refresh_123",
            userId = 1L,
            email = "user@test.com",
            displayName = "Test User"
        )

        coEvery { tokenStore.saveTokens(any(), any(), any(), any(), any()) } returns Unit

        // Act
        tokenManager.saveTokens(authResponse)

        // Assert
        coVerify {
            tokenStore.saveTokens(
                authResponse.accessToken,
                authResponse.refreshToken,
                authResponse.userId.toString(),
                authResponse.email,
                authResponse.displayName
            )
        }
    }

    @Test
    fun testUpdateAccessToken() {
        // Arrange
        val newToken = "new_access_token"

        coEvery { tokenStore.updateAccessToken(newToken) } returns Unit

        // Act
        tokenManager.updateAccessToken(newToken)

        // Assert
        coVerify { tokenStore.updateAccessToken(newToken) }
    }

    @Test
    fun testClearTokens() {
        // Arrange
        coEvery { tokenStore.clearTokens() } returns Unit

        // Act
        tokenManager.clearTokens()

        // Assert
        coVerify { tokenStore.clearTokens() }
    }
}
