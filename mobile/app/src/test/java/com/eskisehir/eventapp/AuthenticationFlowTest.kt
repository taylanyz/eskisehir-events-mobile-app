package com.eskisehir.eventapp

import com.eskisehir.eventapp.data.local.TokenManager
import com.eskisehir.eventapp.data.model.AuthResponse
import com.eskisehir.eventapp.data.remote.AuthApi
import com.eskisehir.eventapp.data.remote.UserApi
import com.eskisehir.eventapp.domain.usecase.LoginUseCase
import com.eskisehir.eventapp.domain.usecase.LogoutUseCase
import com.eskisehir.eventapp.domain.usecase.RegisterUseCase
import com.eskisehir.eventapp.test.TestData
import com.eskisehir.eventapp.ui.viewmodel.AuthState
import com.eskisehir.eventapp.ui.viewmodel.AuthViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.eskisehir.eventapp.domain.Result

/**
 * End-to-end authentication flow test.
 * Tests complete user journey: Register → Login → GetUser → Logout
 */
class AuthenticationFlowTest {

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private lateinit var authApi: AuthApi
    private lateinit var userApi: UserApi
    private lateinit var tokenManager: TokenManager
    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        authApi = mockk()
        userApi = mockk()
        tokenManager = mockk(relaxed = true)

        val loginUseCase = LoginUseCase(authApi)
        val registerUseCase = RegisterUseCase(authApi)
        val logoutUseCase = LogoutUseCase(tokenManager)

        authViewModel = AuthViewModel(
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
    fun testCompleteAuthenticationFlow() = runTest {
        // Step 1: Register new user
        val newUserEmail = "newuser@example.com"
        val newUserName = "New User"
        val newUserPassword = "password123"

        val registerResponse = TestData.createAuthResponse(
            email = newUserEmail,
            displayName = newUserName,
            userId = 123L
        )

        coEvery { authApi.register(any()) } returns registerResponse
        coEvery { tokenManager.saveTokens(registerResponse) } returns Unit

        authViewModel.register(newUserEmail, newUserName, newUserPassword)

        assert(authViewModel.authState.value is AuthState.RegisterSuccess)
        assert(authViewModel.isLoading.value == false)

        // Step 2: Login existing user
        val email = "user@example.com"
        val password = "password123"

        val loginResponse = TestData.createAuthResponse(
            email = email,
            userId = 1L
        )

        coEvery { authApi.login(any()) } returns loginResponse
        coEvery { tokenManager.saveTokens(loginResponse) } returns Unit

        authViewModel.login(email, password)

        assert(authViewModel.authState.value is AuthState.LoginSuccess)
        assert(authViewModel.isLoading.value == false)

        // Step 3: Logout
        coEvery { tokenManager.clearTokens() } returns Unit

        authViewModel.logout()

        assert(authViewModel.authState.value is AuthState.LogoutSuccess)
        assert(authViewModel.isLoading.value == false)
    }

    @Test
    fun testLoginFailureThenRetry() = runTest {
        // Step 1: Failed login
        val email = "user@example.com"
        val wrongPassword = "wrongpassword"

        val exception = Exception("Invalid credentials")
        coEvery { authApi.login(any()) } throws exception

        authViewModel.login(email, wrongPassword)

        assert(authViewModel.authState.value is AuthState.Error)
        assert(authViewModel.errorMessage.value?.contains("Login failed") == true)

        // Step 2: Retry with correct password
        val correctPassword = "correctpassword"
        val loginResponse = TestData.createAuthResponse(email = email)

        coEvery { authApi.login(any()) } returns loginResponse
        coEvery { tokenManager.saveTokens(loginResponse) } returns Unit

        authViewModel.login(email, correctPassword)

        assert(authViewModel.authState.value is AuthState.LoginSuccess)
        assert(authViewModel.isLoading.value == false)
    }
}
