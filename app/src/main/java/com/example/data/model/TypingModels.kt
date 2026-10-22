package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TypingLanguage(val displayName: String, val nativeName: String) {
    ENGLISH("English", "ইংরেজি"),
    BANGLA("Bengali", "বাংলা")
}

enum class TypingMode(val titleEn: String, val titleBn: String) {
    LESSON("Lessons & Learning", "পাঠ ও শেখা"),
    SPEED_TEST("Speed Test", "গতি পরীক্ষা"),
    DRILL("Fast Fingers Drill", "দ্রুত আঙুল চ্যালেঞ্জ"),
    CUSTOM("Custom Practice", "নিজস্ব অনুচ্ছেদ")
}

enum class InputLayoutMode(val labelEn: String, val labelBn: String) {
    BIJOY("Bijoy Layout (PC / External)", "বিজয় লেআউট (এক্সটার্নাল কিবোর্ড)"),
    AVRO_SYSTEM("System / Avro Keyboard", "অভ্র / সিস্টেম কিবোর্ড")
}

enum class Difficulty(val labelEn: String, val labelBn: String) {
    BEGINNER("Beginner", "প্রাথমিক"),
    INTERMEDIATE("Intermediate", "মধ্যবর্তী"),
    ADVANCED("Advanced", "উন্নত")
}

data class Lesson(
    val id: String,
    val language: TypingLanguage,
    val category: String,
    val categoryBn: String,
    val title: String,
    val titleBn: String,
    val description: String,
    val descriptionBn: String,
    val targetText: String,
    val difficulty: Difficulty,
    val targetWpm: Int = 30
)

@Entity(tableName = "typing_results")
data class TypingResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val language: String, // "ENGLISH" or "BANGLA"
    val mode: String,     // "LESSON", "SPEED_TEST", etc.
    val title: String,
    val wpm: Int,
    val cpm: Int,
    val accuracy: Float,  // 0.0 - 100.0%
    val durationSeconds: Int,
    val totalCharsTyped: Int,
    val correctChars: Int,
    val mistakeCount: Int
)

data class MonthlyDayStat(
    val dayOfMonth: Int,
    val dateEpochMs: Long,
    val avgWpm: Float,
    val peakWpm: Int,
    val avgAccuracy: Float,
    val sessionCount: Int
)

data class MonthlyReportSummary(
    val year: Int,
    val month: Int, // 1-12
    val totalSessions: Int,
    val averageWpm: Float,
    val highestWpm: Int,
    val averageAccuracy: Float,
    val totalTimeSeconds: Int,
    val englishSessionsCount: Int,
    val banglaSessionsCount: Int,
    val dailyStats: List<MonthlyDayStat>
)
