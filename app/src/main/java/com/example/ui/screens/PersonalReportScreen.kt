package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseError
import com.example.ui.theme.VioletSecondary
import com.example.ui.viewmodel.TypingViewModel

@Composable
fun PersonalReportScreen(
    viewModel: TypingViewModel,
    modifier: Modifier = Modifier
) {
    val allResults by viewModel.allResults.collectAsStateWithLifecycle()
    val filteredResults by viewModel.filteredResults.collectAsStateWithLifecycle()
    val currentFilter by viewModel.filterLanguage.collectAsStateWithLifecycle()

    var showClearDialog by remember { mutableStateOf(false) }

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
                        text = "ব্যক্তিগত রিপোর্ট",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyanAccent
                    )
                    Text(
                        text = "আপনার প্রতিটি টাইপিং টেস্টের ইতিহাস ও ফলাফল",
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

        // Summary Hero
        item {
            ScoreboardSummaryHero(
                peakWpmEn = peakWpmEn,
                bestAccuracyEn = bestAccuracyEn,
                peakWpmBn = peakWpmBn,
                totalSessions = totalTests
            )
        }

        // Language Filter
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("personal_report_filter_card"),
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

        // Ad Banner
        item {
            AdBannerView(viewModel = viewModel)
        }

        // Section Title
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "টেস্ট হিস্ট্রি ও স্কোরসমূহ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${filteredResults.size} টি রেকর্ড",
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
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Empty",
                            tint = CyanAccent.copy(alpha = 0.7f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "কোনো টেস্ট হিস্ট্রি পাওয়া যায়নি",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "লেসন বা গতি পরীক্ষা সম্পন্ন করলে আপনার ফলাফল স্বয়ংক্রিয়ভাবে ক্লাউডে এবং এখানে সেভ হয়ে যাবে।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(text = "টেস্ট হিস্ট্রি মুছবেন?")
            },
            text = {
                Text("আপনি কি নিশ্চিত যে আপনার ডিভাইসের টেস্ট ইতিহাস মুছে ফেলতে চান?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllResults()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseError)
                ) {
                    Text("হ্যাঁ, মুছুন", color = Color.White)
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
