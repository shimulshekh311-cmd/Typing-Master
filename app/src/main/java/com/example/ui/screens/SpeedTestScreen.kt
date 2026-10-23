package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.datasource.LessonDataSource
import com.example.data.model.InputLayoutMode
import com.example.data.model.TypingLanguage
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletSecondary
import com.example.ui.viewmodel.TypingViewModel

@Composable
fun SpeedTestScreen(
    viewModel: TypingViewModel,
    onStartTest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDuration by remember { mutableIntStateOf(60) }
    var selectedLanguage by remember { mutableStateOf(TypingLanguage.BANGLA) }
    var selectedDifficulty by remember { mutableStateOf("Medium") }

    val sampleParagraph = if (selectedLanguage == TypingLanguage.BANGLA) {
        LessonDataSource.speedTestParagraphsBangla.first().second
    } else {
        LessonDataSource.speedTestParagraphsEnglish.first().second
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "গতি পরীক্ষা (Speed Test)",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyanAccent
                    )
                    Text(
                        text = "নির্দিষ্ট সময়ে টাইপিংয়ের গতি ও নির্ভুলতা পরিমাপ করুন",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyanAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "Bolt",
                        tint = CyanAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Language Select
        item {
            Text(
                text = "ভাষা নির্বাচন (Choose Language)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LanguageChip(
                    text = "বাংলা (Bengali)",
                    isSelected = selectedLanguage == TypingLanguage.BANGLA,
                    onClick = {
                        selectedLanguage = TypingLanguage.BANGLA
                        viewModel.setSelectedLanguage(TypingLanguage.BANGLA)
                    },
                    color = VioletSecondary,
                    modifier = Modifier.weight(1f)
                )

                LanguageChip(
                    text = "English (ইংরেজি)",
                    isSelected = selectedLanguage == TypingLanguage.ENGLISH,
                    onClick = {
                        selectedLanguage = TypingLanguage.ENGLISH
                        viewModel.setSelectedLanguage(TypingLanguage.ENGLISH)
                    },
                    color = CyanAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Layout Mode for Bangla Speed Test
        if (selectedLanguage == TypingLanguage.BANGLA) {
            item {
                val currentLayout by viewModel.inputLayoutMode.collectAsStateWithLifecycle()
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "বাংলা টাইপিং লেআউট নির্বাচন:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.inputLayoutMode.value = InputLayoutMode.BIJOY },
                                color = if (currentLayout == InputLayoutMode.BIJOY) IndigoPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    if (currentLayout == InputLayoutMode.BIJOY) IndigoPrimary else Color.Transparent
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "⌨️ বিজয় লেআউট",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (currentLayout == InputLayoutMode.BIJOY) IndigoPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "এক্সটার্নাল কিবোর্ড মোড",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.inputLayoutMode.value = InputLayoutMode.AVRO_SYSTEM },
                                color = if (currentLayout == InputLayoutMode.AVRO_SYSTEM) VioletSecondary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    if (currentLayout == InputLayoutMode.AVRO_SYSTEM) VioletSecondary else Color.Transparent
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "📱 অভ্র / সাধারণ",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (currentLayout == InputLayoutMode.AVRO_SYSTEM) VioletSecondary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "মোবাইল কিবোর্ড",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Duration Select (30s, 60s, 120s)
        item {
            Text(
                text = "সময়সীমা নির্বাচন (Select Duration)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DurationOption(
                    title = "৩০ সেকেন্ড",
                    seconds = 30,
                    subtitle = "দ্রুত স্প্রিন্ট",
                    isSelected = selectedDuration == 30,
                    onClick = { selectedDuration = 30 },
                    modifier = Modifier.weight(1f)
                )
                DurationOption(
                    title = "১ মিনিট",
                    seconds = 60,
                    subtitle = "প্রমিত পরীক্ষা",
                    isSelected = selectedDuration == 60,
                    onClick = { selectedDuration = 60 },
                    modifier = Modifier.weight(1f)
                )
                DurationOption(
                    title = "২ মিনিট",
                    seconds = 120,
                    subtitle = "ধৈর্য্য পরীক্ষা",
                    isSelected = selectedDuration == 120,
                    onClick = { selectedDuration = 120 },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Paragraph Preview Card
        item {
            Text(
                text = "অনুচ্ছেদ নমুনা (Preview)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "\"${sampleParagraph.take(150)}...\"",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Big Launch Button
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = {
                    if (!viewModel.canAccessPractice()) {
                        viewModel.openSubscriptionDialog()
                    } else {
                        viewModel.setSelectedLanguage(selectedLanguage)
                        viewModel.startSpeedTest(selectedDuration, selectedDifficulty)
                        onStartTest()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("start_speed_test_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "পরীক্ষা শুরু করুন ($selectedDuration সেকেন্ড)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun LanguageChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isSelected) color else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DurationOption(
    title: String,
    seconds: Int,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) IndigoPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
