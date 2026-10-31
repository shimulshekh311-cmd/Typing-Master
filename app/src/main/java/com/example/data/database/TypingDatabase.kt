package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.TypingResultDao
import com.example.data.model.TypingResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(entities = [TypingResult::class], version = 3, exportSchema = false)
abstract class TypingDatabase : RoomDatabase() {

    abstract fun typingResultDao(): TypingResultDao

    companion object {
        @Volatile
        private var INSTANCE: TypingDatabase? = null

        fun getDatabase(context: Context): TypingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TypingDatabase::class.java,
                    "typing_master_scores_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
