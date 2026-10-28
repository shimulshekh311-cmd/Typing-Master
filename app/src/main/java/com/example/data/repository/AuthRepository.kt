package com.example.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.model.UserProfile
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val context: Context
) {
    companion object {
        private const val TAG = "AuthRepository"
    }

    private fun getAuth(): FirebaseAuth? = FirebaseHelper.getAuth(context)

    val currentUser: FirebaseUser?
        get() = try { getAuth()?.currentUser } catch (_: Throwable) { null }

    fun getAuthStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val auth = getAuth()
        if (auth == null) {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        try {
            auth.addAuthStateListener(listener)
        } catch (_: Throwable) {
            trySend(null)
        }
        awaitClose {
            try {
                auth.removeAuthStateListener(listener)
            } catch (_: Throwable) {}
        }
    }

    fun getUserProfile(): UserProfile? {
        val user = currentUser ?: return null
        return UserProfile(
            uid = user.uid,
            displayName = user.displayName?.ifBlank { "ব্যবহারকারী" } ?: "ব্যবহারকারী",
            email = user.email ?: "",
            photoUrl = user.photoUrl?.toString() ?: ""
        )
    }

    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser> {
        val auth = getAuth()
            ?: return Result.failure(IllegalStateException("Firebase Auth সার্ভিস প্রস্তুত করা সম্ভব হয়নি।"))

        return try {
            val webClientId = FirebaseHelper.getWebClientId(context)
            val credentialManager = CredentialManager.create(context)

            // GetGoogleIdOption with filterByAuthorizedAccounts = false ensures that accounts
            // can be selected and directed to the consent screen ("Continue as...").
            // Important: GetSignInWithGoogleOption cannot be combined with other options,
            // so we use GetGoogleIdOption alone.
            val getGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(getGoogleIdOption)
                .build()

            val response = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = response.credential
            val idToken = when {
                credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        googleIdTokenCredential.idToken
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse GoogleIdTokenCredential via createFrom", e)
                        credential.data.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID_TOKEN")
                            ?: credential.data.getString("id_token")
                    }
                }
                else -> {
                    Log.w(TAG, "Received unexpected credential type: ${credential.type}")
                    null
                }
            }

            if (idToken.isNullOrBlank()) {
                return Result.failure(Exception("গুগল আইডি টোকেন পাওয়া যায়নি। দয়া করে আবার চেষ্টা করুন।"))
            }

            val authCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(authCredential).await()
            val user = authResult.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Firebase ব্যবহারকারী তথ্য পাওয়া যায়নি"))
            }
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "Google Sign-In cancelled: ${e.message}")
            Result.failure(Exception("গুগল সাইন-ইন বাতিল করা হয়েছে"))
        } catch (e: GetCredentialException) {
            Log.e(TAG, "CredentialManager error: ${e.type} - ${e.message}", e)
            val msg = when {
                e.message?.contains("10", ignoreCase = true) == true || e.message?.contains("Developer error", ignoreCase = true) == true ->
                    "Google Play Services ত্রুটি (Code 10: সাইনিং কি/SHA-1 মিসম্যাচ)। অনুগ্রহ করে নিশ্চিত করুন সঠিক APK ব্যবহার করছেন।"
                e.message?.contains("16", ignoreCase = true) == true || e.message?.contains("Cannot find a matching", ignoreCase = true) == true ->
                    "ডিভাইসে কোনো সাইন-ইন যোগ্য গুগল একাউন্ট পাওয়া যায়নি।"
                else -> "গুগল সাইন-ইন ত্রুটি: ${e.localizedMessage ?: e.message ?: "ব্যর্থ হয়েছে"}"
            }
            Result.failure(Exception(msg))
        } catch (e: Throwable) {
            Log.e(TAG, "Unexpected error during Google Sign In: ${e.message}", e)
            Result.failure(Exception(e.localizedMessage ?: e.message ?: "লগইন ব্যর্থ হয়েছে"))
        }
    }

    fun signOut() {
        try {
            getAuth()?.signOut()
        } catch (e: Throwable) {
            Log.e(TAG, "Sign out error", e)
        }
    }
}
