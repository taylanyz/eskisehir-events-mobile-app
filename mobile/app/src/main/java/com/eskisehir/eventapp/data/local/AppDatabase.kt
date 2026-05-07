package com.eskisehir.eventapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eskisehir.eventapp.data.model.POI
import com.eskisehir.eventapp.data.local.dao.POIDAO
import com.eskisehir.eventapp.data.local.converters.POIConverters

/**
 * Room Database Configuration for Eskişehir Events App
 * Manages all database entities and DAOs
 */
@Database(
    entities = [
        POI::class
        // Add other entities here: User::class, Route::class, etc.
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(POIConverters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun poiDAO(): POIDAO
    // abstract fun userDAO(): UserDAO
    // abstract fun routeDAO(): RouteDAO
    
    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        
        /**
         * Get singleton database instance
         */
        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        
        /**
         * Build and configure the database
         */
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "eskisehir-events.db"
            )
                .addMigrations()
                // .fallbackToDestructiveMigration() // Only for development!
                .build()
        }
    }
}
