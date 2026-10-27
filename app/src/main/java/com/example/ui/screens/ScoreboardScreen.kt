package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AdBannerView
import com.example.ui.components.GoogleSignInCard
import com.example.ui.components.LeaderboardItemCard
import com.example.ui.components.ScoreboardSummaryHero
import com.example.ui.components.TypingResultItemCard
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseError
import com.example.ui.theme.VioletSecondary
import com.example.ui.viewmodel.TypingViewModel

@Composable
fun ScoreboardScreen(
    viewModel: TypingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isSigningIn by viewModel.isSigningIn.collectAsStateWithLifecycle()
    val authMessage by viewModel.authMessage.collectAsStateWithLifecycle()

    val leaderboardEntries by viewModel.leaderboardEntries.collectAsStateWithLifecycle()
    val leaderboardFilter by viewModel.leaderboardFilter.collectAsStateWithLifecycle()

    val allResults by viewModel.allResults.collectAsStateWithLifecycle()
    val filteredResults by viewModel.filteredResults.collectAsStateWithLifecycle()
    val currentFilter by viewModel.filterLanguage.collectAsStateWithLifecycle()

    var selectedMainTab by remember { mutableIntStateOf(0) } // 0 = Online Leaderboard, 1 = Personal History
    var showClearDialog by remember { mutableStateOf(false) }

    // Aggregate stats for personal history
    val englishResults = allResults.filter { it.language == "ENGLISH" }
    val banglaResults = allResults.filter { it.language == "BANGLA" }

    val peakWpmEn = englishResults.maxOfOrNull { it.wpm } ?: 0
    val bestAccuracyEn = englishResults.maxOfOrNull { it.accuracy } ?: 0f
    val peakWpmBn = banglaResults.maxOfOrNull { it.wpm } ?: 0
    val totalTests = allResults.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "স্কোরবোর্ড ও লিডারবোর্ড",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyanAccent
                    )
                    Text(
                        text = "গুগল একাউন্ট দিয়ে সর্বোচ্চ গতির অনলাইন র‍্যাংকিং",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (selectedMainTab == 1 && allResults.isNotEmpty()) {
                    IconButton(
                        onClick = { showClearDialog = true },
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear History",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Google Sign-In / Account Card
        item {
            GoogleSignInCard(
                currentUser = currentUser,
                isSigningIn = isSigningIn,
                authMessage = authMessage,
                onSignIn = { viewModel.signInWithGoogle(context) },
                onSignOut = { viewModel.signOut() }
            )
        }

        // Ad Banner
        item {
            AdBannerView(viewModel = viewModel)
        }

        // Main Tab Selection (Online Leaderboard vs Personal History)
        item {
            TabRow(
                selectedTabIndex = selectedMainTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = CyanAccent,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedMainTab]),
                        color = CyanAccent,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("scoreboard_main_tabs")
            ) {
                Tab(
                    selected = selectedMainTab == 0,
                    onClick = { selectedMainTab = 0 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "অনলাইন লিডারবোর্ড",
                                fontWeight = if (selectedMainTab == 0) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    },
                    modifier = Modifier.testTag("tab_online_leaderboard")
                )

                Tab(
                    selected = selectedMainTab == 1,
                    onClick = { selectedMainTab = 1 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "ব্যক্তিগত টেস্ট ইতিহাস",
                                fontWeight = if (selectedMainTab == 1) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    },
                    modifier = Modifier.testTag("tab_personal_history")
                )
            }
        }

        // Content for Tab 0: ONLINE LEADERBOARD
        if (selectedMainTab == 0) {
            // Language Sub-filters for Leaderboard
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterTabButton(
                            text = "সব ভাষা",
                            isSelected = leaderboardFilter == "ALL",
                            onClick = { viewModel.setLeaderboardFilter("ALL") },
                            color = IndigoPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        FilterTabButton(
                            text = "বাংলা",
                            isSelected = leaderboardFilter == "BANGLA",
                            onClick = { viewModel.setLeaderboardFilter("BANGLA") },
                            color = VioletSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        FilterTabButton(
                            text = "English",
                            isSelected = leaderboardFilter == "ENGLISH",
                            onClick = { viewModel.setLeaderboardFilter("ENGLISH") },
                            color = CyanAccent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (currentUser == null) {
                // Notice to Sign In to see full online leaderboard
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .testTag("leaderboard_login_prompt"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Trophy",
                                tint = CyanAccent,
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = "গুগল সাইন-ইন আবশ্যক",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "অন্যান্য ব্যবহারকারীরা সর্বোচ্চ কত গতি রেকর্ড করেছে তা দেখতে এবং আপনার নিজের রেকর্ড যুক্ত করতে ফোনের গুগল একাউন্ট দিয়ে লগইন করুন।",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = { viewModel.signInWithGoogle(context) },
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("leaderboard_prompt_sign_in_button")
                            ) {
                                Text("গুগল দিয়ে লগইন করুন", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Authenticated Leaderboard List Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "শীর্ষ টাইপিস্টদের তালিকা (Top Typists)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${leaderboardEntries.size} জন",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (leaderboardEntries.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Empty",
                                    tint = CyanAccent.copy(alpha = 0.5f),
                                    modifier = Modifier.size(46.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "এখনও কোনো রেকর্ড যুক্ত হয়নি",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "একটি টাইপিং টেস্ট সম্পন্ন করুন অথবা আপনার পূর্বের রেকর্ড সিঙ্ক করতে উপরে বাটন চাপুন।",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.syncBestLocalScoreToLeaderboard() },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("আমার সেরা স্কোর সিঙ্ক করুন", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    itemsIndexed(leaderboardEntries, key = { _, item -> item.id }) { index, entry ->
                        LeaderboardItemCard(
                            rank = index + 1,
                            entry = entry,
                            isCurrentUser = entry.userId == currentUser?.uid
                        )
                    }
                }
            }
        } else {
            // Content for Tab 1: PERSONAL TEST HISTORY
            item {
                ScoreboardSummaryHero(
                    peakWpmEn = peakWpmEn,
                    bestAccuracyEn = bestAccuracyEn,
                    peakWpmBn = peakWpmBn,
                    totalSessions = totalTests
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("scoreboard_filter_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterTabButton(
                            text = "সব (${allResults.size})",
                            isSelected = currentFilter == "ALL",
                            onClick = { viewModel.setFilterLanguage("ALL") },
                            color = IndigoPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        FilterTabButton(
                            text = "English (${englishResults.size})",
                            isSelected = currentFilter == "ENGLISH",
                            onClick = { viewModel.setFilterLanguage("ENGLISH") },
                            color = CyanAccent,
                            modifier = Modifier.weight(1f)
                        )
                        FilterTabButton(
                            text = "বাংলা (${banglaResults.size})",
                            isSelected = currentFilter == "BANGLA",
                            onClick = { viewModel.setFilterLanguage("BANGLA") },
                            color = VioletSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ব্যক্তিগত পরীক্ষার ইতিহাস",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${filteredResults.size} টি ফলাফল",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (filteredResults.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Leaderboard,
                                contentDescription = "Empty",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "কোনো রেকর্ড পাওয়া যায়নি",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "অনুশীলন বা গতি পরীক্ষা সম্পন্ন করলে এখানে স্কোর দেখতে পাবেন।",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            } else {
                items(filteredResults, key = { it.id }) { result ->
                    TypingResultItemCard(
                        result = result,
                        onDelete = { viewModel.deleteResult(result.id) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Confirmation Dialog for Clearing All
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("সব ব্যক্তিগত স্কোর মুছে ফেলবেন?") },
            text = { Text("আপনার ডিভাইসে সংরক্ষিত সকল পরীক্ষার ইতিহাস মুছে যাবে।") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllResults()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseError)
                ) {
                    Text("মুছে ফেলুন", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showClearDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
private fun FilterTabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) color else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
