package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.database.TypingDatabase
import com.example.data.model.TypingResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserDataSyncRepository(private val context: Context) {

    companion object {
        private const val TAG = "UserDataSyncRepository"
        private const val COLLECTION_USERS = "users"
        private const val SUBCOLLECTION_RESULTS = "results"
    }

    private fun getDb(): FirebaseFirestore? = FirebaseHelper.getFirestore(context)
    private fun getAuth(): FirebaseAuth? = FirebaseHelper.getAuth(context)

    suspend fun uploadResult(userId: String, result: TypingResult) = withContext(Dispatchers.IO) {
        val db = getDb() ?: return@withContext
        try {
            val docId = result.timestamp.toString()
            val data = hashMapOf<String, Any>(
                "timestamp" to result.timestamp,
                "language" to result.language,
                "mode" to result.mode,
                "title" to result.title,
                "wpm" to result.wpm,
                "cpm" to result.cpm,
                "accuracy" to result.accuracy.toDouble(),
                "durationSeconds" to result.durationSeconds,
                "totalCharsTyped" to result.totalCharsTyped,
                "correctChars" to result.correctChars,
                "mistakeCount" to result.mistakeCount
            )
            db.collection(COLLECTION_USERS)
                .document(userId)
                .collection(SUBCOLLECTION_RESULTS)
                .document(docId)
                .set(data, SetOptions.merge())
                .await()
            Log.d(TAG, "Uploaded test result $docId to Firestore for user $userId")
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to upload result to Firestore: ${e.message}", e)
        }
    }

    suspend fun syncCloudToLocal(userId: String) = withContext(Dispatchers.IO) {
        val db = getDb() ?: return@withContext
        val dao = TypingDatabase.getDatabase(context).typingResultDao()
        try {
            val snapshot = db.collection(COLLECTION_USERS)
                .document(userId)
                .collection(SUBCOLLECTION_RESULTS)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val existingTimestamps = dao.getAllTimestamps().toSet()
                val cloudResults = snapshot.documents.mapNotNull { doc ->
                    try {
                        val timestamp = doc.getLong("timestamp") ?: doc.id.toLongOrNull() ?: return@mapNotNull null
                        val language = doc.getString("language") ?: "ENGLISH"
                        val mode = doc.getString("mode") ?: "SPEED_TEST"
                        val title = doc.getString("title") ?: "Typing Test"
                        val wpm = doc.getLong("wpm")?.toInt() ?: 0
                        val cpm = doc.getLong("cpm")?.toInt() ?: 0
                        val accuracy = doc.getDouble("accuracy")?.toFloat()
                            ?: (doc.getLong("accuracy")?.toFloat() ?: 0f)
                        val durationSeconds = doc.getLong("durationSeconds")?.toInt() ?: 60
                        val totalCharsTyped = doc.getLong("totalCharsTyped")?.toInt() ?: 0
                        val correctChars = doc.getLong("correctChars")?.toInt() ?: 0
                        val mistakeCount = doc.getLong("mistakeCount")?.toInt() ?: 0

                        TypingResult(
                            id = 0,
                            timestamp = timestamp,
                            language = language,
                            mode = mode,
                            title = title,
                            wpm = wpm,
                            cpm = cpm,
                            accuracy = accuracy,
                            durationSeconds = durationSeconds,
                            totalCharsTyped = totalCharsTyped,
                            correctChars = correctChars,
                            mistakeCount = mistakeCount
                        )
                    } catch (_: Throwable) {
                        null
                    }
                }.filter { !existingTimestamps.contains(it.timestamp) }

                if (cloudResults.isNotEmpty()) {
                    dao.insertAll(cloudResults)
                    Log.d(TAG, "Successfully restored ${cloudResults.size} records from Firestore for user $userId")
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error restoring cloud results: ${e.message}", e)
        }
    }
}
