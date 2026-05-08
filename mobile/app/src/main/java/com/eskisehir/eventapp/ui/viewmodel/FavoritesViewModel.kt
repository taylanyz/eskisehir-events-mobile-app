package com.eskisehir.eventapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eskisehir.eventapp.data.local.TokenManager
import com.eskisehir.eventapp.data.repository.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    init {
        viewModelScope.launch {
            tokenManager.userIdFlow.collect { _userId.value = it }
        }
    }

    fun loadFavoriteState(eventId: Long) {
        viewModelScope.launch {
            val uid = _userId.value ?: return@launch
            _isFavorite.value = favoritesRepository.isFavoriteEvent(uid, eventId)
        }
    }

    fun toggleFavorite(eventId: Long) {
        val uid = _userId.value ?: return
        viewModelScope.launch {
            val isNowFavorite = favoritesRepository.toggleFavoriteEvent(uid, eventId)
            _isFavorite.value = isNowFavorite
        }
    }
}
