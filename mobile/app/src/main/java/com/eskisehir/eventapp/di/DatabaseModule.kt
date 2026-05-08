package com.eskisehir.eventapp.di

import android.content.Context
import androidx.room.Room
import com.eskisehir.eventapp.data.local.AppDatabase
import com.eskisehir.eventapp.data.local.dao.POIDAO
import com.eskisehir.eventapp.data.local.dao.CommentDAO
import com.eskisehir.eventapp.data.local.dao.UserEventDAO
import com.eskisehir.eventapp.data.local.dao.UserProfileDAO
import com.eskisehir.eventapp.data.repository.EventInteractionRepository
import com.eskisehir.eventapp.data.repository.POIRepository
import com.eskisehir.eventapp.data.repository.POIRepositoryImpl
import com.eskisehir.eventapp.data.repository.ProfileRepository
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
        ).addMigrations(AppDatabase.MIGRATION_1_2).build()
    }

    @Singleton
    @Provides
    fun providePOIDAO(database: AppDatabase): POIDAO = database.poiDAO()

    @Singleton
    @Provides
    fun provideCommentDAO(database: AppDatabase): CommentDAO = database.commentDAO()

    @Singleton
    @Provides
    fun provideUserEventDAO(database: AppDatabase): UserEventDAO = database.userEventDAO()

    @Singleton
    @Provides
    fun provideUserProfileDAO(database: AppDatabase): UserProfileDAO = database.userProfileDAO()

    @Singleton
    @Provides
    fun providePOIRepository(poiDAO: POIDAO): POIRepository = POIRepositoryImpl(poiDAO)

    @Singleton
    @Provides
    fun provideEventInteractionRepository(
        commentDAO: CommentDAO,
        userEventDAO: UserEventDAO
    ): EventInteractionRepository = EventInteractionRepository(commentDAO, userEventDAO)

    @Singleton
    @Provides
    fun provideProfileRepository(
        userProfileDAO: UserProfileDAO
    ): ProfileRepository = ProfileRepository(userProfileDAO)
}
