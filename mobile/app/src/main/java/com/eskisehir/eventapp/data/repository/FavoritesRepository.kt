package com.eskisehir.eventapp.data.repository

import com.eskisehir.eventapp.data.local.dao.FavoriteEventDAO
import com.eskisehir.eventapp.data.local.dao.FavoritePlaceDAO
import com.eskisehir.eventapp.data.local.entity.FavoriteEventEntity
import com.eskisehir.eventapp.data.local.entity.FavoritePlaceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor(
    private val favoriteEventDAO: FavoriteEventDAO,
    private val favoritePlaceDAO: FavoritePlaceDAO
) {
    // ── Favorite Events ──────────────────────────────────────────────────────
    fun getFavoriteEvents(userId: String): Flow<List<FavoriteEventEntity>> =
        favoriteEventDAO.getFavoriteEvents(userId)

    suspend fun isFavoriteEvent(userId: String, eventId: Long): Boolean =
        favoriteEventDAO.getFavoriteEvent(userId, eventId) != null

    suspend fun addFavoriteEvent(userId: String, eventId: Long) =
        favoriteEventDAO.insertFavoriteEvent(FavoriteEventEntity(userId, eventId))

    suspend fun removeFavoriteEvent(userId: String, eventId: Long) =
        favoriteEventDAO.deleteFavoriteEvent(userId, eventId)

    suspend fun toggleFavoriteEvent(userId: String, eventId: Long): Boolean {
        return if (isFavoriteEvent(userId, eventId)) {
            removeFavoriteEvent(userId, eventId)
            false
        } else {
            addFavoriteEvent(userId, eventId)
            true
        }
    }

    // ── Favorite Places ──────────────────────────────────────────────────────
    fun getFavoritePlaces(userId: String): Flow<List<FavoritePlaceEntity>> =
        favoritePlaceDAO.getFavoritePlaces(userId)

    suspend fun isFavoritePlace(userId: String, placeId: String): Boolean =
        favoritePlaceDAO.getFavoritePlace(userId, placeId) != null

    suspend fun addFavoritePlace(entity: FavoritePlaceEntity) =
        favoritePlaceDAO.insertFavoritePlace(entity)

    suspend fun removeFavoritePlace(id: Long, userId: String) =
        favoritePlaceDAO.deleteFavoritePlace(id, userId)
}
