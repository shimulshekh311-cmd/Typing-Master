package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.datasource.LessonDataSource
import com.example.data.model.Difficulty
import com.example.data.model.InputLayoutMode
import com.example.data.model.Lesson
import com.example.data.model.TypingLanguage
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletSecondary
import com.example.ui.viewmodel.TypingViewModel

@Composable
fun HomeScreen(
    viewModel: TypingViewModel,
    onStartLesson: (Lesson) -> Unit,
    onNavigateToSpeedTest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember { mutableStateOf(TypingLanguage.BANGLA) }
    var showCustomInput by remember { mutableStateOf(false) }
    var customTitle by remember { mutableStateOf("") }
    var customText by remember { mutableStateOf("") }

    val lessons = if (selectedLanguage == TypingLanguage.BANGLA) {
        LessonDataSource.banglaLessons
    } else {
        LessonDataSource.englishLessons
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App Header Banner
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "টাইপিং মাস্টার (Typing Master)",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyanAccent
                    )
                    Text(
                        text = "বাংলা ও ইংরেজি টাইপিং দক্ষতা বৃদ্ধির প্ল্যাটফর্ম",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(IndigoPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "Keyboard",
                        tint = IndigoPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Language Switcher Selector
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("language_selector_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LanguageTabButton(
                        text = "বাংলা (Bengali)",
                        isSelected = selectedLanguage == TypingLanguage.BANGLA,
                        onClick = {
                            selectedLanguage = TypingLanguage.BANGLA
                            viewModel.setSelectedLanguage(TypingLanguage.BANGLA)
                        },
                        accentColor = VioletSecondary,
                        modifier = Modifier.weight(1f)
                    )

                    LanguageTabButton(
                        text = "English (ইংরেজি)",
                        isSelected = selectedLanguage == TypingLanguage.ENGLISH,
                        onClick = {
                            selectedLanguage = TypingLanguage.ENGLISH
                            viewModel.setSelectedLanguage(TypingLanguage.ENGLISH)
                        },
                        accentColor = CyanAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Keyboard layout mode selector when Bangla is selected
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
                            text = "বাংলা টাইপিং কিবোর্ড মোড:",
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
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "⌨️ বিজয় লেআউট",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (currentLayout == InputLayoutMode.BIJOY) IndigoPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "এক্সটার্নাল কিবোর্ড ও চাকরির পরীক্ষা",
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
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "📱 অভ্র / সাধারণ",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (currentLayout == InputLayoutMode.AVRO_SYSTEM) VioletSecondary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "সরাসরি সফটওয়্যার কিবোর্ড",
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

        // Quick Speed Sprint Launch Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSpeedTest() }
                    .testTag("quick_speed_test_banner"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = IndigoPrimary.copy(alpha = 0.15f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(IndigoPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Speed Test",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "দ্রুত গতি পরীক্ষা (Speed Sprint)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "৩০ বা ৬০ সেকেন্ডে আপনার WPM ও নির্ভুলতা মাপুন",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Go",
                        tint = IndigoPrimary
                    )
                }
            }
        }

        // Custom Text Section Toggle
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCustomInput = !showCustomInput },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showCustomInput) "▲ নিজস্ব লেখা লুকান" else "▼ নিজের পছন্দের টেক্সট লিখে অনুশীলন করুন (Custom Text)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = CyanAccent
                    )
                }
            }
        }

        // Custom Text Input form
        if (showCustomInput) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        OutlinedTextField(
                            value = customTitle,
                            onValueChange = { customTitle = it },
                            label = { Text("অনুচ্ছেদের শিরোনাম (Title)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = customText,
                            onValueChange = { customText = it },
                            label = { Text("অনুশীলনের টেক্সট পেস্ট বা লিখুন...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            maxLines = 5
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                if (customText.isNotBlank()) {
                                    viewModel.startCustomPractice(customTitle, customText, null)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("start_custom_text_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("কাস্টম টেক্সটে টাইপিং শুরু করুন", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Structured Lessons Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedLanguage == TypingLanguage.BANGLA) "বাংলা টাইপিং পাঠমালা (Lessons)" else "English Typing Lessons",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${lessons.size} টি পাঠ",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Lesson Items
        items(lessons, key = { it.id }) { lesson ->
            LessonCardItem(
                lesson = lesson,
                onStart = { onStartLesson(lesson) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun LanguageTabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) accentColor else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LessonCardItem(
    lesson: Lesson,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBangla = lesson.language == TypingLanguage.BANGLA
    val diffColor = when (lesson.difficulty) {
        Difficulty.BEGINNER -> EmeraldSuccess
        Difficulty.INTERMEDIATE -> CyanAccent
        Difficulty.ADVANCED -> AmberWarning
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("lesson_card_${lesson.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = diffColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (isBangla) lesson.difficulty.labelBn else lesson.difficulty.labelEn,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = diffColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = IndigoPrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "লক্ষ্য: ${lesson.targetWpm} WPM",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = IndigoPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isBangla) lesson.titleBn else lesson.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isBangla) lesson.descriptionBn else lesson.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Text preview snippet
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
            ) {
                Text(
                    text = "\"${lesson.targetText.take(45)}...\"",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("start_lesson_${lesson.id}"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isBangla) VioletSecondary else CyanAccent
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isBangla) "অনুশীলন শুরু করুন" else "Start Practice",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
