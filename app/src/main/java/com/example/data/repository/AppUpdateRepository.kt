package com.example.data.repository

import android.content.Context
import android.os.Build
import android.util.Log
import com.example.data.model.AppUpdateInfo
import kotlinx.coroutines.tasks.await

class AppUpdateRepository(private val context: Context) {

    private val firestore get() = FirebaseHelper.getFirestore(context)

    fun getCurrentVersion(): Pair<Long, String> {
        return try {
            val pInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    android.content.pm.PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode.toLong()
            }
            val versionName = pInfo.versionName ?: "1.0"
            versionCode to versionName
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching package info: ${e.message}")
            1L to "1.0"
        }
    }

    suspend fun fetchUpdateInfo(): AppUpdateInfo {
        val (currentCode, currentName) = getCurrentVersion()
        val db = firestore ?: return AppUpdateInfo(
            currentVersionCode = currentCode,
            currentVersionName = currentName,
            isUpdateAvailable = false
        )

        return try {
            val snapshot = db.collection("app_config")
                .document("update")
                .get()
                .await()

            if (snapshot.exists()) {
                val latestCode = snapshot.getLong("latestVersionCode") ?: currentCode
                val latestName = snapshot.getString("latestVersionName") ?: "$latestCode.0"
                val updateUrl = snapshot.getString("updateUrl") ?: "https://github.com/brandifycreative"
                val releaseNotes = snapshot.getString("releaseNotes")
                    ?: "নতুন আপডেট উপলব্ধ! সেরা অভিজ্ঞতা পেতে এখনই অ্যাপটি আপডেট করে নিন।"
                val isForceUpdate = snapshot.getBoolean("isForceUpdate") ?: false

                val isNewer = latestCode > currentCode

                AppUpdateInfo(
                    currentVersionCode = currentCode,
                    currentVersionName = currentName,
                    latestVersionCode = latestCode,
                    latestVersionName = latestName,
                    updateUrl = updateUrl,
                    releaseNotes = releaseNotes,
                    isForceUpdate = isForceUpdate,
                    isUpdateAvailable = isNewer
                )
            } else {
                AppUpdateInfo(
                    currentVersionCode = currentCode,
                    currentVersionName = currentName,
                    isUpdateAvailable = false
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Update check failed: ${e.message}")
            AppUpdateInfo(
                currentVersionCode = currentCode,
                currentVersionName = currentName,
                isUpdateAvailable = false
            )
        }
    }

    suspend fun setRemoteVersionConfig(
        latestCode: Long,
        latestName: String,
        updateUrl: String,
        releaseNotes: String,
        isForceUpdate: Boolean
    ): Boolean {
        val db = firestore ?: return false
        return try {
            val data = mapOf(
                "latestVersionCode" to latestCode,
                "latestVersionName" to latestName,
                "updateUrl" to updateUrl,
                "releaseNotes" to releaseNotes,
                "isForceUpdate" to isForceUpdate,
                "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )
            db.collection("app_config")
                .document("update")
                .set(data, com.google.firebase.firestore.SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set remote version config: ${e.message}")
            false
        }
    }

    companion object {
        private const val TAG = "AppUpdateRepo"
    }
}
