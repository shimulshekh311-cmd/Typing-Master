package com.example.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

sealed interface UpdateDownloadState {
    object Idle : UpdateDownloadState
    data class Downloading(
        val progressPercent: Int,
        val downloadedBytes: Long,
        val totalBytes: Long,
        val downloadedMb: Float,
        val totalMb: Float
    ) : UpdateDownloadState
    data class ReadyToInstall(val apkFile: File) : UpdateDownloadState
    data class Error(val message: String) : UpdateDownloadState
}

class InAppUpdateDownloader(private val context: Context) {

    companion object {
        private const val TAG = "InAppUpdateDownloader"
        private const val APK_FILE_NAME = "TypingMaster_Update.apk"
    }

    fun getDestinationApkFile(): File {
        val baseDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            ?: File(context.cacheDir, "updates").apply { mkdirs() }
        return File(baseDir, APK_FILE_NAME)
    }

    suspend fun downloadApk(
        downloadUrl: String,
        onProgress: (UpdateDownloadState) -> Unit
    ): File? = withContext(Dispatchers.IO) {
        var currentUrl = downloadUrl.trim()
        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null

        val targetFile = getDestinationApkFile()
        if (targetFile.exists()) {
            try {
                targetFile.delete()
            } catch (_: Exception) {}
        }

        try {
            onProgress(UpdateDownloadState.Downloading(0, 0L, 0L, 0f, 0f))

            // GitHub release assets redirect (HTTP 302/307 to AWS S3/objects.githubusercontent.com)
            // We follow redirects manually up to 6 times to ensure successful download connection
            var redirectCount = 0
            while (redirectCount < 6) {
                val urlObj = URL(currentUrl)
                connection = (urlObj.openConnection() as HttpURLConnection).apply {
                    connectTimeout = 20000
                    readTimeout = 40000
                    instanceFollowRedirects = true
                    setRequestProperty("User-Agent", "TypingMaster-AndroidApp")
                    setRequestProperty("Accept", "*/*")
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                    responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                    responseCode == HttpURLConnection.HTTP_SEE_OTHER ||
                    responseCode == 307 || responseCode == 308
                ) {
                    val newLocation = connection.getHeaderField("Location")
                    connection.disconnect()
                    if (!newLocation.isNullOrBlank()) {
                        currentUrl = newLocation
                        redirectCount++
                        continue
                    }
                }
                break
            }

            val finalConn = connection ?: throw IllegalStateException("Could not establish connection")
            val responseCode = finalConn.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                val err = "ডাউনলোড সার্ভার রেসপন্স কোড: $responseCode"
                Log.e(TAG, err)
                onProgress(UpdateDownloadState.Error(err))
                return@withContext null
            }

            val totalBytes = finalConn.contentLength.toLong()
            val totalMb = if (totalBytes > 0) totalBytes / (1024f * 1024f) else 0f

            inputStream = finalConn.inputStream
            outputStream = FileOutputStream(targetFile)

            val buffer = ByteArray(8192)
            var bytesRead: Int
            var downloadedBytes = 0L
            var lastEmittedTime = 0L

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                downloadedBytes += bytesRead

                val now = System.currentTimeMillis()
                if (now - lastEmittedTime > 150) {
                    lastEmittedTime = now
                    val percent = if (totalBytes > 0) {
                        ((downloadedBytes * 100) / totalBytes).toInt().coerceIn(0, 99)
                    } else {
                        0
                    }
                    val downloadedMb = downloadedBytes / (1024f * 1024f)
                    onProgress(
                        UpdateDownloadState.Downloading(
                            progressPercent = percent,
                            downloadedBytes = downloadedBytes,
                            totalBytes = totalBytes,
                            downloadedMb = downloadedMb,
                            totalMb = totalMb
                        )
                    )
                }
            }

            outputStream.flush()

            if (targetFile.exists() && targetFile.length() > 500_000) {
                onProgress(UpdateDownloadState.ReadyToInstall(targetFile))
                return@withContext targetFile
            } else {
                val err = "ফাইল ডাউনলোড অসম্পূর্ণ হয়েছে"
                Log.e(TAG, err)
                onProgress(UpdateDownloadState.Error(err))
                return@withContext null
            }

        } catch (e: Exception) {
            Log.e(TAG, "Download error: ${e.message}", e)
            onProgress(UpdateDownloadState.Error("ডাউনলোড ব্যর্থ হয়েছে: ${e.localizedMessage ?: e.message}"))
            return@withContext null
        } finally {
            try {
                inputStream?.close()
            } catch (_: Exception) {}
            try {
                outputStream?.close()
            } catch (_: Exception) {}
            connection?.disconnect()
        }
    }

    fun installApk(apkFile: File): Boolean {
        if (!apkFile.exists()) {
            Toast.makeText(context, "ইনস্টলার ফাইল পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
            return false
        }

        try {
            // Android 8.0+ (Oreo) unknown app sources permission check
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    Toast.makeText(
                        context,
                        "অ্যাপ আপডেট ইনস্টল করতে 'Allow from this source' অনুমতি দিন",
                        Toast.LENGTH_LONG
                    ).show()
                    val permissionIntent = Intent(
                        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:${context.packageName}")
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(permissionIntent)
                    return false
                }
            }

            val apkUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(installIntent)
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Install error: ${e.message}", e)
            Toast.makeText(context, "ইনস্টলার খুলতে সমস্যা হয়েছে: ${e.message}", Toast.LENGTH_LONG).show()
            return false
        }
    }
}
