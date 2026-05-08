package com.eskisehir.eventapp.data.repository

import com.eskisehir.eventapp.data.local.dao.CommentDAO
import com.eskisehir.eventapp.data.local.dao.UserEventDAO
import com.eskisehir.eventapp.data.local.entity.CommentEntity
import com.eskisehir.eventapp.data.local.entity.UserEventEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventInteractionRepository @Inject constructor(
    private val commentDAO: CommentDAO,
    private val userEventDAO: UserEventDAO
) {
    fun getCommentsForEvent(eventId: Long): Flow<List<CommentEntity>> =
        commentDAO.getCommentsForEvent(eventId)

    suspend fun addComment(comment: CommentEntity) =
        commentDAO.insertComment(comment)

    suspend fun deleteComment(commentId: Long, userId: String) =
        commentDAO.deleteComment(commentId, userId)

    suspend fun getUserEventStatus(userId: String, eventId: Long): UserEventEntity? =
        userEventDAO.getUserEventStatus(userId, eventId)

    suspend fun setUserEventStatus(userId: String, eventId: Long, status: String) =
        userEventDAO.upsertUserEvent(UserEventEntity(userId, eventId, status))

    suspend fun removeUserEventStatus(userId: String, eventId: Long) =
        userEventDAO.deleteUserEvent(userId, eventId)

    fun getEventsByStatus(userId: String, status: String): Flow<List<UserEventEntity>> =
        userEventDAO.getEventsByStatus(userId, status)
}
