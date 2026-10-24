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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Leaderboard
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AdBannerView
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
    val allResults by viewModel.allResults.collectAsStateWithLifecycle()
    val filteredResults by viewModel.filteredResults.collectAsStateWithLifecycle()
    val currentFilter by viewModel.filterLanguage.collectAsStateWithLifecycle()

    var showClearDialog by remember { mutableStateOf(false) }

    // Aggregate stats
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
                        text = "স্কোরবোর্ড (Scoreboard)",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyanAccent
                    )
                    Text(
                        text = "গতি এবং নির্ভুলতার পূর্ণাঙ্গ তালিকা ও রেকর্ড",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (allResults.isNotEmpty()) {
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

        // Google AdMob / Sponsor Banner
        item {
            AdBannerView(viewModel = viewModel)
        }

        // Hero Summary Banner
        item {
            ScoreboardSummaryHero(
                peakWpmEn = peakWpmEn,
                bestAccuracyEn = bestAccuracyEn,
                peakWpmBn = peakWpmBn,
                totalSessions = totalTests
            )
        }

        // Filter Tabs (All, English, Bangla)
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

        // List Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "পরীক্ষার ইতিহাস (Test Records)",
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

        // Empty state or Items
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

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Confirmation Dialog for Clearing All
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("সব স্কোর মুছে ফেলবেন?") },
            text = { Text("আপনার সংরক্ষিত সকল পরীক্ষার ইতিহাস ও স্কোরবোর্ড তথ্য মুছে যাবে।") },
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
