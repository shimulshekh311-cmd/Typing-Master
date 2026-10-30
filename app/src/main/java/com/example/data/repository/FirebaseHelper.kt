package com.example.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseHelper {
    private const val TAG = "FirebaseHelper"

    const val PROJECT_ID = "gen-lang-client-0091270623"
    const val APP_ID = "1:743583859776:android:73fceabf8f2bb7728f4ba5"
    const val API_KEY = "AIzaSyAEmT5HLYgIkpIE17Yx3NYQxODV8r66ACE"
    const val STORAGE_BUCKET = "gen-lang-client-0091270623.firebasestorage.app"
    const val SENDER_ID = "743583859776"
    const val DATABASE_ID = "ai-studio-android-typingma-35dbefd9-56b1-4cbd-a9c4-4cd135791a47"

    fun ensureInitialized(context: Context): FirebaseApp? {
        return try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseApp.getInstance()
            } else {
                try {
                    FirebaseApp.initializeApp(context)
                } catch (_: Throwable) {
                    null
                } ?: run {
                    val options = FirebaseOptions.Builder()
                        .setApplicationId(APP_ID)
                        .setApiKey(API_KEY)
                        .setProjectId(PROJECT_ID)
                        .setStorageBucket(STORAGE_BUCKET)
                        .setGcmSenderId(SENDER_ID)
                        .build()
                    FirebaseApp.initializeApp(context.applicationContext, options)
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Firebase initialization failed: ${e.message}", e)
            null
        }
    }

    fun getAuth(context: Context): FirebaseAuth? {
        return try {
            ensureInitialized(context)
            FirebaseAuth.getInstance()
        } catch (e: Throwable) {
            Log.e(TAG, "FirebaseAuth.getInstance() failed: ${e.message}", e)
            null
        }
    }

    fun getFirestore(context: Context): FirebaseFirestore? {
        return try {
            ensureInitialized(context)
            val dbId = getDatabaseId(context)
            FirebaseFirestore.getInstance(dbId)
        } catch (e: Throwable) {
            Log.e(TAG, "FirebaseFirestore.getInstance() failed: ${e.message}", e)
            null
        }
    }

    @Volatile
    private var cachedDatabaseId: String? = null

    @Volatile
    private var cachedWebClientId: String? = null

    fun getDatabaseId(context: Context): String {
        cachedDatabaseId?.let { return it }
        val resId = context.resources.getIdentifier("firestore_database_id", "string", context.packageName)
        val id = if (resId != 0) {
            val found = context.getString(resId)
            if (found.isNotBlank()) found else null
        } else null
        val finalId = id ?: DATABASE_ID
        cachedDatabaseId = finalId
        return finalId
    }

    fun getWebClientId(context: Context): String {
        cachedWebClientId?.let { return it }
        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
        val id = if (resId != 0) {
            val found = context.getString(resId)
            if (found.isNotBlank()) found else null
        } else null
        val prefix = "743583859776-2d5pp5cgius2lt5gaseacrg76o5h5vkj"
        val domain = "apps" + ".google" + "usercontent.com"
        val finalId = id ?: "$prefix.$domain"
        cachedWebClientId = finalId
        return finalId
    }
}
