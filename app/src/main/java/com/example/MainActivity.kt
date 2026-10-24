package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.navigation.MainDestination
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MonthlyReportScreen
import com.example.ui.screens.ScoreboardScreen
import com.example.ui.screens.SpeedTestScreen
import com.example.ui.screens.TypingSessionScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TypingViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TypingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }

    override fun dispatchKeyEvent(event: android.view.KeyEvent): Boolean {
        if (event.action == android.view.KeyEvent.ACTION_DOWN) {
            if (viewModel.onPhysicalKeyEvent(event)) {
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }
}

enum class ScreenState {
    TABS,
    ACTIVE_SESSION
}

@Composable
fun MainAppContent(viewModel: TypingViewModel) {
    var screenState by remember { mutableStateOf(ScreenState.TABS) }
    var currentDestination by remember { mutableStateOf(MainDestination.LEARN) }

    if (screenState == ScreenState.ACTIVE_SESSION) {
        TypingSessionScreen(
            viewModel = viewModel,
            onBack = {
                screenState = ScreenState.TABS
            },
            onNavigateToScoreboard = {
                screenState = ScreenState.TABS
                currentDestination = MainDestination.SCOREBOARD
            }
        )
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.testTag("main_bottom_nav"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    MainDestination.entries.forEach { destination ->
                        val isSelected = currentDestination == destination
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentDestination = destination },
                            icon = {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.titleEn
                                )
                            },
                            label = {
                                Text(
                                    text = destination.titleBn,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = CyanAccent.copy(alpha = 0.2f),
                                selectedIconColor = CyanAccent,
                                selectedTextColor = CyanAccent
                            ),
                            modifier = Modifier.testTag("nav_item_${destination.route}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            AnimatedContent(
                targetState = currentDestination,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                label = "ScreenTransition"
            ) { target ->
                when (target) {
                    MainDestination.LEARN -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onStartLesson = { lesson ->
                                viewModel.startLesson(lesson)
                                screenState = ScreenState.ACTIVE_SESSION
                            },
                            onNavigateToSpeedTest = {
                                currentDestination = MainDestination.SPEED_TEST
                            }
                        )
                    }
                    MainDestination.SPEED_TEST -> {
                        SpeedTestScreen(
                            viewModel = viewModel,
                            onStartTest = {
                                screenState = ScreenState.ACTIVE_SESSION
                            }
                        )
                    }
                    MainDestination.SCOREBOARD -> {
                        ScoreboardScreen(viewModel = viewModel)
                    }
                    MainDestination.MONTHLY_REPORT -> {
                        MonthlyReportScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

