package com.eskisehir.eventapp.data.remote

import com.eskisehir.eventapp.data.model.UserResponse
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Integration tests for UserApi using MockWebServer.
 * Tests user profile and preference endpoints.
 */
class UserApiIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var userApi: UserApi
    private val gson = Gson()

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        userApi = retrofit.create(UserApi::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testGetCurrentUserSuccess() = runBlocking {
        // Arrange
        val response = UserResponse(
            id = 1L,
            email = "user@test.com",
            displayName = "Test User",
            createdAt = "2024-01-01T10:00:00",
            lastLoginAt = "2024-01-02T15:30:00"
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(response))
        )

        // Act
        val result = userApi.getCurrentUser()

        // Assert
        assert(result.id == 1L)
        assert(result.email == "user@test.com")
        assert(result.displayName == "Test User")

        val request = mockWebServer.takeRequest()
        assert(request.path == "/users/me")
        assert(request.method == "GET")
    }

    @Test
    fun testGetCurrentUserUnauthorized() = runBlocking {
        // Arrange
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("""{"status": 401, "message": "Unauthorized"}""")
        )

        // Act & Assert
        try {
            userApi.getCurrentUser()
            assert(false) // Should have thrown exception
        } catch (e: Exception) {
            assert(true) // Expected
        }
    }

    @Test
    fun testUpdatePreferencesSuccess() = runBlocking {
        // Arrange
        val request = com.eskisehir.eventapp.data.model.PreferenceUpdateRequest(
            preferredCategories = listOf("CONCERT", "THEATER"),
            budgetSensitivity = "LOW",
            maxWalkingMinutes = 60
        )

        val response = UserResponse(
            id = 1L,
            email = "user@test.com",
            displayName = "Test User",
            createdAt = "2024-01-01T10:00:00"
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(gson.toJson(response))
        )

        // Act
        val result = userApi.updatePreferences(request)

        // Assert
        assert(result.id == 1L)
        assert(result.email == "user@test.com")

        val httpRequest = mockWebServer.takeRequest()
        assert(httpRequest.path == "/users/preferences")
        assert(httpRequest.method == "PUT")
    }
}
