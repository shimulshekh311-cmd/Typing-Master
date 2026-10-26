package com.example.ui.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardHide
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.bijoy.BijoyLayoutEngine
import com.example.data.model.InputLayoutMode
import com.example.data.model.TypingLanguage
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseError
import com.example.ui.theme.VioletSecondary
import java.util.Locale

@Composable
fun LiveTypingDisplay(
    targetText: String,
    userInput: String,
    liveWpm: Int,
    liveAccuracy: Float,
    elapsedSeconds: Int,
    timeLimitSeconds: Int?,
    mistakeCount: Int,
    language: TypingLanguage,
    inputLayoutMode: InputLayoutMode,
    isPaperMode: Boolean,
    onTogglePaperMode: () -> Unit,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit,
    onPhysicalKey: (Char) -> Unit,
    onBackspace: () -> Unit,
    onToggleLayoutMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val keyboardController = LocalSoftwareKeyboardController.current

    val sampleScrollState = rememberScrollState()
    val typedScrollState = rememberScrollState()
    val isBanglaBijoy = language == TypingLanguage.BANGLA && inputLayoutMode == InputLayoutMode.BIJOY

    // Soft keyboard state (kept OFF by default, especially in landscape)
    var useSoftwareKeyboard by remember { mutableStateOf(false) }
    val softKeyboardFocusRequester = remember { FocusRequester() }

    // When landscape mode is active, automatically keep the software keyboard hidden
    LaunchedEffect(isLandscape) {
        if (isLandscape) {
            keyboardController?.hide()
            useSoftwareKeyboard = false
        }
    }

    // Auto-hide software keyboard unless explicitly enabled by user
    LaunchedEffect(useSoftwareKeyboard) {
        if (!useSoftwareKeyboard) {
            keyboardController?.hide()
        } else {
            kotlinx.coroutines.delay(100)
            try {
                softKeyboardFocusRequester.requestFocus()
            } catch (_: Exception) {}
        }
    }

    // Target Words and Current Word Index
    val targetWords = remember(targetText) {
        if (targetText.isBlank()) emptyList() else targetText.trim().split(Regex("\\s+"))
    }
    val typedWords = remember(userInput) {
        if (userInput.isEmpty()) emptyList() else userInput.split(Regex("\\s+"))
    }
    val currentWordIndex = (typedWords.size - 1).coerceAtLeast(0)

    // Measured Layout Result and Container Height for Line-by-Line Auto-Scrolling
    var sampleTextLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var sampleBoxHeightPx by remember { mutableStateOf(0) }

    val currentCursorOffset = userInput.length.coerceIn(0, targetText.length)
    val currentLineIndex = remember(currentCursorOffset, sampleTextLayoutResult) {
        sampleTextLayoutResult?.let { layout ->
            if (layout.lineCount > 0) {
                layout.getLineForOffset(currentCursorOffset)
            } else 0
        } ?: 0
    }

    // LINE-BY-LINE SYNCHRONIZED SCROLLING:
    // The initial lines visible from the start do not scroll until the user reaches near the bottom of the initial view.
    // Once that threshold is reached, both the sample box and typing box scroll down by exactly ONE line for each completed line.
    LaunchedEffect(currentLineIndex, sampleTextLayoutResult, sampleBoxHeightPx) {
        val layout = sampleTextLayoutResult ?: return@LaunchedEffect
        if (layout.lineCount <= 1) return@LaunchedEffect

        val singleLineHeightPx = (layout.getLineBottom(0) - layout.getLineTop(0)).toInt().coerceAtLeast(1)
        val visibleLinesCount = if (sampleBoxHeightPx > 0) {
            (sampleBoxHeightPx / singleLineHeightPx).coerceAtLeast(2)
        } else {
            if (isLandscape) 4 else 5
        }

        val thresholdLine = (visibleLinesCount - 2).coerceAtLeast(1)
        val targetScrollLine = (currentLineIndex - thresholdLine).coerceAtLeast(0)
        val targetScrollPx = (targetScrollLine * singleLineHeightPx).coerceIn(0, sampleScrollState.maxValue)

        sampleScrollState.animateScrollTo(targetScrollPx)
        typedScrollState.animateScrollTo(targetScrollPx.coerceIn(0, typedScrollState.maxValue))
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // 1. Real-time HUD (WPM, Accuracy, Time, Mistakes)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isLandscape) 6.dp else 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HudStatBadge(
                label = "WPM",
                value = "$liveWpm",
                icon = Icons.Default.Speed,
                tint = CyanAccent,
                modifier = Modifier.weight(1f)
            )

            HudStatBadge(
                label = "Accuracy",
                value = "${String.format(Locale.US, "%.0f", liveAccuracy)}%",
                icon = Icons.Default.Check,
                tint = EmeraldSuccess,
                modifier = Modifier.weight(1f)
            )

            val timerText = if (timeLimitSeconds != null) {
                val remaining = (timeLimitSeconds - elapsedSeconds).coerceAtLeast(0)
                String.format(Locale.US, "%02d:%02d", remaining / 60, remaining % 60)
            } else {
                String.format(Locale.US, "%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60)
            }

            HudStatBadge(
                label = if (timeLimitSeconds != null) "Time Left" else "Elapsed",
                value = timerText,
                icon = Icons.Default.Timer,
                tint = IndigoPrimary,
                modifier = Modifier.weight(1f)
            )

            HudStatBadge(
                label = if (isPaperMode) "Typed Chars" else "Errors",
                value = if (isPaperMode) "${userInput.length}" else "$mistakeCount",
                icon = if (isPaperMode) Icons.Default.Article else Icons.Default.Close,
                tint = if (isPaperMode) CyanAccent else (if (mistakeCount > 0) RoseError else MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier.weight(1f)
            )
        }

        // 2. Mode Toggle Bar (Landscape 50/50 Split Mode vs Fullscreen Paper Copy Mode)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Mode Tab 1: On-screen Sample View
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        if (isPaperMode) onTogglePaperMode()
                    }
                    .testTag("mode_sample_screen_button"),
                shape = RoundedCornerShape(10.dp),
                color = if (!isPaperMode) IndigoPrimary.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = BorderStroke(
                    1.dp,
                    if (!isPaperMode) IndigoPrimary else Color.Transparent
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Splitscreen,
                        contentDescription = "Sample Split Mode",
                        tint = if (!isPaperMode) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isLandscape) "নমুনা স্ক্রিন (৫০/৫০ ভাগ)" else "নমুনা দেখে লিখুন",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (!isPaperMode) FontWeight.Bold else FontWeight.Normal,
                        color = if (!isPaperMode) IndigoPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Mode Tab 2: External Paper Copy Fullscreen Mode
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        if (!isPaperMode) onTogglePaperMode()
                    }
                    .testTag("mode_paper_screen_button"),
                shape = RoundedCornerShape(10.dp),
                color = if (isPaperMode) CyanAccent.copy(alpha = 0.22f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = BorderStroke(
                    1.dp,
                    if (isPaperMode) CyanAccent else Color.Transparent
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Article,
                        contentDescription = "Paper Fullscreen Mode",
                        tint = if (isPaperMode) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "কাগজ দেখে লিখুন (ফুলস্ক্রিন)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isPaperMode) FontWeight.Bold else FontWeight.Normal,
                        color = if (isPaperMode) CyanAccent else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // 3. Status Ribbon: Physical Keyboard with Bijoy layout auto-convert + Landscape Keyboard Status
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "Keyboard Status",
                        tint = if (isBanglaBijoy) IndigoPrimary else CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == TypingLanguage.BANGLA) {
                            if (isBanglaBijoy) "⌨️ বিজয় লেআউট (J=ক, Shift+J=খ)" else "📱 অভ্র / সফটওয়্যার মোড"
                        } else {
                            "⌨️ এক্সটার্নাল ইংরেজি কিবোর্ড (QWERTY)"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // If not in landscape, user can toggle on-screen soft keyboard if needed
                if (!isLandscape) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        modifier = Modifier.clickable {
                            useSoftwareKeyboard = !useSoftwareKeyboard
                            if (!useSoftwareKeyboard) {
                                keyboardController?.hide()
                            }
                        }
                    ) {
                        Text(
                            text = if (useSoftwareKeyboard) "সফট কিবোর্ড বন্ধ" else "সফট কিবোর্ড চালু",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = CyanAccent
                        )
                    }
                } else {
                    // In landscape mode: explicitly show that keyboard is auto-hidden for physical typing
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.KeyboardHide,
                            contentDescription = "Keyboard Auto-Hidden",
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "কিবোর্ড হাইড (এক্সটার্নাল চালু)",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = EmeraldSuccess,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 4. Annotated Target Reading Text (Whole-word formatting to prevent script breaks)
        val onSurfaceColor = MaterialTheme.colorScheme.onSurface
        val annotatedText = buildAnnotatedString {
            if (targetText.isBlank()) {
                return@buildAnnotatedString
            }
            targetWords.forEachIndexed { index, word ->
                when {
                    index < currentWordIndex -> {
                        val isCorrect = index < typedWords.size && 
                            java.text.Normalizer.normalize(typedWords[index], java.text.Normalizer.Form.NFC) == 
                            java.text.Normalizer.normalize(word, java.text.Normalizer.Form.NFC)
                        val color = if (isCorrect) EmeraldSuccess else RoseError
                        withStyle(
                            SpanStyle(
                                color = color,
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = if (isCorrect) TextDecoration.None else TextDecoration.Underline
                            )
                        ) {
                            append(word)
                        }
                    }
                    index == currentWordIndex -> {
                        // Current word being typed (highlighted intact)
                        withStyle(
                            SpanStyle(
                                color = CyanAccent,
                                background = CyanAccent.copy(alpha = 0.28f),
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(word)
                        }
                    }
                    else -> {
                        withStyle(
                            SpanStyle(
                                color = onSurfaceColor.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append(word)
                        }
                    }
                }
                if (index < targetWords.size - 1) {
                    append(" ")
                }
            }
        }

        // Typed text with inline cursor - ensures 100% identical line breaks, spaces, and margins as sample text
        val typedAnnotatedString = buildAnnotatedString {
            append(userInput)
            withStyle(
                SpanStyle(
                    color = if (isBanglaBijoy) IndigoPrimary else CyanAccent,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("▍")
            }
        }

        // 100% Identical formatting parameters for both Sample Box and Typing Box
        val sharedFontSize = if (isLandscape) 19.sp else 21.sp
        val sharedLineHeight = if (isLandscape) 32.sp else 35.sp
        val sharedLetterSpacing = 0.sp
        val sharedFontFamily = FontFamily.SansSerif
        val sharedFontWeight = FontWeight.Medium

        // Shared Preview Key Event Handler for physical keyboard
        val onKeyModifier = Modifier.onPreviewKeyEvent { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                val nativeEvent = keyEvent.nativeKeyEvent
                if (keyEvent.key == Key.Backspace) {
                    onBackspace()
                    return@onPreviewKeyEvent true
                } else if (keyEvent.key == Key.Spacebar) {
                    onPhysicalKey(' ')
                    return@onPreviewKeyEvent true
                } else if (keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter) {
                    onPhysicalKey('\n')
                    return@onPreviewKeyEvent true
                } else {
                    val unicodeChar = nativeEvent.getUnicodeChar(nativeEvent.metaState)
                    if (unicodeChar != 0) {
                        val char = unicodeChar.toChar()
                        if (char in 'a'..'z' || char in 'A'..'Z' || char in "0123456789.,;:'\"/\\`~!@#$%^&*()-=_+[]{}|<>?\n") {
                            onPhysicalKey(char)
                            return@onPreviewKeyEvent true
                        }
                    }
                }
            }
            false
        }

        // 5. Layout Rendering: Landscape 50/50 Split OR Paper Mode Fullscreen OR Portrait Stacked
        if (isPaperMode) {
            // ==========================================
            // PAPER COPY MODE: FULLSCREEN TYPING BOX
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isLandscape) 250.dp else 360.dp)
                    .focusRequester(focusRequester)
                    .focusable()
                    .clickable {
                        try {
                            focusRequester.requestFocus()
                        } catch (_: Exception) {}
                        if (isLandscape || !useSoftwareKeyboard) {
                            keyboardController?.hide()
                        }
                    }
                    .then(onKeyModifier)
                    .testTag("typing_paper_fullscreen_box"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(2.dp, CyanAccent.copy(alpha = 0.85f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    // Header inside Paper Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Article,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "কাগজ দেখে টাইপিং বক্স (ফুলস্ক্রিন)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                        }

                        Text(
                            text = "অটো-স্ক্রোল চালু • Enter এ নতুন লাইন",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Multi-line auto-scrolling content
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(typedScrollState)
                    ) {
                        if (userInput.isEmpty()) {
                            Text(
                                text = "📄 বাইরে রাখা প্রশ্ন বা কাগজ দেখে এখানে সরাসরি টাইপ করুন...\n(সম্পূর্ণ স্ক্রিন লেখার বক্স, কিবোর্ড থেকে টাইপ করুন, Enter চাপলে নতুন লাইনে যাবে)",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                lineHeight = 30.sp
                            )
                        } else {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = userInput,
                                    fontSize = if (isLandscape) 20.sp else 22.sp,
                                    lineHeight = if (isLandscape) 32.sp else 36.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                // Blinking Cursor
                                Box(
                                    modifier = Modifier
                                        .size(width = 3.dp, height = 22.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(CyanAccent)
                                )
                            }
                        }
                    }
                }
            }
        } else if (isLandscape) {
            // ==========================================
            // LANDSCAPE MODE: 50/50 MIDDLE EQUAL SPLIT
            // Left: Sample Box (নমুনা বক্স)
            // Right: Typing Box (লেখার বক্স)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // LEFT 50%: নমুনা বক্স (Sample Box)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            try {
                                focusRequester.requestFocus()
                            } catch (_: Exception) {}
                            keyboardController?.hide()
                        }
                        .testTag("typing_display_card"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "নমুনা অনুচ্ছেদ (বামপাশে)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "শব্দ: ${currentWordIndex + 1}/${targetWords.size}",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(sampleScrollState)
                        ) {
                            Text(
                                text = annotatedText,
                                fontSize = sharedFontSize,
                                lineHeight = sharedLineHeight,
                                letterSpacing = sharedLetterSpacing,
                                fontFamily = sharedFontFamily,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onSizeChanged { size -> sampleBoxHeightPx = size.height },
                                onTextLayout = { layoutResult ->
                                    sampleTextLayoutResult = layoutResult
                                }
                            )
                        }
                    }
                }

                // RIGHT 50%: লেখার বক্স (Typing Box)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .focusRequester(focusRequester)
                        .focusable()
                        .clickable {
                            try {
                                focusRequester.requestFocus()
                            } catch (_: Exception) {}
                            keyboardController?.hide()
                        }
                        .then(onKeyModifier)
                        .testTag("typing_active_box"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(
                        1.5.dp,
                        if (isBanglaBijoy) IndigoPrimary.copy(alpha = 0.8f) else CyanAccent.copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "আপনার লেখার বক্স (ডানপাশে)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isBanglaBijoy) IndigoPrimary else CyanAccent
                            )
                            Text(
                                text = "অটো-স্ক্রোল সক্রিয়",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = EmeraldSuccess
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(typedScrollState)
                        ) {
                            if (userInput.isEmpty()) {
                                Text(
                                    text = if (isBanglaBijoy) "এক্সটার্নাল কিবোর্ড দিয়ে লিখুন (J=ক, Shift+J=খ)..." else "টাইপিং শুরু করুন...",
                                    fontSize = sharedFontSize,
                                    lineHeight = sharedLineHeight,
                                    letterSpacing = sharedLetterSpacing,
                                    fontFamily = sharedFontFamily,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            } else {
                                Text(
                                    text = typedAnnotatedString,
                                    fontSize = sharedFontSize,
                                    lineHeight = sharedLineHeight,
                                    letterSpacing = sharedLetterSpacing,
                                    fontFamily = sharedFontFamily,
                                    fontWeight = sharedFontWeight,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // ==========================================
            // PORTRAIT MODE: VERTICALLY STACKED & ENLARGED
            // Top: Sample Box (Auto-scrolling, 250dp)
            // Bottom: Typing Box (Multi-line, Auto-scrolling, 230dp)
            // ==========================================
            // Sample Box (Enlarged for desktop-grade practice)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clickable {
                        try {
                            focusRequester.requestFocus()
                        } catch (_: Exception) {}
                        if (!useSoftwareKeyboard) keyboardController?.hide()
                    }
                    .testTag("typing_display_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "নমুনা অনুচ্ছেদ",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "শব্দ: ${currentWordIndex + 1}/${targetWords.size}",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(sampleScrollState)
                    ) {
                        Text(
                            text = annotatedText,
                            fontSize = sharedFontSize,
                            lineHeight = sharedLineHeight,
                            letterSpacing = sharedLetterSpacing,
                            fontFamily = sharedFontFamily,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onSizeChanged { size -> sampleBoxHeightPx = size.height },
                            onTextLayout = { layoutResult ->
                                sampleTextLayoutResult = layoutResult
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Typing Box (Enlarged for desktop-grade practice)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .focusRequester(focusRequester)
                    .focusable()
                    .clickable {
                        try {
                            focusRequester.requestFocus()
                        } catch (_: Exception) {}
                        if (!useSoftwareKeyboard) keyboardController?.hide()
                    }
                    .then(onKeyModifier)
                    .testTag("typing_active_box"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(
                    1.5.dp,
                    if (isBanglaBijoy) IndigoPrimary.copy(alpha = 0.8f) else CyanAccent.copy(alpha = 0.8f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "আপনার লেখার বক্স",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isBanglaBijoy) IndigoPrimary else CyanAccent
                        )
                        Text(
                            text = "অটো-স্ক্রোল সক্রিয়",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            color = EmeraldSuccess
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(typedScrollState)
                    ) {
                        if (userInput.isEmpty()) {
                            Text(
                                text = if (isBanglaBijoy) "এক্সটার্নাল কিবোর্ড থেকে সরাসরি টাইপ করুন (J=ক, Shift+J=খ)..." else "টাইপিং শুরু করুন...",
                                fontSize = sharedFontSize,
                                lineHeight = sharedLineHeight,
                                letterSpacing = sharedLetterSpacing,
                                fontFamily = sharedFontFamily,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        } else {
                            Text(
                                text = typedAnnotatedString,
                                fontSize = sharedFontSize,
                                lineHeight = sharedLineHeight,
                                letterSpacing = sharedLetterSpacing,
                                fontFamily = sharedFontFamily,
                                fontWeight = sharedFontWeight,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // 6. Optional Soft Keyboard BasicTextField (Only rendered in portrait if user toggled on-screen keyboard)
        if (useSoftwareKeyboard && !isLandscape) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                BasicTextField(
                    value = userInput,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .focusRequester(softKeyboardFocusRequester),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(CyanAccent),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        autoCorrectEnabled = false
                    ),
                    decorationBox = { inner ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (userInput.isEmpty()) {
                                Text(
                                    text = "সফটওয়্যার কিবোর্ড দিয়ে লিখুন...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            inner()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun HudStatBadge(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = tint,
                fontSize = 15.sp
            )
        }
    }
}
