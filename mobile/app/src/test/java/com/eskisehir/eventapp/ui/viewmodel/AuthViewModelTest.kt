package com.eskisehir.eventapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.eskisehir.eventapp.data.local.TokenManager
import com.eskisehir.eventapp.data.model.AuthResponse
import com.eskisehir.eventapp.domain.Result
import com.eskisehir.eventapp.domain.usecase.LoginUseCase
import com.eskisehir.eventapp.domain.usecase.LogoutUseCase
import com.eskisehir.eventapp.domain.usecase.RegisterUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for AuthViewModel.
 * Tests login, register, logout, and error handling flows.
 */
class AuthViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var tokenManager: TokenManager

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        loginUseCase = mockk()
        registerUseCase = mockk()
        logoutUseCase = mockk()
        tokenManager = mockk(relaxed = true)

        viewModel = AuthViewModel(
            loginUseCase = loginUseCase,
            registerUseCase = registerUseCase,
            logoutUseCase = logoutUseCase,
            tokenManager = tokenManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLoginSuccess() {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val authResponse = AuthResponse(
            accessToken = "access_token_123",
            refreshToken = "refresh_token_123",
            userId = 1L,
            email = email,
            displayName = "Test User"
        )

        coEvery { loginUseCase(email, password) } returns Result.Success(authResponse)
        coEvery { tokenManager.saveTokens(authResponse) } returns Unit

        // Act
        viewModel.login(email, password)

        // Assert
        coVerify { loginUseCase(email, password) }
        coVerify { tokenManager.saveTokens(authResponse) }

        assert(viewModel.authState.value is AuthState.LoginSuccess)
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun testLoginFailure() {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val exception = Exception("Invalid credentials")

        coEvery { loginUseCase(email, password) } returns Result.Error(exception, "Login failed")

        // Act
        viewModel.login(email, password)

        // Assert
        coVerify { loginUseCase(email, password) }

        assert(viewModel.authState.value is AuthState.Error)
        assert(viewModel.errorMessage.value == "Login failed")
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun testRegisterSuccess() {
        // Arrange
        val email = "newuser@example.com"
        val displayName = "New User"
        val password = "password123"
        val authResponse = AuthResponse(
            accessToken = "access_token_123",
            refreshToken = "refresh_token_123",
            userId = 2L,
            email = email,
            displayName = displayName
        )

        coEvery { registerUseCase(email, displayName, password) } returns Result.Success(authResponse)
        coEvery { tokenManager.saveTokens(authResponse) } returns Unit

        // Act
        viewModel.register(email, displayName, password)

        // Assert
        coVerify { registerUseCase(email, displayName, password) }
        coVerify { tokenManager.saveTokens(authResponse) }

        assert(viewModel.authState.value is AuthState.RegisterSuccess)
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun testLogoutSuccess() {
        // Arrange
        coEvery { logoutUseCase() } returns Result.Success(Unit)

        // Act
        viewModel.logout()

        // Assert
        coVerify { logoutUseCase() }

        assert(viewModel.authState.value is AuthState.LogoutSuccess)
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun testClearError() {
        // Arrange - Create a failed login to generate error
        val exception = Exception("Test error")
        coEvery { loginUseCase("test@test.com", "wrong") } returns Result.Error(exception, "Test error")

        // Act
        viewModel.login("test@test.com", "wrong")
        viewModel.clearError()

        // Assert
        assert(viewModel.errorMessage.value == null)
    }
}
