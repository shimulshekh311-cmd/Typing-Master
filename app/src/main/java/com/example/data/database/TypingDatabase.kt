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

@Database(entities = [TypingResult::class], version = 1, exportSchema = false)
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
                    "typing_master_db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedInitialData(database.typingResultDao())
                    }
                }
            }
        }

        private suspend fun seedInitialData(dao: TypingResultDao) {
            val sampleResults = mutableListOf<TypingResult>()
            val cal = Calendar.getInstance()
            val nowMs = cal.timeInMillis

            // Generate realistic typing progress records for the past 24 days
            val testProfiles = listOf(
                Triple("Home Row Mastery", "ENGLISH", "LESSON"),
                Triple("60s Speed Sprint", "ENGLISH", "SPEED_TEST"),
                Triple("বাংলা স্বরবর্ণ অনুশীলন", "BANGLA", "LESSON"),
                Triple("রবীন্দ্রনাথের সাহিত্য পাঠ", "BANGLA", "SPEED_TEST"),
                Triple("English Quotes Sprint", "ENGLISH", "SPEED_TEST"),
                Triple("বাংলা বাক্য ও যুক্তবর্ণ", "BANGLA", "LESSON"),
                Triple("Top Row QWERTY Drill", "ENGLISH", "LESSON"),
                Triple("2-Min Endurance Test", "ENGLISH", "SPEED_TEST"),
                Triple("নজরুলের বিদ্রোহ বাণী", "BANGLA", "SPEED_TEST")
            )

            // Progressive improvement in WPM and Accuracy
            for (i in 22 downTo 0) {
                val dayCal = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -i)
                }
                val testsToday = if (i % 2 == 0) 2 else 1
                for (t in 0 until testsToday) {
                    val profile = testProfiles[(i + t) % testProfiles.size]
                    val isEnglish = profile.second == "ENGLISH"
                    
                    // Gradual improvement: 26 WPM rising to ~48 WPM, Accuracy 86% -> 98%
                    val baseWpm = 25 + (22 - i) * 1.05f + (t * 2)
                    val wpm = (baseWpm + ((-2..3).random())).toInt().coerceIn(18, 65)
                    val accuracy = if (isEnglish) {
                        (88.0f + (22 - i) * 0.45f + (t * 0.5f)).coerceIn(85.0f, 99.5f)
                    } else {
                        (82.0f + (22 - i) * 0.5f).coerceIn(80.0f, 98.0f)
                    }
                    val duration = if (profile.third == "SPEED_TEST") 60 else 45
                    val totalChars = (wpm * 5 * (duration / 60.0f)).toInt().coerceAtLeast(30)
                    val mistakeCount = ((totalChars * (100f - accuracy)) / 100f).toInt().coerceAtLeast(0)
                    val correctChars = totalChars - mistakeCount
                    val cpm = ((totalChars / duration.toFloat()) * 60f).toInt()

                    dayCal.set(Calendar.HOUR_OF_DAY, 9 + t * 4)
                    dayCal.set(Calendar.MINUTE, 15 + t * 10)

                    sampleResults.add(
                        TypingResult(
                            timestamp = dayCal.timeInMillis,
                            language = profile.second,
                            mode = profile.third,
                            title = profile.first,
                            wpm = wpm,
                            cpm = cpm,
                            accuracy = String.format(java.util.Locale.US, "%.1f", accuracy).toFloat(),
                            durationSeconds = duration,
                            totalCharsTyped = totalChars,
                            correctChars = correctChars,
                            mistakeCount = mistakeCount
                        )
                    )
                }
            }

            dao.insertAll(sampleResults)
        }
    }
}
