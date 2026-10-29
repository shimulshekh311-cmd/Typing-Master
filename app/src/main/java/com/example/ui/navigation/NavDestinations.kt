package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.ui.graphics.vector.ImageVector

enum class MainDestination(
    val route: String,
    val titleEn: String,
    val titleBn: String,
    val icon: ImageVector
) {
    LEARN("learn", "Lessons", "পাঠ", Icons.Default.School),
    SPEED_TEST("speed_test", "Speed Test", "গতি পরীক্ষা", Icons.Default.Speed),
    LEADERBOARD("leaderboard", "Leaderboard", "লিডারবোর্ড", Icons.Default.Leaderboard),
    MONTHLY_REPORT("monthly_report", "Report", "রিপোর্ট", Icons.Default.BarChart),
    ACCOUNT("account", "Account", "একাউন্ট", Icons.Default.AccountCircle)
}

