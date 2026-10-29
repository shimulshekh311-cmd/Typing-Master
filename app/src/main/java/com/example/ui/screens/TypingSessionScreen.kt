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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Vibration
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
                                if (inputLayoutMode == com.example.data.model.InputLayoutMode.BIJOY) "বাংলা (বিজয় লেআউট - এক্সটার্নাল কিবোর্ড)" else "বাংলা (অভ্র / সাধারণ কিবোর্ড)"
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
                    // Toggle Paper Copy Mode / Split Screen Mode
                    IconButton(
                        onClick = {
                            viewModel.togglePaperMode()
                        },
                        modifier = Modifier.testTag("toggle_paper_mode_action_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Article,
                            contentDescription = "Toggle Paper Mode",
                            tint = if (isPaperMode) CyanAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

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
                focusRequester = focusRequester,
                onValueChange = { viewModel.onUserInputChange(it) },
                onPhysicalKey = { char -> viewModel.handlePhysicalKey(char) },
                onBackspace = { viewModel.handleBackspace() },
                onToggleLayoutMode = {
                    viewModel.inputLayoutMode.value = if (inputLayoutMode == com.example.data.model.InputLayoutMode.BIJOY) {
                        com.example.data.model.InputLayoutMode.AVRO_SYSTEM
                    } else {
                        com.example.data.model.InputLayoutMode.BIJOY
                    }
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

    // Completion Results Dialog
    if (completedResult != null) {
        val res = completedResult!!
        val rank = when {
            res.wpm >= 50 && res.accuracy >= 95f -> "S"
            res.wpm >= 35 && res.accuracy >= 90f -> "A"
            res.wpm >= 20 && res.accuracy >= 80f -> "B"
            else -> "C"
        }
        val rankColor = when (rank) {
            "S" -> AmberWarning
            "A" -> CyanAccent
            "B" -> IndigoPrimary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
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
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Complete",
                        tint = AmberWarning,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "টাইপিং সম্পন্ন হয়েছে!",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Big Rank Circle
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(rankColor.copy(alpha = 0.15f))
                            .border(2.dp, rankColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = rank,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = rankColor
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
                                color = CyanAccent
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

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(text = "ভুল: ${res.mistakeCount}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "অক্ষর: ${res.totalCharsTyped}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "সময়: ${res.durationSeconds}s", style = MaterialTheme.typography.bodySmall)
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
                    Text("লিডারবোর্ডে দেখুন", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
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
                    Text("আবার চেষ্টা করুন")
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
