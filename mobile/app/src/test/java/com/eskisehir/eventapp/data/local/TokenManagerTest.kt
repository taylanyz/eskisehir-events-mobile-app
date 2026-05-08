package com.eskisehir.eventapp.data.local

import com.eskisehir.eventapp.data.model.AuthResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class TokenManagerTest {

    private lateinit var tokenStore: TokenStore
    private lateinit var tokenManager: TokenManager

    @Before
    fun setUp() {
        tokenStore = mockk(relaxed = true)
        tokenManager = TokenManager(tokenStore)
    }

    @Test
    fun testSaveTokens() = runBlocking {
        val authResponse = AuthResponse(
            accessToken = "access_123",
            refreshToken = "refresh_123",
            userId = 1L,
            email = "user@test.com",
            displayName = "Test User"
        )
        coEvery { tokenStore.saveTokens(any(), any(), any(), any(), any()) } returns Unit

        tokenManager.saveTokens(authResponse)

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
    fun testUpdateAccessToken() = runBlocking {
        val newToken = "new_access_token"
        coEvery { tokenStore.updateAccessToken(newToken) } returns Unit

        tokenManager.updateAccessToken(newToken)

        coVerify { tokenStore.updateAccessToken(newToken) }
    }

    @Test
    fun testClearTokens() = runBlocking {
        coEvery { tokenStore.clearTokens() } returns Unit

        tokenManager.clearTokens()

        coVerify { tokenStore.clearTokens() }
    }
}
