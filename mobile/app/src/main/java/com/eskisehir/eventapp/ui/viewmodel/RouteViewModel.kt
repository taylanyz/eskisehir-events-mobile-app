package com.eskisehir.eventapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eskisehir.eventapp.data.model.RouteRequest
import com.eskisehir.eventapp.data.model.RouteResponse
import com.eskisehir.eventapp.domain.usecase.GenerateRouteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for route generation and display.
 * Manages state for route planning UI screens.
 */
@HiltViewModel
class RouteViewModel @Inject constructor(
    private val generateRouteUseCase: GenerateRouteUseCase
) : ViewModel() {

    private val _route = MutableStateFlow<RouteResponse?>(null)
    val route: StateFlow<RouteResponse?> = _route

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _selectedPoiIndex = MutableStateFlow(0)
    val selectedPoiIndex: StateFlow<Int> = _selectedPoiIndex

    /**
     * Generate a route from the given request.
     */
    fun generateRoute(
        eventIds: List<Long>,
        durationMinutes: Int?,
        maxWalkingMinutes: Int?,
        maxBudget: Double?,
        startLatitude: Double? = null,
        startLongitude: Double? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val request = RouteRequest(
                eventIds = eventIds,
                startLatitude = startLatitude,
                startLongitude = startLongitude,
                durationMinutes = durationMinutes,
                maxWalkingMinutes = maxWalkingMinutes,
                maxBudget = maxBudget,
                preferredCategories = null,
                mobilityPreference = "WALKING"
            )

            generateRouteUseCase.invoke(request)
                .onSuccess { response ->
                    _route.value = response
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Route generation failed"
                }

            _isLoading.value = false
        }
    }

    /**
     * Select a specific POI in the route to display details.
     */
    fun selectPoi(index: Int) {
        if (index >= 0 && index < (_route.value?.orderedPois?.size ?: 0)) {
            _selectedPoiIndex.value = index
        }
    }

    /**
     * Clear the current route.
     */
    fun clearRoute() {
        _route.value = null
        _selectedPoiIndex.value = 0
    }
}
