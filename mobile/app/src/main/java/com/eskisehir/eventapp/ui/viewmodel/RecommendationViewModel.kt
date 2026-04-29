package com.eskisehir.eventapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eskisehir.eventapp.data.model.PoiResponse
import com.eskisehir.eventapp.data.model.RecommendationRequest
import com.eskisehir.eventapp.domain.Result
import com.eskisehir.eventapp.domain.usecase.GetRecommendationsUseCase
import com.eskisehir.eventapp.domain.usecase.GetTrendingUseCase
import com.eskisehir.eventapp.domain.usecase.LogInteractionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val getRecommendationsUseCase: GetRecommendationsUseCase,
    private val getTrendingUseCase: GetTrendingUseCase,
    private val logInteractionUseCase: LogInteractionUseCase
) : ViewModel() {

    private val _recommendations = MutableStateFlow<List<PoiResponse>>(emptyList())
    val recommendations: StateFlow<List<PoiResponse>> = _recommendations

    private val _trending = MutableStateFlow<List<PoiResponse>>(emptyList())
    val trending: StateFlow<List<PoiResponse>> = _trending

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _interactionMessage = MutableStateFlow<String?>(null)
    val interactionMessage: StateFlow<String?> = _interactionMessage

    fun loadRecommendations(request: RecommendationRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = getRecommendationsUseCase(request)) {
                is Result.Success -> {
                    _recommendations.value = result.data
                }
                is Result.Error -> {
                    _errorMessage.value = result.message ?: "Recommendation loading failed"
                }
                else -> {
                    _errorMessage.value = "Unknown recommendation error"
                }
            }

            _isLoading.value = false
        }
    }

    fun loadTrending(limit: Int = 8) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = getTrendingUseCase(limit)) {
                is Result.Success -> {
                    _trending.value = result.data
                }
                is Result.Error -> {
                    _errorMessage.value = result.message ?: "Trending loading failed"
                }
                else -> {
                    _errorMessage.value = "Unknown trending error"
                }
            }

            _isLoading.value = false
        }
    }

    fun defaultRequest(): RecommendationRequest {
        val now = LocalDateTime.now()
        return RecommendationRequest(
            preferredCategories = listOf(
                com.eskisehir.eventapp.data.model.Category.CAFE,
                com.eskisehir.eventapp.data.model.Category.RESTAURANT,
                com.eskisehir.eventapp.data.model.Category.PARK
            ),
            preferredTags = listOf("local", "family", "outdoor"),
            maxPrice = 200.0,
            limit = 10,
            latitude = 39.7667,
            longitude = 30.5256,
            timeOfDay = now.toLocalTime().toString(),
            dayOfWeek = now.dayOfWeek.name,
            mobilityPreference = com.eskisehir.eventapp.data.model.MobilityPreference.WALKING
        )
    }

    fun logView(poiId: Long) {
        viewModelScope.launch {
            when (val result = logInteractionUseCase.logView(poiId)) {
                is Result.Error -> _errorMessage.value = result.message ?: "Interaction logging failed"
                else -> Unit
            }
        }
    }

    fun logOpen(poiId: Long) {
        viewModelScope.launch {
            when (val result = logInteractionUseCase.logOpen(poiId)) {
                is Result.Error -> _errorMessage.value = result.message ?: "Interaction logging failed"
                else -> Unit
            }
        }
    }

    fun logBookmark(poiId: Long) {
        viewModelScope.launch {
            when (val result = logInteractionUseCase.logBookmark(poiId)) {
                is Result.Success -> _interactionMessage.value = "Mekan kaydedildi"
                is Result.Error -> _errorMessage.value = result.message ?: "Kaydetme başarısız"
                else -> Unit
            }
        }
    }

    fun logShare(poiId: Long) {
        viewModelScope.launch {
            when (val result = logInteractionUseCase.logShare(poiId)) {
                is Result.Success -> _interactionMessage.value = "Paylaşım etkileşimi kaydedildi"
                is Result.Error -> _errorMessage.value = result.message ?: "Paylaşım başarısız"
                else -> Unit
            }
        }
    }

    fun logFeedback(poiId: Long, rating: Int?, isHelpful: Boolean?, comment: String?) {
        viewModelScope.launch {
            when (val result = logInteractionUseCase.logFeedback(poiId, rating, isHelpful, comment)) {
                is Result.Success -> _interactionMessage.value = "Geri bildirim gönderildi"
                is Result.Error -> _errorMessage.value = result.message ?: "Geri bildirim gönderilemedi"
                else -> Unit
            }
        }
    }

    fun clearInteractionMessage() {
        _interactionMessage.value = null
    }
}
