package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseError
import com.example.ui.theme.VioletSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpeedometerGaugeView(
    liveWpm: Int,
    liveAccuracy: Float,
    streakCount: Int = 0,
    modifier: Modifier = Modifier
) {
    // Dynamic color tier based on typing speed
    val targetColor = when {
        liveWpm >= 80 -> Color(0xFFEC4899) // Godspeed Neon Pink
        liveWpm >= 60 -> AmberWarning       // High Speed Amber/Gold
        liveWpm >= 40 -> EmeraldSuccess     // Fast Emerald Green
        liveWpm >= 20 -> CyanAccent         // Moderate Cyan
        else -> IndigoPrimary               // Warm-up Indigo
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(400),
        label = "GaugeColor"
    )

    // Gauge sweep angle: 0 WPM = 0°, 120+ WPM = 240°
    val maxDisplayWpm = 120f
    val normalizedWpm = (liveWpm / maxDisplayWpm).coerceIn(0f, 1f)
    val animatedSweep by animateFloatAsState(
        targetValue = normalizedWpm * 240f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "NeedleSweep"
    )

    // Pulsing streak flame animation
    val infiniteTransition = rememberInfiniteTransition(label = "StreakPulse")
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FlameScale"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A).copy(alpha = 0.85f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(
                    animatedColor.copy(alpha = 0.45f),
                    VioletSecondary.copy(alpha = 0.25f),
                    animatedColor.copy(alpha = 0.45f)
                )
            ),
            width = 1.5.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Speed Gauge Canvas with Center WPM Counter
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(140.dp)) {
                    val strokeWidth = 12.dp.toPx()
                    val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                    val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                    // Background Track (240 degree arc starting from 150°)
                    drawArc(
                        color = Color(0xFF1E293B),
                        startAngle = 150f,
                        sweepAngle = 240f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Active Glowing Speed Progress Arc
                    if (animatedSweep > 0.5f) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                0.0f to CyanAccent,
                                0.5f to EmeraldSuccess,
                                0.8f to AmberWarning,
                                1.0f to Color(0xFFEC4899)
                            ),
                            startAngle = 150f,
                            sweepAngle = animatedSweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    // Indicator needle tip dot
                    val currentAngleRad = Math.toRadians((150f + animatedSweep).toDouble())
                    val radius = (size.width - strokeWidth) / 2
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    val tipX = centerX + (radius * cos(currentAngleRad)).toFloat()
                    val tipY = centerY + (radius * sin(currentAngleRad)).toFloat()

                    drawCircle(
                        color = Color.White,
                        radius = 6.dp.toPx(),
                        center = Offset(tipX, tipY)
                    )
                    drawCircle(
                        color = animatedColor,
                        radius = 3.5.dp.toPx(),
                        center = Offset(tipX, tipY)
                    )
                }

                // Digital Speedometer Display inside circle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$liveWpm",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        lineHeight = 36.sp
                    )
                    Text(
                        text = "WPM",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = animatedColor,
                        letterSpacing = 1.2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Speed Tier & Accuracy & Streak Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speed Tier Pill
                val tierText = when {
                    liveWpm >= 80 -> "🔥 GODSPEED"
                    liveWpm >= 60 -> "⚡ ULTRA FAST"
                    liveWpm >= 40 -> "🚀 FAST"
                    liveWpm >= 20 -> "👍 STEADY"
                    else -> "⌨️ READY"
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = animatedColor.copy(alpha = 0.15f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(animatedColor, animatedColor.copy(alpha = 0.6f))),
                        width = 1.dp
                    )
                ) {
                    Text(
                        text = tierText,
                        color = animatedColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Combo Streak Pill (if any)
                if (streakCount >= 5) {
                    val streakMultiplier = when {
                        streakCount >= 40 -> "3.5x"
                        streakCount >= 25 -> "2.5x"
                        streakCount >= 15 -> "2.0x"
                        else -> "1.5x"
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .scale(flameScale)
                            .background(
                                color = AmberWarning.copy(alpha = 0.18f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(1.dp, AmberWarning.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak Flame",
                            tint = AmberWarning,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "STREAK $streakCount ($streakMultiplier)",
                            color = AmberWarning,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp
                        )
                    }
                }

                // Live Accuracy Indicator
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = EmeraldSuccess.copy(alpha = 0.15f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(listOf(EmeraldSuccess, EmeraldSuccess.copy(alpha = 0.5f))),
                        width = 1.dp
                    )
                ) {
                    Text(
                        text = "%.0f%% ACCURACY".format(liveAccuracy),
                        color = EmeraldSuccess,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
