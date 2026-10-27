package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class LeaderboardEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val highestWpm: Long = 0L,
    val highestAccuracy: Long = 0L,
    val language: String = "english", // "bangla", "english", "mixed"
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class UserProfile(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String
)
