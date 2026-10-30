package com.example.data.model

data class AppUpdateInfo(
    val currentVersionCode: Long = 1L,
    val currentVersionName: String = "1.0",
    val latestVersionCode: Long = 1L,
    val latestVersionName: String = "1.0",
    val updateUrl: String = "https://github.com/brandifycreative",
    val releaseNotes: String = "নতুন আপডেট উপলব্ধ! সেরা অভিজ্ঞতা পেতে এখনই অ্যাপটি আপডেট করে নিন।",
    val isForceUpdate: Boolean = false,
    val isUpdateAvailable: Boolean = false
)
