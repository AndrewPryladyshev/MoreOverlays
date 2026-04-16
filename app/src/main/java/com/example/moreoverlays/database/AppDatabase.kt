package com.example.moreoverlays.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ AppData::class, OverlayConfig::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun daoApps() : AppsDao
    abstract fun daoOverlayConfigs() : OverlayConfigsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .enableMultiInstanceInvalidation()
                    .build().also { INSTANCE = it }
//                    .fallbackToDestructiveMigration()
            }
        }
    }

}