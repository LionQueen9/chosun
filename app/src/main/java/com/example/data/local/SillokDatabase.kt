package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BookmarkEntity::class, SearchHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SillokDatabase : RoomDatabase() {
    abstract fun sillokDao(): SillokDao

    companion object {
        @Volatile
        private var INSTANCE: SillokDatabase? = null

        fun getInstance(context: Context): SillokDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SillokDatabase::class.java,
                    "joseon_sillok.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
