package com.eskisehir.eventapp.data.local.dao

import androidx.room.*
import com.eskisehir.eventapp.data.local.entity.FavoriteEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteEventDAO {
    @Query("SELECT * FROM favorite_events WHERE userId = :userId ORDER BY addedAt DESC")
    fun getFavoriteEvents(userId: String): Flow<List<FavoriteEventEntity>>

    @Query("SELECT * FROM favorite_events WHERE userId = :userId AND eventId = :eventId LIMIT 1")
    suspend fun getFavoriteEvent(userId: String, eventId: Long): FavoriteEventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteEvent(entity: FavoriteEventEntity)

    @Query("DELETE FROM favorite_events WHERE userId = :userId AND eventId = :eventId")
    suspend fun deleteFavoriteEvent(userId: String, eventId: Long)
}
