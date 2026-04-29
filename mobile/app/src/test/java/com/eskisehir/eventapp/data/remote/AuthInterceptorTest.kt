package com.eskisehir.eventapp.data.remote

import com.eskisehir.eventapp.data.local.TokenManager
import com.eskisehir.eventapp.data.model.AuthResponse
import com.eskisehir.eventapp.data.model.RefreshTokenRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for AuthInterceptor.
 * Tests token injection and 401 refresh logic.
 */
class AuthInterceptorTest {

    private lateinit var tokenManager: TokenManager
    private lateinit var authApi: AuthApi
    private lateinit var interceptor: AuthInterceptor

    @Before
    fun setUp() {
        tokenManager = mockk(relaxed = true)
        authApi = mockk(relaxed = true)
        interceptor = AuthInterceptor(tokenManager, authApi)
    }

    @Test
    fun testInterceptorAddsAuthorizationHeader() {
        // Arrange
        val accessToken = "test_token_123"
        val originalRequest = mockk<Request>(relaxed = true)
        val requestWithToken = mockk<Request>(relaxed = true)
        val response = mockk<Response>(relaxed = true)

        val chain = mockk<Interceptor.Chain> {
            coEvery { request() } returns originalRequest
            coEvery { proceed(requestWithToken) } returns response
        }

        coEvery { tokenManager.getAccessToken() } returns accessToken
        coEvery { originalRequest.newBuilder() } returns mockk {
            coEvery { header("Authorization", "Bearer $accessToken") } returns mockk {
                coEvery { build() } returns requestWithToken
            }
        }

        // Act
        val result = interceptor.intercept(chain)

        // Assert
        assert(result == response)
        coVerify { originalRequest.newBuilder() }
    }

    @Test
    fun testInterceptorHandles401Response() {
        // Arrange
        val newAccessToken = "new_token_456"
        val refreshToken = "refresh_token_789"
        val originalRequest = mockk<Request>(relaxed = true)
        val response401 = mockk<Response> {
            every { code } returns 401
            coEvery { close() } returns Unit
        }
        val successResponse = mockk<Response> {
            every { code } returns 200
        }

        val authResponse = AuthResponse(
            accessToken = newAccessToken,
            refreshToken = refreshToken,
            userId = 1L,
            email = "test@test.com",
            displayName = "Test"
        )

        val chain = mockk<Interceptor.Chain> {
            coEvery { request() } returns originalRequest
            coEvery { proceed(originalRequest) } returns response401 andThen successResponse
        }

        coEvery { tokenManager.getAccessToken() } returns "old_token"
        coEvery { tokenManager.getRefreshToken() } returns refreshToken
        coEvery { authApi.refreshToken(RefreshTokenRequest(refreshToken)) } returns authResponse
        coEvery { tokenManager.updateAccessToken(newAccessToken) } returns Unit

        // Act
        val result = interceptor.intercept(chain)

        // Assert
        coVerify { authApi.refreshToken(any()) }
        coVerify { tokenManager.updateAccessToken(newAccessToken) }
    }
}
