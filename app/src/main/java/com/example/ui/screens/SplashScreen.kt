package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanAccentLight
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Logo entrance animations
    val logoScale = remember { Animatable(0.35f) }
    val logoAlpha = remember { Animatable(0f) }

    // Text entrance animations
    val textAlpha = remember { Animatable(0f) }
    val textTranslationY = remember { Animatable(40f) }

    // Pulsing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    LaunchedEffect(Unit) {
        // Launch logo animation
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(500))
        }
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.6f,
                    stiffness = 250f
                )
            )
        }

        // Delay for text reveal
        delay(300L)
        launch {
            textAlpha.animateTo(1f, animationSpec = tween(600))
        }
        launch {
            textTranslationY.animateTo(0f, animationSpec = tween(600, easing = FastOutSlowInEasing))
        }

        // Keep splash visible for pleasant display (~1.9s total)
        delay(1600L)
        onSplashFinished()
    }

    // Rich multi-stop colorful gradient background
    val colorfulBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0A0F1D), // Deep obsidian
            Color(0xFF1E1B4B), // Rich indigo
            Color(0xFF2E1065), // Royal violet
            Color(0xFF0F766E), // Vibrant deep cyan/teal
            Color(0xFF022C22)  // Deep emerald forest
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorfulBackground)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Background ambient glow orbs
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(pulseScale)
                .alpha(glowAlpha * 0.4f)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(CyanAccent, VioletSecondary, Color.Transparent)
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // Animated Large App Logo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
            ) {
                // Outer pulsing ring
                Box(
                    modifier = Modifier
                        .size(145.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    CyanAccent.copy(alpha = glowAlpha),
                                    VioletSecondary.copy(alpha = glowAlpha),
                                    IndigoPrimary.copy(alpha = glowAlpha),
                                    CyanAccent.copy(alpha = glowAlpha)
                                )
                            )
                        )
                )

                // Inner premium container
                Box(
                    modifier = Modifier
                        .size(126.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF1E293B),
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                        .border(
                            width = 2.5.dp,
                            brush = Brush.linearGradient(
                                listOf(CyanAccentLight, VioletSecondary)
                            ),
                            shape = RoundedCornerShape(32.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // App logo from rasterized drawable
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_fg),
                        contentDescription = "Typing Master Logo",
                        modifier = Modifier
                            .size(116.dp)
                            .clip(RoundedCornerShape(26.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                // Decorative Lightning badge on logo corner
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(CyanAccent, Color(0xFF06B6D4)))
                        )
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Name & Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "টাইপিং মাস্টার",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 34.sp,
                    color = Color.White,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "বাংলা ও ইংরেজি টাইপিং ও গতি পরীক্ষা",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = CyanAccentLight
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Elegant animated loading indicator dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.alpha(textAlpha.value)
            ) {
                repeat(3) { index ->
                    val dotScale by infiniteTransition.animateFloat(
                        initialValue = 0.5f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = index * 180, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dotScale$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .scale(dotScale)
                            .clip(CircleShape)
                            .background(if (index == 1) VioletSecondary else CyanAccent)
                    )
                }
            }
        }

        // Bottom Brand / Version Note
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .alpha(textAlpha.value)
        ) {
            Text(
                text = "Brandify Creative • v1.0",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp
            )
        }
    }
}
