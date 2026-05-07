package com.eskisehir.eventapp.di

import android.content.Context
import androidx.room.Room
import com.eskisehir.eventapp.data.local.AppDatabase
import com.eskisehir.eventapp.data.local.dao.POIDAO
import com.eskisehir.eventapp.data.repository.POIRepository
import com.eskisehir.eventapp.data.repository.POIRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt Module for Database Dependencies
 * Provides singleton instances for database and repositories
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provide singleton AppDatabase instance
     */
    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "eskisehir-events.db"
        )
            .addMigrations()
            .build()
    }
    
    /**
     * Provide singleton POI DAO
     */
    @Singleton
    @Provides
    fun providePOIDAO(database: AppDatabase): POIDAO {
        return database.poiDAO()
    }
    
    /**
     * Provide singleton POI Repository
     */
    @Singleton
    @Provides
    fun providePOIRepository(
        poiDAO: POIDAO
    ): POIRepository {
        return POIRepositoryImpl(poiDAO)
    }
}
