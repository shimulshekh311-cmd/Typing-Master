package com.example.data.repository

import android.content.Context
import android.os.Build
import android.util.Log
import com.example.data.model.AppUpdateInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

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
        val defaultRepo = "shimulshekh311-cmd/Typing-Master"

        // 1. First, check if Firestore overrides GitHub repo name or sets custom config
        var firestoreRepo: String? = null
        var firestoreInfo: AppUpdateInfo? = null

        try {
            val db = firestore
            if (db != null) {
                val snapshot = db.collection("app_config")
                    .document("update")
                    .get()
                    .await()

                if (snapshot.exists()) {
                    firestoreRepo = snapshot.getString("githubRepo")
                    val latestCode = snapshot.getLong("latestVersionCode") ?: currentCode
                    val latestName = snapshot.getString("latestVersionName") ?: "$latestCode.0"
                    val updateUrl = snapshot.getString("updateUrl") ?: "https://github.com/$defaultRepo/releases"
                    val releaseNotes = snapshot.getString("releaseNotes")
                        ?: "নতুন আপডেট উপলব্ধ! সেরা অভিজ্ঞতা পেতে এখনই অ্যাপটি আপডেট করে নিন।"
                    val isForceUpdate = snapshot.getBoolean("isForceUpdate") ?: false

                    if (latestCode > currentCode) {
                        firestoreInfo = AppUpdateInfo(
                            currentVersionCode = currentCode,
                            currentVersionName = currentName,
                            latestVersionCode = latestCode,
                            latestVersionName = latestName,
                            updateUrl = updateUrl,
                            releaseNotes = releaseNotes,
                            isForceUpdate = isForceUpdate,
                            isUpdateAvailable = true
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firestore check failed (continuing to GitHub): ${e.message}")
        }

        // 2. Fetch latest release directly from GitHub Releases API
        val targetRepo = firestoreRepo?.takeIf { it.isNotBlank() } ?: defaultRepo
        val githubRelease = fetchGitHubLatestRelease(targetRepo)

        if (githubRelease != null) {
            val tagName = githubRelease.optString("tag_name", "")
            val releaseTitle = githubRelease.optString("name", "")
            val htmlUrl = githubRelease.optString("html_url", "https://github.com/$targetRepo/releases")
            val rawBody = githubRelease.optString("body", "")

            // Extract direct APK asset download URL
            var apkDownloadUrl: String? = null
            val assetsArray = githubRelease.optJSONArray("assets")
            if (assetsArray != null) {
                for (i in 0 until assetsArray.length()) {
                    val asset = assetsArray.optJSONObject(i) ?: continue
                    val assetName = asset.optString("name", "")
                    if (assetName.endsWith(".apk", ignoreCase = true)) {
                        apkDownloadUrl = asset.optString("browser_download_url")
                        break
                    }
                }
            }
            val finalUpdateUrl = apkDownloadUrl
                ?: "https://github.com/$targetRepo/releases/latest/download/app-debug.apk"

            // Parse build number or semantic version
            val githubBuildNum = extractBuildNumber(tagName, releaseTitle)
            val githubSemVer = parseSemVer(tagName) ?: parseSemVer(releaseTitle)
            val currentSemVer = parseSemVer(currentName)

            val isNewer = when {
                githubBuildNum != null -> githubBuildNum > currentCode
                githubSemVer != null && currentSemVer != null -> isSemVerGreater(githubSemVer, currentSemVer)
                else -> false
            }

            val latestDisplayName = when {
                githubBuildNum != null -> "Build #$githubBuildNum"
                releaseTitle.isNotBlank() -> releaseTitle
                tagName.isNotBlank() -> tagName.removePrefix("v").removePrefix("debug-apk-")
                else -> "1.1"
            }

            val cleanReleaseNotes = formatReleaseNotes(rawBody, latestDisplayName)

            if (isNewer) {
                return AppUpdateInfo(
                    currentVersionCode = currentCode,
                    currentVersionName = currentName,
                    latestVersionCode = githubBuildNum ?: (currentCode + 1),
                    latestVersionName = latestDisplayName,
                    updateUrl = finalUpdateUrl,
                    releaseNotes = cleanReleaseNotes,
                    isForceUpdate = false,
                    isUpdateAvailable = true
                )
            }
        }

        // 3. Fallback to Firestore update if GitHub didn't detect an update or was unreachable
        if (firestoreInfo != null && firestoreInfo.isUpdateAvailable) {
            return firestoreInfo
        }

        // 4. No update available - app is already up to date
        return AppUpdateInfo(
            currentVersionCode = currentCode,
            currentVersionName = currentName,
            latestVersionCode = currentCode,
            latestVersionName = currentName,
            isUpdateAvailable = false
        )
    }

    private suspend fun fetchGitHubLatestRelease(repo: String): JSONObject? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            // First attempt: fetch recent releases array to include pre-releases & latest builds
            val releasesUrl = URL("https://api.github.com/repos/$repo/releases?per_page=5")
            connection = (releasesUrl.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                setRequestProperty("User-Agent", "TypingMaster-AndroidApp")
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val releasesArray = JSONArray(responseText)
                if (releasesArray.length() > 0) {
                    return@withContext releasesArray.getJSONObject(0)
                }
            } else {
                Log.w(TAG, "GitHub /releases HTTP response: ${connection.responseCode}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "GitHub /releases fetch error: ${e.message}")
        } finally {
            connection?.disconnect()
        }

        // Second attempt: fetch /releases/latest endpoint
        try {
            val latestUrl = URL("https://api.github.com/repos/$repo/releases/latest")
            connection = (latestUrl.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("Accept", "application/vnd.github.v3+json")
                setRequestProperty("User-Agent", "TypingMaster-AndroidApp")
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                return@withContext JSONObject(responseText)
            }
        } catch (e: Exception) {
            Log.w(TAG, "GitHub /releases/latest fetch error: ${e.message}")
        } finally {
            connection?.disconnect()
        }

        null
    }

    private fun extractBuildNumber(tag: String, name: String): Long? {
        val patterns = listOf(
            Regex("""debug-apk-build-(\d+)""", RegexOption.IGNORE_CASE),
            Regex("""(?:build|run)\s*#?(\d+)""", RegexOption.IGNORE_CASE),
            Regex("""#(\d+)"""),
            Regex("""[bB]uild[-_](\d+)"""),
            Regex("""-(\d+)(?:-\d+)?$""")
        )
        for (pattern in patterns) {
            pattern.find(tag)?.groupValues?.get(1)?.toLongOrNull()?.let { return it }
            pattern.find(name)?.groupValues?.get(1)?.toLongOrNull()?.let { return it }
        }
        return null
    }

    private fun parseSemVer(versionStr: String): Triple<Int, Int, Int>? {
        val regex = Regex("""(?:v|ver|version)?\s*(\d+)\.(\d+)(?:\.(\d+))?""", RegexOption.IGNORE_CASE)
        val match = regex.find(versionStr) ?: return null
        val major = match.groupValues[1].toIntOrNull() ?: 0
        val minor = match.groupValues[2].toIntOrNull() ?: 0
        val patch = match.groupValues.getOrNull(3)?.toIntOrNull() ?: 0
        return Triple(major, minor, patch)
    }

    private fun isSemVerGreater(v1: Triple<Int, Int, Int>, v2: Triple<Int, Int, Int>): Boolean {
        if (v1.first != v2.first) return v1.first > v2.first
        if (v1.second != v2.second) return v1.second > v2.second
        return v1.third > v2.third
    }

    private fun formatReleaseNotes(rawBody: String, latestName: String): String {
        return if (rawBody.isNotBlank() && !rawBody.contains("Automatically generated debug APK", ignoreCase = true)) {
            rawBody.trim()
        } else {
            "• নতুন সংস্করণ $latestName প্রকাশ করা হয়েছে\n• দ্রুত ও নির্ভুল টাইপিং রেসপন্স\n• লেটেস্ট আপডেট ও বাগ ফিক্স পেতে এখনই 'Update Now' চাপুন"
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
