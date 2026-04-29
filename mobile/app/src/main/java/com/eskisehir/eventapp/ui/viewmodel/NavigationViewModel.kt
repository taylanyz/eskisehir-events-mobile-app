package com.eskisehir.eventapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eskisehir.eventapp.data.model.NavigationStepDto
import com.eskisehir.eventapp.data.model.TurnByTurnNavigationResponse
import com.eskisehir.eventapp.domain.usecase.GetTurnByTurnDirectionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for turn-by-turn navigation (Phase 4.6).
 * Manages navigation state including current step, location, and progress.
 */
@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val getTurnByTurnDirectionsUseCase: GetTurnByTurnDirectionsUseCase
) : ViewModel() {

    private val _navigationData = MutableStateFlow<TurnByTurnNavigationResponse?>(null)
    val navigationData: StateFlow<TurnByTurnNavigationResponse?> = _navigationData

    private val _currentStepIndex = MutableStateFlow(0)
    val currentStepIndex: StateFlow<Int> = _currentStepIndex

    private val _currentLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val currentLocation: StateFlow<Pair<Double, Double>?> = _currentLocation

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isNavigationActive = MutableStateFlow(false)
    val isNavigationActive: StateFlow<Boolean> = _isNavigationActive

    /**
     * Start turn-by-turn navigation for the given POI IDs.
     */
    fun startNavigation(
        eventIds: List<Long>,
        startLatitude: Double? = null,
        startLongitude: Double? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            getTurnByTurnDirectionsUseCase.invoke(eventIds, startLatitude, startLongitude)
                .onSuccess { response ->
                    _navigationData.value = response
                    _currentStepIndex.value = 0
                    _isNavigationActive.value = true
                    // Set initial location to first step start
                    if (response.steps.isNotEmpty()) {
                        _currentLocation.value = Pair(
                            response.steps[0].startLatitude,
                            response.steps[0].startLongitude
                        )
                    }
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Failed to load directions"
                }

            _isLoading.value = false
        }
    }

    /**
     * Update current location (from GPS).
     * Automatically detects if user has reached next waypoint.
     */
    fun updateCurrentLocation(latitude: Double, longitude: Double) {
        _currentLocation.value = Pair(latitude, longitude)

        // Check if user has reached the next waypoint
        _navigationData.value?.let { nav ->
            if (_currentStepIndex.value < nav.steps.size) {
                val currentStep = nav.steps[_currentStepIndex.value]
                val distance = distanceBetweenPoints(
                    latitude, longitude,
                    currentStep.endLatitude, currentStep.endLongitude
                )

                // If within 50 meters, advance to next step
                if (distance < 0.05) {
                    advanceToNextStep()
                }
            }
        }
    }

    /**
     * Advance to the next navigation step.
     */
    fun advanceToNextStep() {
        _navigationData.value?.let { nav ->
            if (_currentStepIndex.value < nav.steps.size - 1) {
                _currentStepIndex.value += 1
            } else {
                // Reached destination
                _isNavigationActive.value = false
            }
        }
    }

    /**
     * Go back to previous step.
     */
    fun previousStep() {
        if (_currentStepIndex.value > 0) {
            _currentStepIndex.value -= 1
        }
    }

    /**
     * Stop navigation and clear state.
     */
    fun stopNavigation() {
        _isNavigationActive.value = false
        _currentStepIndex.value = 0
        _navigationData.value = null
        _currentLocation.value = null
    }

    /**
     * Calculate distance between two points in kilometers (Haversine).
     */
    private fun distanceBetweenPoints(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val R = 6371.0  // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }

    /**
     * Get current step details.
     */
    fun getCurrentStep(): NavigationStepDto? {
        return _navigationData.value?.steps?.getOrNull(_currentStepIndex.value)
    }

    /**
     * Get remaining distance and time to destination.
     */
    fun getRemainingMetrics(): Pair<Double, Int>? {
        _navigationData.value?.let { nav ->
            val currentStep = _currentStepIndex.value
            var remainingDistance = 0.0
            var remainingTime = 0

            for (i in currentStep until nav.steps.size) {
                remainingDistance += nav.steps[i].distanceKm
                remainingTime += nav.steps[i].durationMinutes
            }

            return Pair(remainingDistance, remainingTime)
        }
        return null
    }
}
