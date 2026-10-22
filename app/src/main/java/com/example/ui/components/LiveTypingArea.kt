package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Column(modifier = modifier.fillMaxWidth()) {
        // Real-time HUD (Heads-Up Display)
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

        // External Keyboard & Layout Mode Indicator Bar
        if (language == TypingLanguage.BANGLA) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable { onToggleLayoutMode() },
                shape = RoundedCornerShape(10.dp),
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
                            contentDescription = "Keyboard Mode",
                            tint = if (isBanglaBijoy) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBanglaBijoy) "⌨️ এক্সটার্নাল কিবোর্ড মোড: বিজয় লেআউট সক্রিয়" else "📱 সিস্টেম / অভ্র কিবোর্ড মোড",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isBanglaBijoy) IndigoPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "মোড পরিবর্তন ⇄",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Pristine Target Text Card (Rendered via AnnotatedString to prevent script breaking!)
        val onSurfaceColor = MaterialTheme.colorScheme.onSurface

        val annotatedText = buildAnnotatedString {
            val inputLen = userInput.length
            val targetLen = targetText.length

            for (i in 0 until targetLen) {
                val targetChar = targetText[i]
                when {
                    i < inputLen -> {
                        val isCorrect = userInput[i] == targetChar
                        if (isCorrect) {
                            withStyle(
                                SpanStyle(
                                    color = EmeraldSuccess,
                                    fontWeight = FontWeight.SemiBold
                                )
                            ) {
                                append(targetChar)
                            }
                        } else {
                            withStyle(
                                SpanStyle(
                                    color = RoseError,
                                    background = RoseError.copy(alpha = 0.25f),
                                    textDecoration = TextDecoration.Underline,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(targetChar)
                            }
                        }
                    }
                    i == inputLen -> {
                        // Current active cursor position
                        withStyle(
                            SpanStyle(
                                color = CyanAccent,
                                background = CyanAccent.copy(alpha = 0.3f),
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(targetChar)
                        }
                    }
                    else -> {
                        withStyle(
                            SpanStyle(
                                color = onSurfaceColor.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append(targetChar)
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clickable { focusRequester.requestFocus() }
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

        // Input Field that intercepts both Physical External Keyboard events and IME software input
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            BasicTextField(
                value = userInput,
                onValueChange = { newValue ->
                    // When in Bijoy physical keyboard mode, hardware keys are intercepted by onPreviewKeyEvent.
                    // But if the user is using on-screen keyboard, onValueChange handles it seamlessly!
                    onValueChange(newValue)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        1.5.dp,
                        if (isBanglaBijoy) IndigoPrimary.copy(alpha = 0.8f) else CyanAccent.copy(alpha = 0.6f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .focusRequester(focusRequester)
                    .onPreviewKeyEvent { keyEvent ->
                        if (keyEvent.type == KeyEventType.KeyDown) {
                            val nativeEvent = keyEvent.nativeKeyEvent
                            if (keyEvent.key == Key.Backspace) {
                                if (isBanglaBijoy) {
                                    onBackspace()
                                    return@onPreviewKeyEvent true
                                }
                            } else if (keyEvent.key == Key.Spacebar) {
                                if (isBanglaBijoy) {
                                    onPhysicalKey(' ')
                                    return@onPreviewKeyEvent true
                                }
                            } else {
                                val unicodeChar = nativeEvent.getUnicodeChar(nativeEvent.metaState)
                                if (unicodeChar != 0 && isBanglaBijoy) {
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
                    .testTag("typing_input_field"),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp
                ),
                cursorBrush = SolidColor(if (isBanglaBijoy) IndigoPrimary else CyanAccent),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    autoCorrectEnabled = false
                ),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (userInput.isEmpty()) {
                            Text(
                                text = if (isBanglaBijoy) "এক্সটার্নাল কিবোর্ড দিয়ে বিজয় টাইপিং শুরু করুন..." else "এখানে টাইপ শুরু করুন... (Type here)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                        innerTextField()
                    }
                }
            )
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
