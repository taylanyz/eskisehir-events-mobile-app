package com.eskisehir.eventapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eskisehir.eventapp.data.model.PreferenceUpdateRequest
import com.eskisehir.eventapp.data.model.UserResponse
import com.eskisehir.eventapp.domain.Result
import com.eskisehir.eventapp.domain.usecase.GetCurrentUserUseCase
import com.eskisehir.eventapp.domain.usecase.UpdatePreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for user profile and preferences operations.
 * Manages user information and preference updates.
 */
@HiltViewModel
class UserViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updatePreferencesUseCase: UpdatePreferencesUseCase
) : ViewModel() {

    private val _userState = MutableStateFlow<UserState>(UserState.Idle)
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    fun getCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _userState.value = UserState.Loading

            val result = getCurrentUserUseCase()
            when (result) {
                is Result.Success -> {
                    _userState.value = UserState.UserLoaded(result.data)
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _userState.value = UserState.Error(result.message ?: "Failed to get user")
                    _errorMessage.value = result.message ?: "Failed to get user"
                    _isLoading.value = false
                }
                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun updatePreferences(request: PreferenceUpdateRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _userState.value = UserState.Loading

            val result = updatePreferencesUseCase(request)
            when (result) {
                is Result.Success -> {
                    _userState.value = UserState.PreferencesUpdated(result.data)
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _userState.value = UserState.Error(result.message ?: "Failed to update preferences")
                    _errorMessage.value = result.message ?: "Failed to update preferences"
                    _isLoading.value = false
                }
                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

/**
 * Sealed class representing different user-related states.
 */
sealed class UserState {
    object Idle : UserState()
    object Loading : UserState()
    data class UserLoaded(val user: UserResponse) : UserState()
    data class PreferencesUpdated(val user: UserResponse) : UserState()
    data class Error(val message: String) : UserState()
}
