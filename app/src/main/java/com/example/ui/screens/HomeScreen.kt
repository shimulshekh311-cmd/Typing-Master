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
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.datasource.LessonDataSource
import com.example.data.model.Difficulty
import com.example.data.model.InputLayoutMode
import com.example.data.model.Lesson
import com.example.data.model.TypingLanguage
import com.example.ui.components.AdBannerView
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
    var showCommunityDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val lessons = if (selectedLanguage == TypingLanguage.BANGLA) {
        LessonDataSource.banglaLessons
    } else {
        LessonDataSource.englishLessons
    }

    if (showCommunityDialog) {
        AlertDialog(
            onDismissRequest = { showCommunityDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = "কমিউনিটি ও সাপোর্ট",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "টাইপিং মাস্টার সম্পূর্ণ ফ্রি এবং বিজ্ঞাপন সমর্থিত। আমাদের সোশ্যাল কমিউনিটিতে যুক্ত হয়ে আপডেট পান ও প্রতিযোগিতা করুন:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/groups/712399671177659"))
                                )
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = IndigoPrimary.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("👥 টাইপিং কমিউনিটি গ্রুপ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Icon(Icons.Default.OpenInNew, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/profile.php?id=61569426362539"))
                                )
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = VioletSecondary.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🌐 অফিসিয়াল ফেসবুক পেজ", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Icon(Icons.Default.OpenInNew, contentDescription = null, tint = VioletSecondary, modifier = Modifier.size(16.dp))
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/8801635586380"))
                                )
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = EmeraldSuccess.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("💬 WhatsApp বিজনেস (01635586380)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Icon(Icons.Default.OpenInNew, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCommunityDialog = false }) {
                    Text("বন্ধ করুন", color = CyanAccent, fontWeight = FontWeight.Bold)
                }
            }
        )
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "টাইপিং মাস্টার (Typing Master)",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyanAccent
                    )
                    Text(
                        text = "১০০% ফ্রি বাংলা ও ইংরেজি টাইপিং প্ল্যাটফর্ম • বিজ্ঞাপন সমর্থিত",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldSuccess,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(
                    onClick = { showCommunityDialog = true },
                    modifier = Modifier.testTag("community_hub_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = "Community Hub",
                        tint = CyanAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Google AdMob / Sponsor Banner Component
        item {
            AdBannerView(
                viewModel = viewModel,
                onNavigateToSpeedTest = onNavigateToSpeedTest
            )
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
