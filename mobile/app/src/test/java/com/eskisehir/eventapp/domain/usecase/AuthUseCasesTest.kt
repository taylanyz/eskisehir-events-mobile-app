package com.eskisehir.eventapp.domain.usecase

import com.eskisehir.eventapp.data.model.AuthResponse
import com.eskisehir.eventapp.data.remote.AuthApi
import com.eskisehir.eventapp.domain.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for Use Cases.
 * Tests business logic for authentication operations.
 */
class AuthUseCasesTest {

    private lateinit var authApi: AuthApi

    @Before
    fun setUp() {
        authApi = mockk()
    }

    @Test
    fun testLoginUseCaseSuccess() = runBlocking {
        // Arrange
        val email = "user@test.com"
        val password = "password123"
        val authResponse = AuthResponse(
            accessToken = "token",
            refreshToken = "refresh",
            userId = 1L,
            email = email,
            displayName = "Test"
        )

        val loginUseCase = LoginUseCase(authApi)
        coEvery { authApi.login(any()) } returns authResponse

        // Act
        val result = loginUseCase(email, password)

        // Assert
        coVerify { authApi.login(any()) }
        assert(result is Result.Success)
        assert((result as Result.Success).data == authResponse)
    }

    @Test
    fun testLoginUseCaseFailure() = runBlocking {
        // Arrange
        val email = "user@test.com"
        val password = "password123"
        val exception = Exception("Network error")

        val loginUseCase = LoginUseCase(authApi)
        coEvery { authApi.login(any()) } throws exception

        // Act
        val result = loginUseCase(email, password)

        // Assert
        coVerify { authApi.login(any()) }
        assert(result is Result.Error)
        assert((result as Result.Error).exception == exception)
    }

    @Test
    fun testRegisterUseCaseSuccess() = runBlocking {
        // Arrange
        val email = "newuser@test.com"
        val displayName = "New User"
        val password = "password123"
        val authResponse = AuthResponse(
            accessToken = "token",
            refreshToken = "refresh",
            userId = 2L,
            email = email,
            displayName = displayName
        )

        val registerUseCase = RegisterUseCase(authApi)
        coEvery { authApi.register(any()) } returns authResponse

        // Act
        val result = registerUseCase(email, displayName, password)

        // Assert
        coVerify { authApi.register(any()) }
        assert(result is Result.Success)
        assert((result as Result.Success).data == authResponse)
    }

    @Test
    fun testRefreshTokenUseCaseSuccess() = runBlocking {
        // Arrange
        val refreshToken = "refresh_token_123"
        val newAuthResponse = AuthResponse(
            accessToken = "new_token",
            refreshToken = refreshToken,
            userId = 1L,
            email = "user@test.com",
            displayName = "Test"
        )

        val refreshTokenUseCase = RefreshTokenUseCase(authApi)
        coEvery { authApi.refreshToken(any()) } returns newAuthResponse

        // Act
        val result = refreshTokenUseCase(refreshToken)

        // Assert
        coVerify { authApi.refreshToken(any()) }
        assert(result is Result.Success)
        assert((result as Result.Success).data == newAuthResponse)
    }
}
