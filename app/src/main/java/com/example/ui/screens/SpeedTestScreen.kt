package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.datasource.LessonDataSource
import com.example.data.model.TypingLanguage
import com.example.ui.components.AdBannerView
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletSecondary
import com.example.ui.viewmodel.TypingViewModel

enum class SpeedTestSourceMode(
    val title: String,
    val icon: ImageVector
) {
    PARAGRAPH(
        title = "অনুচ্ছেদ থেকে",
        icon = Icons.Default.Article
    ),
    PAPER(
        title = "কাগজ দেখে",
        icon = Icons.Default.Description
    ),
    CUSTOM(
        title = "কাস্টম টেক্সট",
        icon = Icons.Default.EditNote
    )
}

@Composable
fun SpeedTestScreen(
    viewModel: TypingViewModel,
    onStartTest: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDuration by remember { mutableIntStateOf(120) }
    var selectedLanguage by remember { mutableStateOf(TypingLanguage.BANGLA) }
    val currentScrollWpm by viewModel.targetScrollWpm.collectAsStateWithLifecycle()
    var selectedScrollWpm by remember { mutableIntStateOf(if (currentScrollWpm in 20..40) currentScrollWpm else 20) }

    // Pre-test source mode selection: Paragraph, Paper copy, or Custom text
    var selectedSourceMode by remember { mutableStateOf(SpeedTestSourceMode.PARAGRAPH) }

    // Custom text input states
    var customTextTitle by remember { mutableStateOf("") }
    var customTextContent by remember { mutableStateOf("") }

    val availableParagraphs = remember(selectedLanguage) {
        if (selectedLanguage == TypingLanguage.BANGLA) {
            LessonDataSource.speedTestParagraphsBangla
        } else {
            LessonDataSource.speedTestParagraphsEnglish
        }
    }

    var selectedPassageTitle by remember(selectedLanguage) {
        mutableStateOf(availableParagraphs.first().first)
    }
    var isPassageDropdownExpanded by remember { mutableStateOf(false) }

    val activeParagraphPair = remember(selectedPassageTitle, availableParagraphs) {
        availableParagraphs.find { it.first == selectedPassageTitle } ?: availableParagraphs.first()
    }

    // Duration options: 2 min, 5 min, 10 min
    val durationOptions = listOf(
        120 to "২ মিনিট",
        300 to "৫ মিনিট",
        600 to "১০ মিনিট"
    )

    // Scroll speed options: 20 to 40 WPM
    val scrollSpeedOptions = listOf(
        20 to "২০ WPM",
        25 to "২৫ WPM",
        30 to "৩০ WPM",
        35 to "৩৫ WPM",
        40 to "৪০ WPM"
    )

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
                Text(
                    text = "গতি পরীক্ষা",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = CyanAccent
                )

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

        // Google AdMob / Sponsor Banner
        item {
            AdBannerView(viewModel = viewModel)
        }

        // 1. Language Select
        item {
            Text(
                text = "ভাষা নির্বাচন",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LanguageChip(
                    text = "বাংলা",
                    isSelected = selectedLanguage == TypingLanguage.BANGLA,
                    onClick = {
                        selectedLanguage = TypingLanguage.BANGLA
                        viewModel.setSelectedLanguage(TypingLanguage.BANGLA)
                    },
                    color = VioletSecondary,
                    modifier = Modifier.weight(1f)
                )

                LanguageChip(
                    text = "English",
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

        // 2. Pre-test mode selection: Paragraph, Paper copy, or Custom text
        item {
            Text(
                text = "পরীক্ষার ধরন",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpeedTestSourceMode.values().forEach { mode ->
                    val isSelected = selectedSourceMode == mode
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedSourceMode = mode }
                            .testTag("mode_${mode.name.lowercase()}"),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) CyanAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isSelected) CyanAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = mode.icon,
                                contentDescription = mode.title,
                                tint = if (isSelected) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = mode.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) CyanAccent else MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // 3. Selectable Chips for Duration Selection (Left: সময় নির্ধারণ :, Right: ২ মিনিট - ৫ মিনিট - ১০ মিনিট)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "সময় নির্ধারণ :",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    durationOptions.forEach { (seconds, label) ->
                        val isSelected = selectedDuration == seconds
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedDuration = seconds }
                                .testTag("duration_${seconds}s"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) CyanAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) CyanAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) CyanAccent else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Scrolling Target Speed (Single line: fixed "স্ক্রোলিং গতি :", followed by horizontally scrollable WPM options)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "স্ক্রোলিং গতি :",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    scrollSpeedOptions.forEach { (wpm, label) ->
                        val isSelected = selectedScrollWpm == wpm
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedScrollWpm = wpm
                                    viewModel.setTargetScrollWpm(wpm)
                                }
                                .testTag("scroll_wpm_$wpm"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) CyanAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) CyanAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) CyanAccent else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Content depending on selectedSourceMode
        when (selectedSourceMode) {
            SpeedTestSourceMode.PARAGRAPH -> {
                // Passage Selection Card
                item {
                    Text(
                        text = "নমুনা অনুচ্ছেদ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { isPassageDropdownExpanded = !isPassageDropdownExpanded }
                            .testTag("passage_dropdown_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                        ),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Article,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = activeParagraphPair.first,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanAccent
                                    )
                                }
                                Icon(
                                    imageVector = if (isPassageDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Expand",
                                    tint = CyanAccent
                                )
                            }

                            if (isPassageDropdownExpanded) {
                                Spacer(modifier = Modifier.height(10.dp))
                                // Re-roll Random option
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            val otherParagraphs = availableParagraphs.filter { it.first != selectedPassageTitle }
                                            selectedPassageTitle = (if (otherParagraphs.isNotEmpty()) otherParagraphs.random() else availableParagraphs.random()).first
                                            isPassageDropdownExpanded = false
                                        }
                                        .padding(vertical = 2.dp),
                                    color = CyanAccent.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "🎲 অন্য অনুচ্ছেদ নির্বাচন করুন",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanAccent,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp)
                                    )
                                }

                                availableParagraphs.forEach { pair ->
                                    val isSelected = selectedPassageTitle == pair.first
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                selectedPassageTitle = pair.first
                                                isPassageDropdownExpanded = false
                                            }
                                            .padding(vertical = 2.dp),
                                        color = if (isSelected) CyanAccent.copy(alpha = 0.2f) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = pair.first,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) CyanAccent else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = CyanAccent,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Paragraph Preview Card
                item {
                    Text(
                        text = "অনুচ্ছেদ প্রিভিউ",
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
                                text = activeParagraphPair.first,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = activeParagraphPair.second,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 24.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            SpeedTestSourceMode.PAPER -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = IndigoPrimary.copy(alpha = 0.15f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(IndigoPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = "Paper Mode",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "কাগজ দেখে টাইপিং মোড",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary
                            )
                        }
                    }
                }
            }

            SpeedTestSourceMode.CUSTOM -> {
                item {
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
                                text = "কাস্টম টেক্সট",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = customTextTitle,
                                onValueChange = { customTextTitle = it },
                                label = { Text("শিরোনাম (ঐচ্ছিক)") },
                                placeholder = { Text("আমার অনুচ্ছেদ") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("custom_test_title_input"),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = customTextContent,
                                onValueChange = { customTextContent = it },
                                label = { Text("পরীক্ষার টেক্সট লিখুন বা পেস্ট করুন *") },
                                placeholder = { Text("এখানে টেক্সট লিখুন...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .testTag("custom_test_content_input"),
                                maxLines = 6
                            )
                        }
                    }
                }
            }
        }

        // 6. Big Launch Button
        item {
            Spacer(modifier = Modifier.height(6.dp))
            val isCustomBlank = selectedSourceMode == SpeedTestSourceMode.CUSTOM && customTextContent.isBlank()

            Button(
                onClick = {
                    viewModel.setSelectedLanguage(selectedLanguage)
                    when (selectedSourceMode) {
                        SpeedTestSourceMode.PARAGRAPH -> {
                            viewModel.setPaperMode(false)
                            viewModel.startSpeedTest(
                                durationSecs = selectedDuration,
                                difficulty = "Medium",
                                chosenTitle = activeParagraphPair.first,
                                scrollWpm = selectedScrollWpm
                            )
                            onStartTest()
                        }
                        SpeedTestSourceMode.PAPER -> {
                            viewModel.setPaperMode(true)
                            if (selectedScrollWpm > 0) {
                                viewModel.setTargetScrollWpm(selectedScrollWpm)
                            }
                            viewModel.startSession(
                                title = if (selectedLanguage == TypingLanguage.BANGLA) "কাগজ দেখে টাইপিং পরীক্ষা" else "Paper Copy Speed Test",
                                text = "",
                                timeLimitSecs = selectedDuration
                            )
                            onStartTest()
                        }
                        SpeedTestSourceMode.CUSTOM -> {
                            if (customTextContent.isNotBlank()) {
                                viewModel.setPaperMode(false)
                                if (selectedScrollWpm > 0) {
                                    viewModel.setTargetScrollWpm(selectedScrollWpm)
                                }
                                viewModel.startCustomPractice(
                                    title = customTextTitle.ifBlank {
                                        if (selectedLanguage == TypingLanguage.BANGLA) "কাস্টম গতি পরীক্ষা" else "Custom Speed Test"
                                    },
                                    customText = customTextContent,
                                    timeLimitSecs = selectedDuration
                                )
                                onStartTest()
                            }
                        }
                    }
                },
                enabled = !isCustomBlank,
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
                    text = "পরীক্ষা শুরু করুন (${selectedDuration / 60} মিনিট)",
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
