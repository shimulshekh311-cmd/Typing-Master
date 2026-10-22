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
import androidx.compose.foundation.layout.size
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
import com.example.data.bijoy.BijoyLayoutEngine
import com.example.data.datasource.LessonDataSource
import com.example.data.model.InputLayoutMode
import com.example.data.model.TypingLanguage
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary

@Composable
fun VirtualKeyboardHelper(
    targetChar: Char?,
    language: TypingLanguage,
    inputLayoutMode: InputLayoutMode = InputLayoutMode.BIJOY,
    modifier: Modifier = Modifier
) {
    val isBanglaBijoy = language == TypingLanguage.BANGLA && inputLayoutMode == InputLayoutMode.BIJOY

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Target character & Key Hint Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "পরবর্তী অক্ষর: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = CyanAccent.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent)
                    ) {
                        Text(
                            text = if (targetChar == null || targetChar == ' ') "Space (স্পেস)" else targetChar.toString(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                    }
                }

                if (targetChar != null) {
                    if (isBanglaBijoy) {
                        val bijoyHint = BijoyLayoutEngine.getBijoyKeyHint(targetChar)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AmberWarning.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberWarning.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "বিজয় কি: $bijoyHint",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AmberWarning
                            )
                        }
                    } else if (language == TypingLanguage.BANGLA) {
                        val avroHint = LessonDataSource.banglaKeyHints[targetChar]
                        if (avroHint != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldSuccess.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "অভ্র কি: $avroHint",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSuccess
                                )
                            }
                        }
                    }
                }
            }

            // Keyboard rows
            val row1 = listOf('Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P')
            val row2 = listOf('A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L')
            val row3 = listOf('Z', 'X', 'C', 'V', 'B', 'N', 'M')

            // Determine which physical key should glow for targetChar
            val activeKeyLetter: Char? = if (targetChar == null || targetChar == ' ') {
                null
            } else if (isBanglaBijoy) {
                // Find in unshiftedMap or shiftedMap
                val unshiftedMatch = BijoyLayoutEngine.unshiftedMap.entries.firstOrNull { it.value == targetChar.toString() }?.key
                val shiftedMatch = BijoyLayoutEngine.shiftedMap.entries.firstOrNull { it.value == targetChar.toString() }?.key
                (unshiftedMatch ?: shiftedMatch)?.uppercaseChar()
            } else {
                targetChar.uppercaseChar()
            }

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
                        BijoyKeyCap(
                            letter = key,
                            isBanglaBijoy = isBanglaBijoy,
                            isHighlighted = (key == activeKeyLetter),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Row 2
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(6.dp))
                    row2.forEach { key ->
                        val isHomeKey = (key == 'F' || key == 'J')
                        BijoyKeyCap(
                            letter = key,
                            isBanglaBijoy = isBanglaBijoy,
                            isHighlighted = (key == activeKeyLetter),
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
                        BijoyKeyCap(
                            letter = key,
                            isBanglaBijoy = isBanglaBijoy,
                            isHighlighted = (key == activeKeyLetter),
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
                            .height(28.dp)
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
private fun BijoyKeyCap(
    letter: Char,
    isBanglaBijoy: Boolean,
    isHighlighted: Boolean,
    isHomeKey: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isHighlighted -> CyanAccent
        isHomeKey -> IndigoPrimary.copy(alpha = 0.2f)
        else -> MaterialTheme.colorScheme.surface
    }

    val primaryTextColor = when {
        isHighlighted -> MaterialTheme.colorScheme.onPrimary
        isHomeKey -> IndigoPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }

    val keyInfo = BijoyLayoutEngine.keyGuideMap[letter]

    Box(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(
                width = if (isHighlighted) 1.5.dp else 1.dp,
                color = if (isHighlighted) CyanAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = RoundedCornerShape(6.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isBanglaBijoy && keyInfo != null) {
            Column(
                modifier = Modifier.padding(horizontal = 1.dp, vertical = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Shifted character & physical English key label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = keyInfo.shifted,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isHighlighted) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else IndigoPrimary
                    )
                    Text(
                        text = letter.toString(),
                        fontSize = 7.sp,
                        color = if (isHighlighted) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }

                // Main unshifted character
                Text(
                    text = keyInfo.normal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryTextColor,
                    lineHeight = 12.sp
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = letter.toString(),
                    fontSize = 12.sp,
                    fontWeight = if (isHighlighted || isHomeKey) FontWeight.Bold else FontWeight.Medium,
                    color = primaryTextColor
                )
                if (isHomeKey && !isHighlighted) {
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
}
