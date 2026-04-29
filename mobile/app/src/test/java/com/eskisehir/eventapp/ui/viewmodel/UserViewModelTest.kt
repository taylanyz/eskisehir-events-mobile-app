package com.eskisehir.eventapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.eskisehir.eventapp.data.model.PreferenceUpdateRequest
import com.eskisehir.eventapp.data.model.UserResponse
import com.eskisehir.eventapp.domain.Result
import com.eskisehir.eventapp.domain.usecase.GetCurrentUserUseCase
import com.eskisehir.eventapp.domain.usecase.UpdatePreferencesUseCase
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
 * Unit tests for UserViewModel.
 * Tests getting user info and updating preferences.
 */
class UserViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    private lateinit var updatePreferencesUseCase: UpdatePreferencesUseCase

    private lateinit var viewModel: UserViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        getCurrentUserUseCase = mockk()
        updatePreferencesUseCase = mockk()

        viewModel = UserViewModel(
            getCurrentUserUseCase = getCurrentUserUseCase,
            updatePreferencesUseCase = updatePreferencesUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testGetCurrentUserSuccess() {
        // Arrange
        val userResponse = UserResponse(
            id = 1L,
            email = "user@example.com",
            displayName = "Test User",
            createdAt = "2024-01-01T10:00:00",
            lastLoginAt = "2024-01-02T15:30:00"
        )

        coEvery { getCurrentUserUseCase() } returns Result.Success(userResponse)

        // Act
        viewModel.getCurrentUser()

        // Assert
        coVerify { getCurrentUserUseCase() }

        assert(viewModel.userState.value is UserState.UserLoaded)
        assert((viewModel.userState.value as UserState.UserLoaded).user == userResponse)
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun testGetCurrentUserFailure() {
        // Arrange
        val exception = Exception("Failed to fetch user")

        coEvery { getCurrentUserUseCase() } returns Result.Error(exception, "Failed to get user")

        // Act
        viewModel.getCurrentUser()

        // Assert
        coVerify { getCurrentUserUseCase() }

        assert(viewModel.userState.value is UserState.Error)
        assert(viewModel.errorMessage.value == "Failed to get user")
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun testUpdatePreferencesSuccess() {
        // Arrange
        val request = PreferenceUpdateRequest(
            preferredCategories = listOf("CONCERT", "THEATER"),
            budgetSensitivity = "LOW",
            crowdTolerance = "HIGH",
            maxWalkingMinutes = 60
        )

        val updatedUser = UserResponse(
            id = 1L,
            email = "user@example.com",
            displayName = "Test User",
            createdAt = "2024-01-01T10:00:00"
        )

        coEvery { updatePreferencesUseCase(request) } returns Result.Success(updatedUser)

        // Act
        viewModel.updatePreferences(request)

        // Assert
        coVerify { updatePreferencesUseCase(request) }

        assert(viewModel.userState.value is UserState.PreferencesUpdated)
        assert((viewModel.userState.value as UserState.PreferencesUpdated).user == updatedUser)
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun testUpdatePreferencesFailure() {
        // Arrange
        val request = PreferenceUpdateRequest()
        val exception = Exception("Failed to update preferences")

        coEvery { updatePreferencesUseCase(request) } returns Result.Error(
            exception,
            "Failed to update preferences"
        )

        // Act
        viewModel.updatePreferences(request)

        // Assert
        coVerify { updatePreferencesUseCase(request) }

        assert(viewModel.userState.value is UserState.Error)
        assert(viewModel.errorMessage.value == "Failed to update preferences")
        assert(viewModel.isLoading.value == false)
    }

    @Test
    fun testClearError() {
        // Arrange - Create a failed request to generate error
        val exception = Exception("Test error")
        coEvery { getCurrentUserUseCase() } returns Result.Error(exception, "Test error")

        // Act
        viewModel.getCurrentUser()
        viewModel.clearError()

        // Assert
        assert(viewModel.errorMessage.value == null)
    }
}
