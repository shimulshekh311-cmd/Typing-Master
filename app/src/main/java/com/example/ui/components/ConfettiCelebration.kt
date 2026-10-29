package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

private data class Particle(
    val xRatio: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
    val swayAmplitude: Float
)

@Composable
fun ConfettiCelebration(
    modifier: Modifier = Modifier,
    durationMs: Int = 3000
) {
    val progress = remember { Animatable(0f) }

    val particles = remember {
        val colors = listOf(
            Color(0xFFFFD700), // Gold
            Color(0xFF06B6D4), // Cyan
            Color(0xFF8B5CF6), // Violet
            Color(0xFF10B981), // Emerald
            Color(0xFFEC4899), // Pink
            Color(0xFFF97316)  // Orange
        )
        List(40) {
            Particle(
                xRatio = Random.nextFloat(),
                speed = 0.5f + Random.nextFloat() * 0.9f,
                size = 8f + Random.nextFloat() * 12f,
                color = colors[Random.nextInt(colors.size)],
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                swayAmplitude = 20f + Random.nextFloat() * 30f
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = durationMs, easing = LinearEasing)
        )
    }

    if (progress.value < 1f) {
        val currentProgress = progress.value
        Canvas(modifier = modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            particles.forEach { p ->
                val y = currentProgress * canvasHeight * p.speed
                val sway = kotlin.math.sin((currentProgress * 6f + p.xRatio * 10f).toDouble()).toFloat() * p.swayAmplitude
                val x = (p.xRatio * canvasWidth) + sway
                val alpha = (1f - (currentProgress * 1.1f).coerceIn(0f, 1f)).coerceAtLeast(0f)

                rotate(
                    degrees = currentProgress * p.rotationSpeed,
                    pivot = Offset(x + p.size / 2, y + p.size / 2)
                ) {
                    drawRect(
                        color = p.color.copy(alpha = alpha),
                        topLeft = Offset(x, y),
                        size = Size(p.size, p.size * 0.6f)
                    )
                }
            }
        }
    }
}
