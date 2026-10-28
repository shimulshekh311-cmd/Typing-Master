package com.example.data.repository

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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

    private fun findActivity(context: Context): Activity? {
        var current = context
        while (current is ContextWrapper) {
            if (current is Activity) return current
            current = current.baseContext
        }
        return null
    }

    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser> {
        val auth = getAuth()
            ?: return Result.failure(IllegalStateException("Firebase Auth সার্ভিস প্রস্তুত করা সম্ভব হয়নি।"))

        return try {
            val targetContext = findActivity(context) ?: context
            val webClientId = FirebaseHelper.getWebClientId(targetContext)
            val credentialManager = CredentialManager.create(targetContext)

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
                context = targetContext
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
                credential is CustomCredential -> {
                    try {
                        GoogleIdTokenCredential.createFrom(credential.data).idToken
                    } catch (_: Throwable) {
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
            val errorText = "${e.type} ${e.message ?: ""}"
            val isCode10 = errorText.contains("DEVELOPER_ERROR", ignoreCase = true) ||
                           errorText.contains("status code 10", ignoreCase = true) ||
                           errorText.contains(": 10", ignoreCase = true) ||
                           errorText.contains("[10]", ignoreCase = true)
            val msg = when {
                isCode10 -> {
                    val actualSha1 = getInstalledAppCertificateSha1(context)
                    "গুগল সাইন-ইন কোড ১০ (SHA-1 কী অমিল):\nফোনটির APK-র SHA-1: $actualSha1\nঅফিশিয়াল কী: FD:24:36:11:6E:53:ED:05:2B:F2:0E:80:C3:7E:AF:F9:6A:F9:A7:B3"
                }
                errorText.contains("16") || errorText.contains("Cannot find a matching", ignoreCase = true) ->
                    "ডিভাইসে কোনো সাইন-ইন যোগ্য গুগল একাউন্ট পাওয়া যায়নি। অনুগ্রহ করে ফোনে গুগল একাউন্ট যোগ করুন।"
                errorText.contains("network", ignoreCase = true) ->
                    "ইন্টারনেট সংযোগ সমস্যা। অনুগ্রহ করে আপনার নেটওয়ার্ক চেক করুন।"
                else -> "গুগল সাইন-ইন ত্রুটি: ${e.localizedMessage ?: e.message ?: e.type}"
            }
            Result.failure(Exception(msg))
        } catch (e: Throwable) {
            Log.e(TAG, "Unexpected error during Google Sign In: ${e.message}", e)
            Result.failure(Exception(e.localizedMessage ?: e.message ?: "লগইন ব্যর্থ হয়েছে"))
        }
    }

    fun getInstalledAppCertificateSha1(context: Context): String {
        return try {
            val pm = context.packageManager
            val signatures = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                val packageInfo = pm.getPackageInfo(context.packageName, android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES)
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                val packageInfo = pm.getPackageInfo(context.packageName, android.content.pm.PackageManager.GET_SIGNATURES)
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }
            val cert = signatures?.firstOrNull() ?: return "N/A"
            val md = java.security.MessageDigest.getInstance("SHA-1")
            val digest = md.digest(cert.toByteArray())
            digest.joinToString(":") { "%02X".format(it) }
        } catch (e: Throwable) {
            "Error: ${e.message}"
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
