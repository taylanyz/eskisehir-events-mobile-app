package com.eskisehir.eventapp.domain.usecase

import com.eskisehir.eventapp.data.local.TokenManager
import com.eskisehir.eventapp.data.model.InteractionRequest
import com.eskisehir.eventapp.data.model.InteractionResponse
import com.eskisehir.eventapp.data.model.InteractionType
import com.eskisehir.eventapp.data.remote.InteractionApi
import com.eskisehir.eventapp.domain.Result
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDateTime
import javax.inject.Inject

class LogInteractionUseCase @Inject constructor(
    private val interactionApi: InteractionApi,
    private val tokenManager: TokenManager
) {

    suspend fun logView(poiId: Long): Result<InteractionResponse> =
        submitInteraction(poiId = poiId, interactionType = InteractionType.VIEW)

    suspend fun logOpen(poiId: Long): Result<InteractionResponse> =
        submitInteraction(poiId = poiId, interactionType = InteractionType.CLICK)

    suspend fun logBookmark(poiId: Long): Result<InteractionResponse> =
        submitInteraction(poiId = poiId, interactionType = InteractionType.SAVE)

    suspend fun logShare(poiId: Long): Result<InteractionResponse> =
        submitInteraction(poiId = poiId, interactionType = InteractionType.SHARE)

    suspend fun logFeedback(
        poiId: Long,
        rating: Int?,
        isHelpful: Boolean?,
        comment: String?
    ): Result<InteractionResponse> {
        val interactionType = if (isHelpful == true || (rating != null && rating >= 4)) {
            InteractionType.POSITIVE_FEEDBACK
        } else {
            InteractionType.DISLIKE
        }

        val normalizedComment = listOfNotNull(
            rating?.let { "rating=$it" },
            isHelpful?.let { "helpful=$it" },
            comment?.takeIf { it.isNotBlank() }
        ).joinToString("; ").ifBlank { null }

        return submitInteraction(
            poiId = poiId,
            interactionType = interactionType,
            comment = normalizedComment
        )
    }

    private suspend fun submitInteraction(
        poiId: Long,
        interactionType: InteractionType,
        comment: String? = null
    ): Result<InteractionResponse> {
        val userId = tokenManager.userIdFlow.firstOrNull()?.toLongOrNull()
            ?: return Result.Error(IllegalStateException("User is not logged in"), "Kullanıcı oturumu bulunamadı")

        val now = LocalDateTime.now()
        val request = InteractionRequest(
            userId = userId,
            poiId = poiId,
            interactionType = interactionType,
            comment = comment,
            timeOfDay = now.toLocalTime().toString(),
            dayOfWeek = now.dayOfWeek.name
        )

        return try {
            Result.Success(interactionApi.logInteraction(request))
        } catch (exception: Exception) {
            Result.Error(exception, "Interaction logging failed: ${exception.message}")
        }
    }
}