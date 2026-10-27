package com.example.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.model.UserProfile
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
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
            val googleIdOption = GetSignInWithGoogleOption.Builder(webClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = response.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val user = authResult.user
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("Google Sign-In returned null user"))
                }
            } else {
                Result.failure(Exception("Invalid credential type received"))
            }
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "Google Sign-In cancelled by user")
            Result.failure(e)
        } catch (e: GetCredentialException) {
            Log.e(TAG, "CredentialManager error: ${e.message}", e)
            Result.failure(e)
        } catch (e: Throwable) {
            Log.e(TAG, "Unexpected error during Google Sign In: ${e.message}", e)
            Result.failure(Exception(e.message ?: "Sign-in failed"))
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
