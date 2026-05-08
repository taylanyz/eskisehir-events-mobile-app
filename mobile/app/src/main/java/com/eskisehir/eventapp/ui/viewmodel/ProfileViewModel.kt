package com.eskisehir.eventapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eskisehir.eventapp.data.local.TokenManager
import com.eskisehir.eventapp.data.model.Event
import com.eskisehir.eventapp.data.model.SampleData
import com.eskisehir.eventapp.data.repository.EventInteractionRepository
import com.eskisehir.eventapp.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val profileRepository: ProfileRepository,
    private val eventInteractionRepository: EventInteractionRepository
) : ViewModel() {

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId.asStateFlow()

    private val _displayName = MutableStateFlow<String?>(null)
    val displayName: StateFlow<String?> = _displayName.asStateFlow()

    private val _email = MutableStateFlow<String?>(null)
    val email: StateFlow<String?> = _email.asStateFlow()

    private val _interestAreas = MutableStateFlow<List<String>>(emptyList())
    val interestAreas: StateFlow<List<String>> = _interestAreas.asStateFlow()

    private val _attendedEvents = MutableStateFlow<List<Event>>(emptyList())
    val attendedEvents: StateFlow<List<Event>> = _attendedEvents.asStateFlow()

    private val _goingEvents = MutableStateFlow<List<Event>>(emptyList())
    val goingEvents: StateFlow<List<Event>> = _goingEvents.asStateFlow()

    private val _wantToGoEvents = MutableStateFlow<List<Event>>(emptyList())
    val wantToGoEvents: StateFlow<List<Event>> = _wantToGoEvents.asStateFlow()

    // For interest areas editing dialog
    private val _showInterestsDialog = MutableStateFlow(false)
    val showInterestsDialog: StateFlow<Boolean> = _showInterestsDialog.asStateFlow()

    init {
        viewModelScope.launch {
            tokenManager.userIdFlow.collect { uid ->
                _userId.value = uid
                if (uid != null) {
                    loadProfile(uid)
                    loadCategorizedEvents(uid)
                }
            }
        }
        viewModelScope.launch {
            tokenManager.displayNameFlow.collect { _displayName.value = it }
        }
        viewModelScope.launch {
            tokenManager.emailFlow.collect { _email.value = it }
        }
    }

    private fun loadProfile(userId: String) {
        viewModelScope.launch {
            profileRepository.getUserProfile(userId).collect { entity ->
                val interests = if (entity != null) {
                    profileRepository.parseInterests(entity.interestAreas)
                } else emptyList()
                _interestAreas.value = interests
            }
        }
    }

    private fun loadCategorizedEvents(userId: String) {
        viewModelScope.launch {
            eventInteractionRepository.getEventsByStatus(userId, "ATTENDED").collect { entities ->
                _attendedEvents.value = entities.mapNotNull { e ->
                    SampleData.events.find { it.id == e.eventId }
                }
            }
        }
        viewModelScope.launch {
            eventInteractionRepository.getEventsByStatus(userId, "GOING").collect { entities ->
                _goingEvents.value = entities.mapNotNull { e ->
                    SampleData.events.find { it.id == e.eventId }
                }
            }
        }
        viewModelScope.launch {
            eventInteractionRepository.getEventsByStatus(userId, "WANT_TO_GO").collect { entities ->
                _wantToGoEvents.value = entities.mapNotNull { e ->
                    SampleData.events.find { it.id == e.eventId }
                }
            }
        }
    }

    fun saveInterestAreas(selected: List<String>) {
        val uid = _userId.value ?: return
        viewModelScope.launch {
            profileRepository.saveInterestAreas(uid, selected)
            _showInterestsDialog.value = false
        }
    }

    fun openInterestsDialog() { _showInterestsDialog.value = true }
    fun closeInterestsDialog() { _showInterestsDialog.value = false }
}
