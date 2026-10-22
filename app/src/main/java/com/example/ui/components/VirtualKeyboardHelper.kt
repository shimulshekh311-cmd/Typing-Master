package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.datasource.LessonDataSource
import com.example.data.model.TypingLanguage
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary

@Composable
fun VirtualKeyboardHelper(
    targetChar: Char?,
    language: TypingLanguage,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Target character indicator banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "পরবর্তী কি (Next Key): ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CyanAccent.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent)
                    ) {
                        Text(
                            text = if (targetChar == null || targetChar == ' ') "Space" else targetChar.toString(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                    }
                }

                if (language == TypingLanguage.BANGLA && targetChar != null) {
                    val hint = LessonDataSource.banglaKeyHints[targetChar]
                    if (hint != null) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldSuccess.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "অভ্র ফনেটিক কি: $hint",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                        }
                    }
                }
            }

            // Keyboard rows
            val row1 = listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P")
            val row2 = listOf("A", "S", "D", "F", "G", "H", "J", "K", "L")
            val row3 = listOf("Z", "X", "C", "V", "B", "N", "M")

            val upperTarget = targetChar?.uppercaseChar()?.toString() ?: ""

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Row 1
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row1.forEach { key ->
                        KeyCap(
                            text = key,
                            isHighlighted = (key == upperTarget),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Row 2 (Home Row with little side padding)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(6.dp))
                    row2.forEach { key ->
                        val isHomeKey = (key == "F" || key == "J")
                        KeyCap(
                            text = key,
                            isHighlighted = (key == upperTarget),
                            isHomeKey = isHomeKey,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                // Row 3
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    row3.forEach { key ->
                        KeyCap(
                            text = key,
                            isHighlighted = (key == upperTarget),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }

                // Space bar
                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.55f)
                            .height(26.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (targetChar == ' ') CyanAccent else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = 1.dp,
                                color = if (targetChar == ' ') CyanAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Spacebar",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (targetChar == ' ') FontWeight.Bold else FontWeight.Normal,
                            color = if (targetChar == ' ') MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyCap(
    text: String,
    isHighlighted: Boolean,
    isHomeKey: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isHighlighted -> CyanAccent
        isHomeKey -> IndigoPrimary.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surface
    }

    val textColor = when {
        isHighlighted -> MaterialTheme.colorScheme.onPrimary
        isHomeKey -> IndigoPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .height(30.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(
                width = if (isHighlighted) 1.5.dp else 1.dp,
                color = if (isHighlighted) CyanAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = if (isHighlighted || isHomeKey) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
            if (isHomeKey && !isHighlighted) {
                // Little tactile bump dot for F and J keys
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height(1.5.dp)
                        .background(IndigoPrimary.copy(alpha = 0.7f), RoundedCornerShape(1.dp))
                )
            }
        }
    }
}
