package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.LeaderboardEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class LeaderboardRepository(
    private val context: Context
) {
    companion object {
        private const val TAG = "LeaderboardRepository"
        private const val COLLECTION_NAME = "leaderboard"
    }

    private fun getDb(): FirebaseFirestore? = FirebaseHelper.getFirestore(context)
    private fun getAuth(): FirebaseAuth? = FirebaseHelper.getAuth(context)

    fun observeLeaderboard(languageFilter: String = "ALL"): Flow<List<LeaderboardEntry>> = callbackFlow {
        val db = getDb()
        val auth = getAuth()
        if (db == null || auth == null || auth.currentUser == null) {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }

        var listener: ListenerRegistration? = null
        try {
            val query = db.collection(COLLECTION_NAME)
                .orderBy("highestWpm", Query.Direction.DESCENDING)
                .limit(100)

            listener = query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Error observing leaderboard: ${error.message}", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val entries = snapshot.documents.mapNotNull { doc ->
                        try {
                            val userId = doc.getString("userId") ?: doc.id
                            val displayName = doc.getString("displayName") ?: "ব্যবহারকারী"
                            val photoUrl = doc.getString("photoUrl") ?: ""
                            val highestWpm = doc.getLong("highestWpm") ?: 0L
                            val highestAccuracy = doc.getLong("highestAccuracy") ?: 0L
                            val language = doc.getString("language") ?: "english"
                            val updatedAt = doc.getTimestamp("updatedAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)

                            LeaderboardEntry(
                                id = doc.id,
                                userId = userId,
                                displayName = displayName,
                                photoUrl = photoUrl,
                                highestWpm = highestWpm,
                                highestAccuracy = highestAccuracy,
                                language = language,
                                updatedAt = updatedAt
                            )
                        } catch (e: Throwable) {
                            null
                        }
                    }

                    val filtered = when (languageFilter.uppercase()) {
                        "BANGLA" -> entries.filter { it.language.equals("bangla", ignoreCase = true) }
                        "ENGLISH" -> entries.filter { it.language.equals("english", ignoreCase = true) }
                        else -> entries
                    }
                    trySend(filtered)
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error attaching leaderboard listener: ${e.message}", e)
            trySend(emptyList())
        }

        awaitClose {
            try {
                listener?.remove()
            } catch (_: Throwable) {}
        }
    }

    suspend fun getUserRecord(userId: String): LeaderboardEntry? {
        val db = getDb() ?: return null
        val auth = getAuth() ?: return null
        if (auth.currentUser == null) return null

        return try {
            val doc = db.collection(COLLECTION_NAME).document(userId).get().await()
            if (doc.exists()) {
                val displayName = doc.getString("displayName") ?: "ব্যবহারকারী"
                val photoUrl = doc.getString("photoUrl") ?: ""
                val highestWpm = doc.getLong("highestWpm") ?: 0L
                val highestAccuracy = doc.getLong("highestAccuracy") ?: 0L
                val language = doc.getString("language") ?: "english"
                val updatedAt = doc.getTimestamp("updatedAt", DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)

                LeaderboardEntry(
                    id = doc.id,
                    userId = doc.getString("userId") ?: doc.id,
                    displayName = displayName,
                    photoUrl = photoUrl,
                    highestWpm = highestWpm,
                    highestAccuracy = highestAccuracy,
                    language = language,
                    updatedAt = updatedAt
                )
            } else {
                null
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error fetching user record: ${e.message}", e)
            null
        }
    }

    suspend fun recordPersonalBest(wpm: Int, accuracy: Float, language: String): Result<Boolean> {
        val db = getDb() ?: return Result.failure(IllegalStateException("Database not available"))
        val auth = getAuth() ?: return Result.failure(IllegalStateException("Auth not available"))
        val user = auth.currentUser ?: return Result.failure(IllegalStateException("User is not signed in"))
        val docRef = db.collection(COLLECTION_NAME).document(user.uid)

        return try {
            val existing = getUserRecord(user.uid)
            val currentBestWpm = existing?.highestWpm?.toInt() ?: 0

            val targetWpm = maxOf(currentBestWpm, wpm)
            val targetAccuracy = if (wpm >= currentBestWpm) accuracy.toInt() else (existing?.highestAccuracy?.toInt() ?: accuracy.toInt())

            val normalizedLang = if (language.contains("BANGLA", ignoreCase = true)) "bangla" else "english"
            val cleanName = (user.displayName ?: "ব্যবহারকারী")
                .trim()
                .ifEmpty { "ব্যবহারকারী" }
                .take(80)

            val photo = user.photoUrl?.toString()?.take(1000)

            val payload = mutableMapOf<String, Any>(
                "userId" to user.uid,
                "displayName" to cleanName,
                "highestWpm" to targetWpm.toLong().coerceIn(0L, 500L),
                "highestAccuracy" to targetAccuracy.toLong().coerceIn(0L, 100L),
                "language" to normalizedLang,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (!photo.isNullOrBlank()) {
                payload["photoUrl"] = photo
            }

            docRef.set(payload, SetOptions.merge()).await()
            Result.success(true)
        } catch (e: Throwable) {
            Log.e(TAG, "Failed to record personal best to leaderboard", e)
            Result.failure(Exception(e.message ?: "Record failed"))
        }
    }
}
