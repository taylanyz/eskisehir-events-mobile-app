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
                    // Network error detected - showing mock data for debugging
                    _errorMessage.value = "⚠️ API Error: ${result.message ?: "Failed to load recommendations"}"
                    // Add mock data to help debug UI
                    _recommendations.value = getMockRecommendations()
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
                    // Network error detected - showing mock data for debugging
                    _errorMessage.value = "⚠️ API Error: ${result.message ?: "Failed to load trending"}"
                    // Add mock data to help debug UI
                    _trending.value = getMockTrending()
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

    private fun getMockTrending(): List<PoiResponse> {
        return listOf(
            PoiResponse(
                id = 1,
                name = "Hamamönü Café",
                description = "Popular local café",
                category = com.eskisehir.eventapp.data.model.Category.CAFE,
                district = "Çankaya",
                latitude = 39.7667,
                longitude = 30.5256,
                venue = "Café",
                date = null,
                price = 50.0,
                budgetLevel = "Budget",
                imageUrl = null,
                tags = listOf("local", "wifi"),
                estimatedVisitMinutes = 60,
                indoorOutdoor = "Indoor",
                familyFriendly = true,
                sustainabilityScore = 0.7,
                localBusinessScore = 0.9,
                crowdProxy = 0.5,
                popularityScore = 0.8,
                rankingScore = 4.5,
                weather = null
            ),
            PoiResponse(
                id = 2,
                name = "Kızılırmak Valley",
                description = "Beautiful valley park",
                category = com.eskisehir.eventapp.data.model.Category.PARK,
                district = "Keçiören",
                latitude = 39.7700,
                longitude = 30.5300,
                venue = "Park",
                date = null,
                price = null,
                budgetLevel = "Free",
                imageUrl = null,
                tags = listOf("outdoor", "nature"),
                estimatedVisitMinutes = 120,
                indoorOutdoor = "Outdoor",
                familyFriendly = true,
                sustainabilityScore = 0.95,
                localBusinessScore = 0.0,
                crowdProxy = 0.6,
                popularityScore = 0.9,
                rankingScore = 4.8,
                weather = null
            )
        )
    }

    private fun getMockRecommendations(): List<PoiResponse> {
        return listOf(
            PoiResponse(
                id = 3,
                name = "Ziraat Restaurant",
                description = "Traditional Turkish food",
                category = com.eskisehir.eventapp.data.model.Category.RESTAURANT,
                district = "Çankaya",
                latitude = 39.7700,
                longitude = 30.5200,
                venue = "Restaurant",
                date = null,
                price = 150.0,
                budgetLevel = "Mid-Range",
                imageUrl = null,
                tags = listOf("family", "traditional"),
                estimatedVisitMinutes = 90,
                indoorOutdoor = "Indoor",
                familyFriendly = true,
                sustainabilityScore = 0.6,
                localBusinessScore = 0.8,
                crowdProxy = 0.7,
                popularityScore = 0.75,
                rankingScore = 4.3,
                weather = null
            ),
            PoiResponse(
                id = 4,
                name = "Seğmenler Park",
                description = "Family-friendly park",
                category = com.eskisehir.eventapp.data.model.Category.PARK,
                district = "Çankaya",
                latitude = 39.7600,
                longitude = 30.5400,
                venue = "Park",
                date = null,
                price = null,
                budgetLevel = "Free",
                imageUrl = null,
                tags = listOf("family", "outdoor"),
                estimatedVisitMinutes = 100,
                indoorOutdoor = "Outdoor",
                familyFriendly = true,
                sustainabilityScore = 0.85,
                localBusinessScore = 0.0,
                crowdProxy = 0.5,
                popularityScore = 0.7,
                rankingScore = 4.6,
                weather = null
            )
        )
    }
}
