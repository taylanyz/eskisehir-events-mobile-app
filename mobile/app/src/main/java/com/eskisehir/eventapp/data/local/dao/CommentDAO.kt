package com.eskisehir.eventapp.data.local.dao

import androidx.room.*
import com.eskisehir.eventapp.data.local.entity.CommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDAO {
    @Query("SELECT * FROM comments WHERE eventId = :eventId ORDER BY timestamp DESC")
    fun getCommentsForEvent(eventId: Long): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Query("DELETE FROM comments WHERE id = :commentId AND userId = :userId")
    suspend fun deleteComment(commentId: Long, userId: String)
}
