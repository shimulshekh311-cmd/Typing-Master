package com.example.data.repository

import com.example.data.dao.TypingResultDao
import com.example.data.model.MonthlyDayStat
import com.example.data.model.MonthlyReportSummary
import com.example.data.model.TypingResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class TypingRepository(private val dao: TypingResultDao) {

    val allResults: Flow<List<TypingResult>> = dao.getAllResults()

    fun getResultsByLanguage(language: String): Flow<List<TypingResult>> {
        return dao.getResultsByLanguage(language)
    }

    suspend fun saveResult(result: TypingResult): Long {
        return dao.insertResult(result)
    }

    suspend fun deleteResult(id: Long) {
        dao.deleteResult(id)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }

    fun getMonthlyReport(year: Int, month: Int): Flow<MonthlyReportSummary> {
        // month: 1..12
        val startCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startTime = startCal.timeInMillis

        val endCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val endTime = endCal.timeInMillis

        val daysInMonth = startCal.getActualMaximum(Calendar.DAY_OF_MONTH)

        return dao.getResultsBetween(startTime, endTime).map { results ->
            if (results.isEmpty()) {
                MonthlyReportSummary(
                    year = year,
                    month = month,
                    totalSessions = 0,
                    averageWpm = 0f,
                    highestWpm = 0,
                    averageAccuracy = 0f,
                    totalTimeSeconds = 0,
                    englishSessionsCount = 0,
                    banglaSessionsCount = 0,
                    dailyStats = emptyList()
                )
            } else {
                val totalSessions = results.size
                val avgWpm = results.map { it.wpm }.average().toFloat()
                val peakWpm = results.maxOfOrNull { it.wpm } ?: 0
                val avgAccuracy = results.map { it.accuracy }.average().toFloat()
                val totalSeconds = results.sumOf { it.durationSeconds }
                val enCount = results.count { it.language == "ENGLISH" }
                val bnCount = results.count { it.language == "BANGLA" }

                // Group by Day of Month
                val dayMap = mutableMapOf<Int, MutableList<TypingResult>>()
                for (day in 1..daysInMonth) {
                    dayMap[day] = mutableListOf()
                }

                val itemCal = Calendar.getInstance()
                for (res in results) {
                    itemCal.timeInMillis = res.timestamp
                    val d = itemCal.get(Calendar.DAY_OF_MONTH)
                    dayMap[d]?.add(res)
                }

                val dailyStatsList = (1..daysInMonth).map { dayNum ->
                    val daySessions = dayMap[dayNum] ?: emptyList()
                    val dayAvgWpm = if (daySessions.isNotEmpty()) daySessions.map { it.wpm }.average().toFloat() else 0f
                    val dayPeakWpm = if (daySessions.isNotEmpty()) daySessions.maxOf { it.wpm } else 0
                    val dayAvgAcc = if (daySessions.isNotEmpty()) daySessions.map { it.accuracy }.average().toFloat() else 0f
                    
                    itemCal.set(Calendar.YEAR, year)
                    itemCal.set(Calendar.MONTH, month - 1)
                    itemCal.set(Calendar.DAY_OF_MONTH, dayNum)

                    MonthlyDayStat(
                        dayOfMonth = dayNum,
                        dateEpochMs = itemCal.timeInMillis,
                        avgWpm = dayAvgWpm,
                        peakWpm = dayPeakWpm,
                        avgAccuracy = dayAvgAcc,
                        sessionCount = daySessions.size
                    )
                }

                MonthlyReportSummary(
                    year = year,
                    month = month,
                    totalSessions = totalSessions,
                    averageWpm = avgWpm,
                    highestWpm = peakWpm,
                    averageAccuracy = avgAccuracy,
                    totalTimeSeconds = totalSeconds,
                    englishSessionsCount = enCount,
                    banglaSessionsCount = bnCount,
                    dailyStats = dailyStatsList
                )
            }
        }
    }
}
