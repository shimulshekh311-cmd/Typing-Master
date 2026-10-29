package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.LeaderboardEntry
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletSecondary

@Composable
fun LeaderboardPodiumView(
    entries: List<LeaderboardEntry>,
    modifier: Modifier = Modifier
) {
    if (entries.isEmpty()) return

    val first = entries.getOrNull(0)
    val second = entries.getOrNull(1)
    val third = entries.getOrNull(2)

    val infiniteTransition = rememberInfiniteTransition(label = "TrophyGlow")
    val crownScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CrownScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("leaderboard_podium_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A).copy(alpha = 0.8f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(
                    AmberWarning.copy(alpha = 0.5f),
                    Color(0xFF1E293B)
                )
            ),
            width = 1.5.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp, start = 8.dp, end = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Trophy",
                    tint = AmberWarning,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "শীর্ষ ৩ চ্যাম্পিয়ন পোডিয়াম",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3-Podium Columns: 2nd (Left), 1st (Center), 3rd (Right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // 2nd Place (Silver)
                if (second != null) {
                    PodiumColumn(
                        entry = second,
                        rank = 2,
                        pedestalHeight = 72.dp,
                        trophyColor = Color(0xFFC0C0C0),
                        podiumGradient = listOf(Color(0xFF475569), Color(0xFF1E293B)),
                        medalLabel = "🥈 2nd",
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                // 1st Place (Gold Champion)
                if (first != null) {
                    PodiumColumn(
                        entry = first,
                        rank = 1,
                        pedestalHeight = 100.dp,
                        trophyColor = Color(0xFFFFD700),
                        podiumGradient = listOf(Color(0xFFF59E0B), Color(0xFFB45309)),
                        medalLabel = "🥇 1st",
                        avatarScale = crownScale,
                        modifier = Modifier.weight(1.15f)
                    )
                }

                // 3rd Place (Bronze)
                if (third != null) {
                    PodiumColumn(
                        entry = third,
                        rank = 3,
                        pedestalHeight = 56.dp,
                        trophyColor = Color(0xFFCD7F32),
                        podiumGradient = listOf(Color(0xFF78350F), Color(0xFF1E293B)),
                        medalLabel = "🥉 3rd",
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PodiumColumn(
    entry: LeaderboardEntry,
    rank: Int,
    pedestalHeight: androidx.compose.ui.unit.Dp,
    trophyColor: Color,
    podiumGradient: List<Color>,
    medalLabel: String,
    avatarScale: Float = 1f,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Avatar + Badge
        Box(
            modifier = Modifier
                .scale(avatarScale)
                .size(if (rank == 1) 58.dp else 46.dp),
            contentAlignment = Alignment.Center
        ) {
            if (entry.photoUrl.isNotBlank()) {
                AsyncImage(
                    model = entry.photoUrl,
                    contentDescription = entry.displayName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .border(2.5.dp, trophyColor, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                val initial = entry.displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(CircleShape)
                        .background(trophyColor.copy(alpha = 0.25f))
                        .border(2.5.dp, trophyColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = if (rank == 1) 22.sp else 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Medal Tag
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = trophyColor.copy(alpha = 0.2f),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = Brush.horizontalGradient(listOf(trophyColor, trophyColor.copy(alpha = 0.5f))),
                width = 1.dp
            )
        ) {
            Text(
                text = medalLabel,
                color = trophyColor,
                fontWeight = FontWeight.Bold,
                fontSize = if (rank == 1) 12.sp else 10.sp,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Name
        Text(
            text = entry.displayName,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1,
            textAlign = TextAlign.Center
        )

        // Speed WPM
        Text(
            text = "${entry.highestWpm} WPM",
            fontWeight = FontWeight.Black,
            fontSize = if (rank == 1) 14.sp else 12.sp,
            color = trophyColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 3D Podium Column
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(pedestalHeight),
            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .background(Brush.verticalGradient(podiumGradient))
                    .border(
                        1.dp,
                        trophyColor.copy(alpha = 0.4f),
                        RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    fontWeight = FontWeight.Black,
                    fontSize = if (rank == 1) 26.sp else 20.sp,
                    color = Color.White.copy(alpha = 0.4f)
                )
            }
        }
    }
}
