package com.eskisehir.eventapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eskisehir.eventapp.data.local.TokenManager
import com.eskisehir.eventapp.data.model.AuthResponse
import com.eskisehir.eventapp.domain.Result
import com.eskisehir.eventapp.domain.usecase.LoginUseCase
import com.eskisehir.eventapp.domain.usecase.LogoutUseCase
import com.eskisehir.eventapp.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for authentication operations (login, register, logout).
 * Manages authentication state and user credentials.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage

    val isUserLoggedInFlow: Flow<Boolean> = tokenManager.accessTokenFlow.let { flow ->
        MutableStateFlow(false)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _authState.value = AuthState.Loading

            val result = loginUseCase(email, password)
            when (result) {
                is Result.Success -> {
                    tokenManager.saveTokens(result.data)
                    _authState.value = AuthState.LoginSuccess(result.data)
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _authState.value = AuthState.Error(result.message ?: "Login failed")
                    _errorMessage.value = result.message ?: "Login failed"
                    _isLoading.value = false
                }
                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun register(email: String, displayName: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _authState.value = AuthState.Loading

            val result = registerUseCase(email, displayName, password)
            when (result) {
                is Result.Success -> {
                    tokenManager.saveTokens(result.data)
                    _authState.value = AuthState.RegisterSuccess(result.data)
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _authState.value = AuthState.Error(result.message ?: "Registration failed")
                    _errorMessage.value = result.message ?: "Registration failed"
                    _isLoading.value = false
                }
                is Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = logoutUseCase()
            when (result) {
                is Result.Success -> {
                    _authState.value = AuthState.LogoutSuccess
                    _isLoading.value = false
                }
                is Result.Error -> {
                    _errorMessage.value = result.message ?: "Logout failed"
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
 * Sealed class representing different authentication states.
 */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class LoginSuccess(val authResponse: AuthResponse) : AuthState()
    data class RegisterSuccess(val authResponse: AuthResponse) : AuthState()
    object LogoutSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}
