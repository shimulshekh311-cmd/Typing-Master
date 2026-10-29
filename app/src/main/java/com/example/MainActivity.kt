package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MonthlyReportScreen
import com.example.ui.screens.OnboardingWelcomeScreen
import com.example.ui.screens.ScoreboardScreen
import com.example.ui.screens.SpeedTestScreen
import com.example.ui.screens.TypingSessionScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.VioletSecondary
import com.example.ui.viewmodel.TypingViewModel
import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {

    private val viewModel: TypingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Safely initialize Firebase App
        com.example.data.repository.FirebaseHelper.ensureInitialized(applicationContext)

        // Initialize Google Mobile Ads SDK safely
        try {
            MobileAds.initialize(applicationContext) {}
        } catch (_: Throwable) {}

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
    var showOnboarding by rememberSaveable { mutableStateOf(true) }
    var screenState by remember { mutableStateOf(ScreenState.TABS) }
    var currentDestination by remember { mutableStateOf(MainDestination.LEARN) }

    if (showOnboarding) {
        BackHandler {
            showOnboarding = false
        }
        OnboardingWelcomeScreen(
            viewModel = viewModel,
            onDismissOrContinue = {
                showOnboarding = false
            }
        )
    } else if (screenState == ScreenState.ACTIVE_SESSION) {
        TypingSessionScreen(
            viewModel = viewModel,
            onBack = {
                screenState = ScreenState.TABS
            },
            onNavigateToScoreboard = {
                screenState = ScreenState.TABS
                currentDestination = MainDestination.LEADERBOARD
            }
        )
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .testTag("main_bottom_nav"),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(
                                CyanAccent.copy(alpha = 0.35f),
                                VioletSecondary.copy(alpha = 0.2f),
                                CyanAccent.copy(alpha = 0.35f)
                            )
                        )
                    ),
                    tonalElevation = 10.dp
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp
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
                                    indicatorColor = CyanAccent.copy(alpha = 0.22f),
                                    selectedIconColor = CyanAccent,
                                    selectedTextColor = CyanAccent,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier.testTag("nav_item_${destination.route}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            AnimatedContent(
                targetState = currentDestination,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(280)) + slideInHorizontally { width -> width / 6 }) togetherWith
                    fadeOut(animationSpec = tween(200))
                },
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
                    MainDestination.LEADERBOARD -> {
                        ScoreboardScreen(
                            viewModel = viewModel,
                            onNavigateToAccount = {
                                currentDestination = MainDestination.ACCOUNT
                            }
                        )
                    }
                    MainDestination.MONTHLY_REPORT -> {
                        MonthlyReportScreen(viewModel = viewModel)
                    }
                    MainDestination.ACCOUNT -> {
                        AccountScreen(
                            viewModel = viewModel,
                            onShowOnboarding = {
                                showOnboarding = true
                            }
                        )
                    }
                }
            }
        }
    }
}

