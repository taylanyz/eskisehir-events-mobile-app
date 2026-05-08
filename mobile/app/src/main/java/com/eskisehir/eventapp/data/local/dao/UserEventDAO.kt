package com.eskisehir.eventapp.data.local.dao

import androidx.room.*
import com.eskisehir.eventapp.data.local.entity.UserEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserEventDAO {
    @Query("SELECT * FROM user_events WHERE userId = :userId AND status = :status")
    fun getEventsByStatus(userId: String, status: String): Flow<List<UserEventEntity>>

    @Query("SELECT * FROM user_events WHERE userId = :userId AND eventId = :eventId LIMIT 1")
    suspend fun getUserEventStatus(userId: String, eventId: Long): UserEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserEvent(entity: UserEventEntity)

    @Query("DELETE FROM user_events WHERE userId = :userId AND eventId = :eventId")
    suspend fun deleteUserEvent(userId: String, eventId: Long)
}
