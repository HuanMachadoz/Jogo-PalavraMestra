package com.palavramestra.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.palavramestra.data.local.dao.RankingDao
import com.palavramestra.data.local.dao.WordDao
import com.palavramestra.data.local.entity.RankingEntity
import com.palavramestra.data.local.entity.WordEntity

@Database(
    entities = [WordEntity::class, RankingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun rankingDao(): RankingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "palavra_mestra_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
