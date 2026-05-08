package com.eskisehir.eventapp.di

import android.content.Context
import androidx.room.Room
import com.eskisehir.eventapp.data.local.AppDatabase
import com.eskisehir.eventapp.data.local.dao.*
import com.eskisehir.eventapp.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "eskisehir-events.db"
        ).addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3).build()
    }

    @Singleton @Provides fun providePOIDAO(db: AppDatabase): POIDAO = db.poiDAO()
    @Singleton @Provides fun provideCommentDAO(db: AppDatabase): CommentDAO = db.commentDAO()
    @Singleton @Provides fun provideUserEventDAO(db: AppDatabase): UserEventDAO = db.userEventDAO()
    @Singleton @Provides fun provideUserProfileDAO(db: AppDatabase): UserProfileDAO = db.userProfileDAO()
    @Singleton @Provides fun provideFavoriteEventDAO(db: AppDatabase): FavoriteEventDAO = db.favoriteEventDAO()
    @Singleton @Provides fun provideFavoritePlaceDAO(db: AppDatabase): FavoritePlaceDAO = db.favoritePlaceDAO()

    @Singleton @Provides
    fun providePOIRepository(poiDAO: POIDAO): POIRepository = POIRepositoryImpl(poiDAO)

    @Singleton @Provides
    fun provideEventInteractionRepository(
        commentDAO: CommentDAO,
        userEventDAO: UserEventDAO
    ): EventInteractionRepository = EventInteractionRepository(commentDAO, userEventDAO)

    @Singleton @Provides
    fun provideProfileRepository(userProfileDAO: UserProfileDAO): ProfileRepository =
        ProfileRepository(userProfileDAO)

    @Singleton @Provides
    fun provideFavoritesRepository(
        favoriteEventDAO: FavoriteEventDAO,
        favoritePlaceDAO: FavoritePlaceDAO
    ): FavoritesRepository = FavoritesRepository(favoriteEventDAO, favoritePlaceDAO)
}
