package com.eskisehir.eventapp.data.remote

import com.eskisehir.eventapp.data.model.AuthResponse
import com.eskisehir.eventapp.data.model.LoginRequest
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Integration tests for AuthApi using MockWebServer.
 * Tests actual Retrofit API calls with mocked server responses.
 */
class AuthApiIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var authApi: AuthApi
    private val gson = Gson()

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        authApi = retrofit.create(AuthApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testLoginEndpointSuccess() = runBlocking {
        // Arrange
        val email = "user@test.com"
        val password = "password123"
        val response = AuthResponse(
            accessToken = "access_token_123",
            refreshToken = "refresh_token_123",
            userId = 1L,
            email = email,
            displayName = "Test User"
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(response))
        )

        // Act
        val result = authApi.login(LoginRequest(email, password))

        // Assert
        assert(result.accessToken == response.accessToken)
        assert(result.email == email)
        assert(result.userId == 1L)

        val request = mockWebServer.takeRequest()
        assert(request.path == "/auth/login")
        assert(request.method == "POST")
    }

    @Test
    fun testLoginEndpointError() = runBlocking {
        // Arrange
        val errorResponse = """{"status": 401, "message": "Invalid credentials"}"""

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody(errorResponse)
        )

        // Act & Assert
        try {
            authApi.login(LoginRequest("user@test.com", "wrongpassword"))
            assert(false) // Should have thrown exception
        } catch (e: Exception) {
            assert(true) // Expected
        }
    }

    @Test
    fun testRegisterEndpointSuccess() = runBlocking {
        // Arrange
        val email = "newuser@test.com"
        val displayName = "New User"
        val response = AuthResponse(
            accessToken = "access_token_new",
            refreshToken = "refresh_token_new",
            userId = 2L,
            email = email,
            displayName = displayName
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(response))
        )

        // Act
        val result = authApi.register(
            com.eskisehir.eventapp.data.model.RegisterRequest(email, displayName, "password123")
        )

        // Assert
        assert(result.userId == 2L)
        assert(result.email == email)

        val request = mockWebServer.takeRequest()
        assert(request.path == "/auth/register")
        assert(request.method == "POST")
    }

    @Test
    fun testRefreshTokenEndpointSuccess() = runBlocking {
        // Arrange
        val refreshToken = "old_refresh_token"
        val newResponse = AuthResponse(
            accessToken = "new_access_token",
            refreshToken = refreshToken,
            userId = 1L,
            email = "user@test.com",
            displayName = "Test"
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(newResponse))
        )

        // Act
        val result = authApi.refreshToken(
            com.eskisehir.eventapp.data.model.RefreshTokenRequest(refreshToken)
        )

        // Assert
        assert(result.accessToken == "new_access_token")
        assert(result.refreshToken == refreshToken)

        val request = mockWebServer.takeRequest()
        assert(request.path == "/auth/refresh")
        assert(request.method == "POST")
    }
}
