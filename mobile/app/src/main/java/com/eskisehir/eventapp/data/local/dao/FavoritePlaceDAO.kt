package com.eskisehir.eventapp.data.local.dao

import androidx.room.*
import com.eskisehir.eventapp.data.local.entity.FavoritePlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritePlaceDAO {
    @Query("SELECT * FROM favorite_places WHERE userId = :userId ORDER BY addedAt DESC")
    fun getFavoritePlaces(userId: String): Flow<List<FavoritePlaceEntity>>

    @Query("SELECT * FROM favorite_places WHERE userId = :userId AND placeId = :placeId LIMIT 1")
    suspend fun getFavoritePlace(userId: String, placeId: String): FavoritePlaceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoritePlace(entity: FavoritePlaceEntity)

    @Query("DELETE FROM favorite_places WHERE id = :id AND userId = :userId")
    suspend fun deleteFavoritePlace(id: Long, userId: String)
}
