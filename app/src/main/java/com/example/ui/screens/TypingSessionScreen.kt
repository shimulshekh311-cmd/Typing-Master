package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.TypingLanguage
import com.example.ui.components.InterstitialAdDialog
import com.example.ui.components.LiveTypingDisplay
import com.example.ui.components.VirtualKeyboardHelper
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseError
import com.example.ui.theme.VioletSecondary
import com.example.ui.viewmodel.TypingViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypingSessionScreen(
    viewModel: TypingViewModel,
    onBack: () -> Unit,
    onNavigateToScoreboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val targetText by viewModel.targetText.collectAsStateWithLifecycle()
    val sessionTitle by viewModel.sessionTitle.collectAsStateWithLifecycle()
    val userInput by viewModel.userInput.collectAsStateWithLifecycle()
    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsStateWithLifecycle()
    val timeLimitSeconds by viewModel.timeLimitSeconds.collectAsStateWithLifecycle()
    val liveWpm by viewModel.liveWpm.collectAsStateWithLifecycle()
    val liveAccuracy by viewModel.liveAccuracy.collectAsStateWithLifecycle()
    val mistakeCount by viewModel.mistakeCount.collectAsStateWithLifecycle()
    val correctWordsCount by viewModel.correctWordsCount.collectAsStateWithLifecycle()
    val incorrectWordsCount by viewModel.incorrectWordsCount.collectAsStateWithLifecycle()
    val liveErrorRatePercent by viewModel.liveErrorRatePercent.collectAsStateWithLifecycle()
    val isLiveFailingGovtRule by viewModel.isLiveFailingGovtRule.collectAsStateWithLifecycle()
    val completedResult by viewModel.lastCompletedResult.collectAsStateWithLifecycle()
    val showInterstitialAd by viewModel.showInterstitialAd.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val inputLayoutMode by viewModel.inputLayoutMode.collectAsStateWithLifecycle()
    val isPaperMode by viewModel.isPaperMode.collectAsStateWithLifecycle()
    val targetScrollWpm by viewModel.targetScrollWpm.collectAsStateWithLifecycle()

    val virtualKeyboardEnabled by viewModel.virtualKeyboardEnabled.collectAsStateWithLifecycle()
    val hapticEnabled by viewModel.hapticEnabled.collectAsStateWithLifecycle()

    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()

    BackHandler {
        onBack()
    }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(150)
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    val targetChar = if (userInput.length < targetText.length) {
        targetText[userInput.length]
    } else {
        null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isPaperMode) "কাগজ দেখে টাইপিং" else sessionTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = if (selectedLanguage == TypingLanguage.BANGLA) {
                                "বাংলা (বিজয় লেআউট)"
                            } else {
                                "English Typing Session"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selectedLanguage == TypingLanguage.BANGLA) VioletSecondary else CyanAccent
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("session_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Toggle Virtual Keyboard
                    IconButton(
                        onClick = {
                            viewModel.virtualKeyboardEnabled.value = !virtualKeyboardEnabled
                        },
                        modifier = Modifier.testTag("toggle_keyboard_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = "Toggle Keyboard",
                            tint = if (virtualKeyboardEnabled) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Toggle Haptic
                    IconButton(
                        onClick = {
                            viewModel.hapticEnabled.value = !hapticEnabled
                        },
                        modifier = Modifier.testTag("toggle_haptic_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Toggle Haptic",
                            tint = if (hapticEnabled) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live Typing HUD & Interactive Display
            LiveTypingDisplay(
                targetText = targetText,
                userInput = userInput,
                liveWpm = liveWpm,
                liveAccuracy = liveAccuracy,
                elapsedSeconds = elapsedSeconds,
                timeLimitSeconds = timeLimitSeconds,
                mistakeCount = mistakeCount,
                language = selectedLanguage,
                inputLayoutMode = inputLayoutMode,
                isPaperMode = isPaperMode,
                isTimerRunning = isTimerRunning,
                targetScrollWpm = targetScrollWpm,
                onSetTargetScrollWpm = { viewModel.setTargetScrollWpm(it) },
                onTogglePaperMode = { viewModel.togglePaperMode() },
                correctWordsCount = correctWordsCount,
                incorrectWordsCount = incorrectWordsCount,
                liveErrorRatePercent = liveErrorRatePercent,
                isLiveFailingGovtRule = isLiveFailingGovtRule,
                focusRequester = focusRequester,
                onValueChange = { viewModel.onUserInputChange(it) },
                onPhysicalKey = { char -> viewModel.handlePhysicalKey(char) },
                onBackspace = { viewModel.handleBackspace() },
                onToggleLayoutMode = {
                    // Always Bijoy layout
                }
            )

            // Optional Virtual Keyboard & Finger Guide (only when enabled and not in paper mode)
            AnimatedVisibility(visible = virtualKeyboardEnabled && !isPaperMode) {
                VirtualKeyboardHelper(
                    targetChar = targetChar,
                    language = selectedLanguage,
                    inputLayoutMode = inputLayoutMode
                )
            }

            // Action Buttons (Restart, Finish Early)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        viewModel.resetSession()
                        try {
                            focusRequester.requestFocus()
                        } catch (_: Exception) {}
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("session_restart_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("পুনরায় শুরু")
                }

                Button(
                    onClick = { viewModel.finishSession() },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("session_finish_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Finish",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("পরীক্ষা শেষ করুন", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // Completion Results Dialog & Confetti Celebration
    if (completedResult != null) {
        val res = completedResult!!
        val isPassedGovt = res.isPassedGovtRule
        val isErrorTooHigh = res.errorRatePercent > 5.0f
        val isSpeedTooLow = res.wpm < 20

        if (isPassedGovt) {
            com.example.ui.components.ConfettiCelebration()
        }

        val statusColor = when {
            isErrorTooHigh -> RoseError
            isSpeedTooLow -> AmberWarning
            else -> EmeraldSuccess
        }

        val statusTitle = when {
            isErrorTooHigh -> "ফেল (অকৃতকার্য)"
            isSpeedTooLow -> "অকৃতকার্য (গতি অপর্যাপ্ত)"
            else -> "কৃতকার্য (PASSED)"
        }

        val statusIcon = when {
            isErrorTooHigh -> Icons.Default.Cancel
            isSpeedTooLow -> Icons.Default.Warning
            else -> Icons.Default.CheckCircle
        }

        val statusDescription = when {
            isErrorTooHigh -> "ভুলের হার ৫%-এর বেশি (${String.format(Locale.US, "%.1f", res.errorRatePercent)}%) — ২০২৪ সালের নিয়োগবিধির সংশোধিত গেজেট অনুযায়ী গতি অর্জিত হয়নি (ফেল)।"
            isSpeedTooLow -> "ভুলের হার ৫% বা কম (${String.format(Locale.US, "%.1f", res.errorRatePercent)}%), কিন্তু পরীক্ষার জন্য প্রয়োজনীয় ন্যূনতম গতি (২০ WPM) অর্জিত হয়নি।"
            else -> "অভিনন্দন! আপনি সরকারি নিয়োগ পরীক্ষার ৫% ভুলের নিয়ম এবং গতি সফলভাবে উত্তীর্ণ হয়েছেন।"
        }

        AlertDialog(
            onDismissRequest = { viewModel.dismissResultDialog() },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = statusTitle,
                        tint = statusColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusTitle,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge,
                        color = statusColor
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Government Assessment Verdict Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.12f)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, statusColor.copy(alpha = 0.8f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isErrorTooHigh) "❌ ৫% ভুলের নিয়মে ফেল" else (if (isSpeedTooLow) "⚠️ গতি কাঙ্ক্ষিত সীমার নিচে" else "✅ সরকারি নিয়োগ পরীক্ষায় উত্তীর্ণ"),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = statusDescription,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Speed & Accuracy Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "গতি (Speed)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${res.wpm} WPM",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (res.wpm >= 20) CyanAccent else RoseError
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "নির্ভুলতা (Accuracy)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.1f", res.accuracy)}%",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Detailed breakdown card (5 Keystrokes = 1 Word, Error Rate %, Total/Correct/Incorrect Words)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "সরকারি নিয়মে বিশ্লেষণ:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            // Keystroke count: 5 keystrokes = 1 word
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "মোট কী-স্ট্রোক:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${res.totalCharsTyped} টি (${res.totalCharsTyped / 5} শব্দ)",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent
                                )
                            }

                            // Correct vs Incorrect words
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "সঠিক / ভুল শব্দ:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row {
                                    Text(
                                        text = "${res.correctWords} সঠিক",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSuccess
                                    )
                                    Text(
                                        text = "  |  ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${res.incorrectWords} ভুল",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (res.incorrectWords > 0) RoseError else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Error percentage vs Allowed 5% threshold
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "ভুলের শতকরা হার:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${String.format(Locale.US, "%.1f", res.errorRatePercent)}% (অনুমোদিত সর্বোচ্চ ৫%)",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (res.errorRatePercent > 5.0f) RoseError else EmeraldSuccess
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "সময়কাল:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${res.durationSeconds} সেকেন্ড",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2024 Gazette Reference Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(16.dp).padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "২০২৪ সালের নিয়োগবিধির সংশোধিত গেজেট: ৫টি কী-স্ট্রোক = ১টি শব্দ (২০ WPM = মিনিটে ১০০ কী-স্ট্রোক)। ৫%-এর বেশি ভুল হলে স্বয়ংক্রিয়ভাবে ফেল গণ্য হবে।",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissResultDialog()
                        onNavigateToScoreboard()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                    modifier = Modifier.testTag("result_dialog_view_scoreboard_button")
                ) {
                    Icon(imageVector = Icons.Default.Leaderboard, contentDescription = "Leaderboard", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("রেকর্ড দেখুন", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        viewModel.dismissResultDialog()
                        viewModel.resetSession()
                        try {
                            focusRequester.requestFocus()
                        } catch (_: Exception) {}
                    },
                    modifier = Modifier.testTag("result_dialog_try_again_button")
                ) {
                    Text("আবার পরীক্ষা দিন")
                }
            }
        )
    }

    if (showInterstitialAd) {
        InterstitialAdDialog(
            onDismiss = {
                viewModel.dismissInterstitialAd()
            }
        )
    }
}
