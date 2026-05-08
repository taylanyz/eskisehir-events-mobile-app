package com.eskisehir.eventapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eskisehir.eventapp.data.local.TokenManager
import com.eskisehir.eventapp.data.local.entity.CommentEntity
import com.eskisehir.eventapp.data.model.UserEventStatus
import com.eskisehir.eventapp.data.repository.EventInteractionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventInteractionViewModel @Inject constructor(
    private val repository: EventInteractionRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // Current user info collected from TokenManager
    private val _userId = MutableStateFlow<String?>(null)
    private val _userDisplayName = MutableStateFlow<String?>(null)
    private val _userEmail = MutableStateFlow<String?>(null)

    val userId: StateFlow<String?> = _userId.asStateFlow()

    // Comments for the currently viewed event
    private val _comments = MutableStateFlow<List<CommentEntity>>(emptyList())
    val comments: StateFlow<List<CommentEntity>> = _comments.asStateFlow()

    // Current user's status for the currently viewed event
    private val _currentEventStatus = MutableStateFlow<UserEventStatus>(UserEventStatus.NONE)
    val currentEventStatus: StateFlow<UserEventStatus> = _currentEventStatus.asStateFlow()

    private val _commentText = MutableStateFlow("")
    val commentText: StateFlow<String> = _commentText.asStateFlow()

    init {
        viewModelScope.launch {
            tokenManager.userIdFlow.collect { _userId.value = it }
        }
        viewModelScope.launch {
            tokenManager.displayNameFlow.collect { _userDisplayName.value = it }
        }
        viewModelScope.launch {
            tokenManager.emailFlow.collect { _userEmail.value = it }
        }
    }

    fun loadCommentsForEvent(eventId: Long) {
        viewModelScope.launch {
            repository.getCommentsForEvent(eventId).collect { list ->
                _comments.value = list
            }
        }
    }

    fun loadStatusForEvent(eventId: Long) {
        viewModelScope.launch {
            val uid = _userId.value ?: return@launch
            val entity = repository.getUserEventStatus(uid, eventId)
            _currentEventStatus.value = when (entity?.status) {
                "ATTENDED"   -> UserEventStatus.ATTENDED
                "GOING"      -> UserEventStatus.GOING
                "WANT_TO_GO" -> UserEventStatus.WANT_TO_GO
                else         -> UserEventStatus.NONE
            }
        }
    }

    fun setEventStatus(eventId: Long, status: UserEventStatus) {
        viewModelScope.launch {
            val uid = _userId.value ?: return@launch
            if (status == UserEventStatus.NONE) {
                repository.removeUserEventStatus(uid, eventId)
            } else {
                repository.setUserEventStatus(uid, eventId, status.name)
            }
            _currentEventStatus.value = status
        }
    }

    fun onCommentTextChange(text: String) {
        _commentText.value = text
    }

    fun submitComment(eventId: Long) {
        val text = _commentText.value.trim()
        if (text.isEmpty()) return
        val uid = _userId.value ?: return
        viewModelScope.launch {
            val displayName = _userDisplayName.value ?: ""
            val email = _userEmail.value ?: ""
            repository.addComment(
                CommentEntity(
                    eventId = eventId,
                    userId = uid,
                    userDisplayName = displayName.ifEmpty { email },
                    userEmail = email,
                    content = text
                )
            )
            _commentText.value = ""
        }
    }

    fun deleteComment(commentId: Long) {
        val uid = _userId.value ?: return
        viewModelScope.launch {
            repository.deleteComment(commentId, uid)
        }
    }
}
