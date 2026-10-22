package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.TypingResult
import kotlinx.coroutines.flow.Flow

@Dao
interface TypingResultDao {

    @Query("SELECT * FROM typing_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<TypingResult>>

    @Query("SELECT * FROM typing_results WHERE language = :language ORDER BY timestamp DESC")
    fun getResultsByLanguage(language: String): Flow<List<TypingResult>>

    @Query("SELECT * FROM typing_results WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    fun getResultsBetween(startTime: Long, endTime: Long): Flow<List<TypingResult>>

    @Query("SELECT MAX(wpm) FROM typing_results WHERE language = :language")
    suspend fun getPeakWpm(language: String): Int?

    @Query("SELECT MAX(accuracy) FROM typing_results WHERE language = :language")
    suspend fun getPeakAccuracy(language: String): Float?

    @Query("SELECT COUNT(*) FROM typing_results")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: TypingResult): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<TypingResult>)

    @Query("DELETE FROM typing_results WHERE id = :id")
    suspend fun deleteResult(id: Long)

    @Query("DELETE FROM typing_results")
    suspend fun clearAll()
}
