package com.eskisehir.eventapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.eskisehir.eventapp.data.model.POI
import com.eskisehir.eventapp.data.local.dao.POIDAO
import com.eskisehir.eventapp.data.local.dao.CommentDAO
import com.eskisehir.eventapp.data.local.dao.UserEventDAO
import com.eskisehir.eventapp.data.local.dao.UserProfileDAO
import com.eskisehir.eventapp.data.local.converters.POIConverters
import com.eskisehir.eventapp.data.local.entity.CommentEntity
import com.eskisehir.eventapp.data.local.entity.UserEventEntity
import com.eskisehir.eventapp.data.local.entity.UserProfileEntity

@Database(
    entities = [POI::class, CommentEntity::class, UserEventEntity::class, UserProfileEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(POIConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun poiDAO(): POIDAO
    abstract fun commentDAO(): CommentDAO
    abstract fun userEventDAO(): UserEventDAO
    abstract fun userProfileDAO(): UserProfileDAO

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `comments` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `eventId` INTEGER NOT NULL, `userId` TEXT NOT NULL, `userDisplayName` TEXT NOT NULL, `userEmail` TEXT NOT NULL, `content` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_comments_eventId` ON `comments` (`eventId`)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `user_events` (`userId` TEXT NOT NULL, `eventId` INTEGER NOT NULL, `status` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`userId`, `eventId`))")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_user_events_userId` ON `user_events` (`userId`)")
                database.execSQL("CREATE INDEX IF NOT EXISTS `index_user_events_eventId` ON `user_events` (`eventId`)")
                database.execSQL("CREATE TABLE IF NOT EXISTS `user_profile` (`userId` TEXT NOT NULL, `interestAreas` TEXT NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`userId`))")
            }
        }

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "eskisehir-events.db"
            ).addMigrations(MIGRATION_1_2).build()
        }
    }
}
