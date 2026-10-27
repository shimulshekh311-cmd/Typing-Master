package com.example.data.repository

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.R
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
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    companion object {
        private const val TAG = "AuthRepository"
    }

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun getAuthStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    fun getUserProfile(): UserProfile? {
        val user = auth.currentUser ?: return null
        return UserProfile(
            uid = user.uid,
            displayName = user.displayName?.ifBlank { "ব্যবহারকারী" } ?: "ব্যবহারকারী",
            email = user.email ?: "",
            photoUrl = user.photoUrl?.toString() ?: ""
        )
    }

    private fun resolveWebClientId(context: Context): String? {
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        if (resId != 0) {
            val id = context.getString(resId)
            if (id.isNotBlank()) return id
        }
        return null
    }

    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser> {
        return try {
            val webClientId = resolveWebClientId(context)
            if (webClientId.isNullOrBlank()) {
                Log.e(TAG, "Google Web Client ID resource not found. Ensure google-services.json is present in app/ module.")
                return Result.failure(
                    IllegalStateException("গুগল কনফিগারেশন (Web Client ID) পাওয়া যায়নি। app/ ফোল্ডারে google-services.json ফাইলটি নিশ্চিত করুন।")
                )
            }

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
            Log.e(TAG, "CredentialManager error during sign in: ${e.message}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during Google Sign In: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }
}
