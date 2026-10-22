package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardHide
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
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
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit,
    onPhysicalKey: (Char) -> Unit,
    onBackspace: () -> Unit,
    onToggleLayoutMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val isBanglaBijoy = language == TypingLanguage.BANGLA && inputLayoutMode == InputLayoutMode.BIJOY
    val keyboardController = LocalSoftwareKeyboardController.current

    // By default, external keyboard mode is ON (no soft keyboard popping up)
    var useSoftwareKeyboard by remember { mutableStateOf(false) }

    // If external keyboard mode is active, keep software keyboard hidden
    LaunchedEffect(useSoftwareKeyboard) {
        if (!useSoftwareKeyboard) {
            keyboardController?.hide()
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Real-time HUD (WPM, Accuracy, Time, Errors)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
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
                label = "Errors",
                value = "$mistakeCount",
                icon = Icons.Default.Close,
                tint = if (mistakeCount > 0) RoseError else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }

        // Mode Status Bar: External Keyboard with Bijoy Auto-Convert
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(12.dp),
            color = if (isBanglaBijoy) IndigoPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isBanglaBijoy) IndigoPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = "External Keyboard",
                        tint = if (isBanglaBijoy) IndigoPrimary else CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == TypingLanguage.BANGLA) {
                            if (isBanglaBijoy) "⌨️ এক্সটার্নাল কিবোর্ড (বিজয় কনভার্টার চালু)" else "📱 অভ্র / সফটওয়্যার মোড"
                        } else {
                            "⌨️ এক্সটার্নাল কিবোর্ড (ইংরেজি QWERTY)"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isBanglaBijoy) IndigoPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }

                // Soft Keyboard toggle (keeps Gboard off by default)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    modifier = Modifier.clickable {
                        useSoftwareKeyboard = !useSoftwareKeyboard
                        if (!useSoftwareKeyboard) {
                            keyboardController?.hide()
                        }
                    }
                ) {
                    Text(
                        text = if (useSoftwareKeyboard) "সফট কিবোর্ড বন্ধ করুন" else "সফট কিবোর্ড চালু",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = CyanAccent
                    )
                }
            }
        }

        // Target Reading Area: Formatted at Word Boundaries to 100% prevent script fragmentation
        val onSurfaceColor = MaterialTheme.colorScheme.onSurface

        val annotatedText = buildAnnotatedString {
            val targetWords = targetText.split(" ")
            val typedWords = userInput.split(" ")
            val currentWordIndex = (typedWords.size - 1).coerceAtLeast(0)

            targetWords.forEachIndexed { index, word ->
                when {
                    index < currentWordIndex -> {
                        val isCorrect = index < typedWords.size && typedWords[index] == word
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
                        // Current word being typed (highlighted whole without internal breaks)
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
                        // Upcoming text in pristine typography
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

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clickable {
                    if (useSoftwareKeyboard) {
                        focusRequester.requestFocus()
                    }
                }
                .testTag("typing_display_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = annotatedText,
                    fontSize = 22.sp,
                    lineHeight = 36.sp,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Active Typing Box (Displays typed text with real-time feedback)
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .focusable()
                .onPreviewKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown) {
                        val nativeEvent = keyEvent.nativeKeyEvent
                        if (keyEvent.key == Key.Backspace) {
                            onBackspace()
                            return@onPreviewKeyEvent true
                        } else if (keyEvent.key == Key.Spacebar) {
                            onPhysicalKey(' ')
                            return@onPreviewKeyEvent true
                        } else {
                            val unicodeChar = nativeEvent.getUnicodeChar(nativeEvent.metaState)
                            if (unicodeChar != 0) {
                                val char = unicodeChar.toChar()
                                if (char in 'a'..'z' || char in 'A'..'Z' || char in "0123456789.,;:'\"/\\`~!@#$%^&*()-=_+[]{}|<>?") {
                                    onPhysicalKey(char)
                                    return@onPreviewKeyEvent true
                                }
                            }
                        }
                    }
                    false
                }
                .testTag("typing_active_box"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (isBanglaBijoy) IndigoPrimary.copy(alpha = 0.8f) else CyanAccent.copy(alpha = 0.8f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (userInput.isEmpty()) {
                        Text(
                            text = if (isBanglaBijoy) "এক্সটার্নাল কিবোর্ড থেকে সরাসরি টাইপ করুন (J=ক, Shift+J=খ)..." else "টাইপিং শুরু করুন...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    } else {
                        // Display user's currently typed text
                        Text(
                            text = userInput,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                }

                // Blinking cursor dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (isBanglaBijoy) IndigoPrimary else CyanAccent)
                )
            }
        }

        // Optional Soft Keyboard BasicTextField (Only rendered if user explicitly enabled on-screen keyboard)
        if (useSoftwareKeyboard) {
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
                        .focusRequester(focusRequester),
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
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
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
