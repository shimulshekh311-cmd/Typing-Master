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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.UnfoldMore
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
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletSecondary
import com.example.ui.viewmodel.TypingViewModel

enum class SpeedTestSourceMode(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
) {
    PARAGRAPH(
        title = "অনুচ্ছেদ থেকে",
        subtitle = "অ্যাপের প্রিসেট অনুচ্ছেদ",
        icon = Icons.Default.Article
    ),
    PAPER(
        title = "কাগজ দেখে",
        subtitle = "প্রিন্ট কপি বা বই দেখে",
        icon = Icons.Default.Description
    ),
    CUSTOM(
        title = "কাস্টম টেক্সট",
        subtitle = "পছন্দের টেক্সট লিখে",
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
    var selectedScrollWpm by remember { mutableIntStateOf(currentScrollWpm) }

    // Pre-test source mode selection: Paragraph, Paper copy, or Custom text
    var selectedSourceMode by remember { mutableStateOf(SpeedTestSourceMode.PARAGRAPH) }

    // Custom text input states
    var customTextTitle by remember { mutableStateOf("") }
    var customTextContent by remember { mutableStateOf("") }

    // Dropdown expanded states
    var isDurationDropdownExpanded by remember { mutableStateOf(false) }
    var isScrollDropdownExpanded by remember { mutableStateOf(false) }

    val availableParagraphs = remember(selectedLanguage) {
        if (selectedLanguage == TypingLanguage.BANGLA) {
            LessonDataSource.speedTestParagraphsBangla
        } else {
            LessonDataSource.speedTestParagraphsEnglish
        }
    }

    var selectedPassageTitle by remember(selectedLanguage) {
        mutableStateOf(availableParagraphs.random().first)
    }
    var isPassageDropdownExpanded by remember { mutableStateOf(false) }

    val activeParagraphPair = remember(selectedPassageTitle, availableParagraphs) {
        availableParagraphs.find { it.first == selectedPassageTitle } ?: availableParagraphs.first()
    }

    // Available duration options
    val durationOptions = listOf(
        60 to "১ মিনিট (৬০ সেকেন্ড) • দ্রুত প্রস্তুতি",
        120 to "২ মিনিট (১২০ সেকেন্ড) • আদর্শ স্প্রিন্ট",
        180 to "৩ মিনিট (১৮০ সেকেন্ড) • স্ট্যান্ডার্ড টেস্ট",
        300 to "৫ মিনিট (৩০০ সেকেন্ড) • সরকারি চাকরি পরীক্ষা",
        600 to "১০ মিনিট (৬০০ সেকেন্ড) • পূর্ণ ব্যবহারিক পরীক্ষা",
        900 to "১৫ মিনিট (৯০০ সেকেন্ড) • দীর্ঘ সহনশীলতা"
    )

    // Available scroll speed options
    val scrollSpeedOptions = listOf(
        0 to "টাইপিং অনুসারী (স্বাভাবিক গতি - কোনো পেসার নেই)",
        15 to "১৫ WPM • প্রাথমিক শিক্ষার্থী",
        20 to "২০ WPM • সাধারণ গতি",
        25 to "২৫ WPM • সরকারি চাকরির কাটঅফ",
        30 to "৩০ WPM • স্ট্যান্ডার্ড গতি",
        35 to "৩৫ WPM • উন্নত গতি",
        40 to "৪০ WPM • দ্রুত পেশাদার গতি",
        50 to "৫০ WPM • বিশেষজ্ঞ গতি",
        60 to "৬০ WPM • মাস্টার টাইপিস্ট"
    )

    val currentDurationLabel = durationOptions.find { it.first == selectedDuration }?.second
        ?: "${selectedDuration / 60} মিনিট"

    val currentScrollLabel = scrollSpeedOptions.find { it.first == selectedScrollWpm }?.second
        ?: if (selectedScrollWpm == 0) "টাইপিং অনুসারী" else "$selectedScrollWpm WPM"

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

        // Google AdMob / Sponsor Banner
        item {
            AdBannerView(viewModel = viewModel)
        }

        // 1. Language Select
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
                    text = "বাংলা (বিজয় লেআউট)",
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

        // 2. Pre-test mode selection: Paragraph, Paper copy, or Custom text
        item {
            Text(
                text = "পরীক্ষার ধরন নির্বাচন (Typing Source)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "আপনি কীভাবে টাইপিং পরীক্ষাটি সম্পন্ন করতে চান তা নির্বাচন করুন",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = mode.icon,
                                contentDescription = mode.title,
                                tint = if (isSelected) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = mode.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) CyanAccent else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = mode.subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp,
                                color = if (isSelected) CyanAccent.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // 3. Dropdown Menu for Duration Selection (সময় নির্ধারণ)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "সময়সীমা নির্ধারণ (Duration)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${selectedDuration / 60} মিনিট",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { isDurationDropdownExpanded = true }
                            .testTag("duration_dropdown_trigger"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                        ),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 13.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = currentDurationLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Icon(
                                imageVector = if (isDurationDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = CyanAccent
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = isDurationDropdownExpanded,
                        onDismissRequest = { isDurationDropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        durationOptions.forEach { (seconds, label) ->
                            val isSelected = selectedDuration == seconds
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) CyanAccent else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    selectedDuration = seconds
                                    isDurationDropdownExpanded = false
                                },
                                trailingIcon = {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = CyanAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // 4. Dropdown Menu for Scrolling Target / Pacer Speed (স্ক্রোলিং লক্ষ্যমাত্রা)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "স্ক্রোলিং গতি ও লক্ষ্যমাত্রা (Scroll Speed Target)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (selectedScrollWpm == 0) "স্বাভাবিক" else "$selectedScrollWpm WPM",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "নির্দিষ্ট লক্ষ্যমাত্রায় প্রথম থেকেই স্ক্রোল করবে অথবা লেখার সাথে সাথে এক লাইন করে স্ক্রোল হবে",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { isScrollDropdownExpanded = true }
                            .testTag("scroll_dropdown_trigger"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                        ),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 13.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = currentScrollLabel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1
                                )
                            }
                            Icon(
                                imageVector = if (isScrollDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = CyanAccent
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = isScrollDropdownExpanded,
                        onDismissRequest = { isScrollDropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        scrollSpeedOptions.forEach { (wpm, label) ->
                            val isSelected = selectedScrollWpm == wpm
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = label,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) CyanAccent else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    selectedScrollWpm = wpm
                                    viewModel.setTargetScrollWpm(wpm)
                                    isScrollDropdownExpanded = false
                                },
                                trailingIcon = {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = CyanAccent,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // 5. Content depending on selectedSourceMode
        when (selectedSourceMode) {
            SpeedTestSourceMode.PARAGRAPH -> {
                // Passage Selection Card (Government Job typing test passages + General)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "অনুচ্ছেদ বেছে নিন (Choose Passage)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${availableParagraphs.size} টি অনুচ্ছেদ",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanAccent
                        )
                    }
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
                                    Column {
                                        Text(
                                            text = activeParagraphPair.first,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = CyanAccent
                                        )
                                        Text(
                                            text = "🎲 স্বয়ংক্রিয়ভাবে নির্বাচিত • পরিবর্তন করতে ট্যাপ করুন",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = if (isPassageDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = "Expand",
                                    tint = CyanAccent
                                )
                            }

                            if (isPassageDropdownExpanded) {
                                Spacer(modifier = Modifier.height(12.dp))
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
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🎲 নতুন করে আরেকটি এলোমেলো অনুচ্ছেদ নিন (Shuffle Random)",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = CyanAccent,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }

                                // Available passages
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
                        text = "নির্বাচিত অনুচ্ছেদ নমুনা (Preview)",
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
                            Spacer(modifier = Modifier.height(4.dp))
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
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                                Column {
                                    Text(
                                        text = "কাগজ দেখে টাইপিং মোড",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = IndigoPrimary
                                    )
                                    Text(
                                        text = "মুদ্রিত কপি, প্রশ্ন বা বই থেকে টাইপ করার জন্য",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "এই মোডে স্ক্রিনে কোনো অনুচ্ছেদ থাকবে না। আপনার সামনে থাকা হার্ডকপি বা সরকারি পরীক্ষার প্রশ্ন দেখে সরাসরি টাইপ করতে পারবেন। সময় গণনা ও টাইপ করা শব্দের গতি (WPM) স্বয়ংক্রিয়ভাবে পরিমাপ করা হবে।",
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface
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
                                text = "কাস্টম টেক্সট ইনপুট (Your Custom Passage)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = customTextTitle,
                                onValueChange = { customTextTitle = it },
                                label = { Text("অনুচ্ছেদের শিরোনাম (ঐচ্ছিক)") },
                                placeholder = { Text("যেমন: আমার প্রিয় অনুচ্ছেদ") },
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
                                placeholder = { Text("এখানে আপনি যে লেখাটি টাইপ করে পরীক্ষা দিতে চান তা লিখুন...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .testTag("custom_test_content_input"),
                                maxLines = 6
                            )
                            if (customTextContent.isBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "* পরীক্ষা শুরু করতে অন্তত কিছু টেক্সট লিখুন বা পেস্ট করুন",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AmberWarning
                                )
                            }
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
