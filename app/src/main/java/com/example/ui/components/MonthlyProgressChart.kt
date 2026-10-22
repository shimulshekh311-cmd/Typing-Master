package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MonthlyDayStat
import com.example.data.model.MonthlyReportSummary
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletSecondary
import java.util.Locale

@Composable
fun MonthlySpeedChart(
    dailyStats: List<MonthlyDayStat>,
    modifier: Modifier = Modifier
) {
    var selectedDay by remember { mutableStateOf<MonthlyDayStat?>(null) }

    val daysWithData = dailyStats.filter { it.sessionCount > 0 }
    val maxWpm = dailyStats.maxOfOrNull { it.peakWpm }?.coerceAtLeast(60) ?: 60
    val avgWpm = if (daysWithData.isNotEmpty()) daysWithData.map { it.avgWpm }.average().toFloat() else 0f

    val surfaceColor = MaterialTheme.colorScheme.surface

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("monthly_speed_chart_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyanAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Speed Chart",
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "গতি বৃদ্ধির চার্ট (Speed Progression)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "দিনভিত্তিক গড় ও সর্বোচ্চ WPM গ্রাফ",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (avgWpm > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = IndigoPrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "গড়: ${String.format(Locale.US, "%.1f", avgWpm)} WPM",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = IndigoPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tooltip banner if a day is clicked
            AnimatedVisibility(visible = selectedDay != null) {
                selectedDay?.let { day ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "তারিখ: ${day.dayOfMonth} তারিখ",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "গড়: ${String.format(Locale.US, "%.1f", day.avgWpm)} WPM",
                                    color = CyanAccent,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "শীর্ষ: ${day.peakWpm} WPM",
                                    color = IndigoPrimary,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "সেশন: ${day.sessionCount}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // Interactive Canvas Line & Area Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                        .pointerInput(dailyStats) {
                            detectTapGestures { tapOffset ->
                                if (dailyStats.isNotEmpty()) {
                                    val dayIndex = ((tapOffset.x / size.width) * dailyStats.size).toInt()
                                        .coerceIn(0, dailyStats.size - 1)
                                    val stat = dailyStats[dayIndex]
                                    selectedDay = if (stat.sessionCount > 0) stat else null
                                }
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height
                    val bottomPadding = 30f
                    val topPadding = 20f
                    val chartHeight = h - bottomPadding - topPadding

                    // Draw subtle grid lines (0, 30, 60, maxWpm)
                    val gridLines = 4
                    for (i in 0..gridLines) {
                        val y = topPadding + (chartHeight / gridLines) * i
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.2f),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    if (dailyStats.isEmpty()) return@Canvas

                    val stepX = w / (dailyStats.size - 1).coerceAtLeast(1)
                    val points = mutableListOf<Offset>()

                    dailyStats.forEachIndexed { index, stat ->
                        val x = index * stepX
                        val normalizedY = if (maxWpm > 0) (stat.avgWpm / maxWpm.toFloat()) else 0f
                        val y = (topPadding + chartHeight) - (normalizedY * chartHeight)
                        points.add(Offset(x, y))
                    }

                    // Draw gradient area under the curve
                    val areaPath = Path().apply {
                        moveTo(0f, topPadding + chartHeight)
                        points.forEach { pt ->
                            lineTo(pt.x, pt.y)
                        }
                        lineTo(w, topPadding + chartHeight)
                        close()
                    }

                    drawPath(
                        path = areaPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                CyanAccent.copy(alpha = 0.35f),
                                CyanAccent.copy(alpha = 0.05f)
                            ),
                            startY = topPadding,
                            endY = topPadding + chartHeight
                        )
                    )

                    // Draw main line path
                    val linePath = Path().apply {
                        points.forEachIndexed { idx, pt ->
                            if (idx == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
                        }
                    }

                    drawPath(
                        path = linePath,
                        brush = Brush.horizontalGradient(
                            colors = listOf(IndigoPrimary, CyanAccent, VioletSecondary)
                        ),
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw average dashed line if avgWpm > 0
                    if (avgWpm > 0) {
                        val avgY = (topPadding + chartHeight) - ((avgWpm / maxWpm.toFloat()) * chartHeight)
                        drawLine(
                            color = AmberWarning.copy(alpha = 0.7f),
                            start = Offset(0f, avgY),
                            end = Offset(w, avgY),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                    }

                    // Draw points on days with sessions
                    dailyStats.forEachIndexed { idx, stat ->
                        if (stat.sessionCount > 0) {
                            val pt = points[idx]
                            drawCircle(
                                color = surfaceColor,
                                radius = 5.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = CyanAccent,
                                radius = 3.5.dp.toPx(),
                                center = pt
                            )
                        }
                    }
                }
            }

            // X-Axis day labels (1, 5, 10, 15, 20, 25, End)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(1, 5, 10, 15, 20, 25, dailyStats.size).forEach { dayNum ->
                    Text(
                        text = "$dayNum",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun MonthlyAccuracyChart(
    dailyStats: List<MonthlyDayStat>,
    modifier: Modifier = Modifier
) {
    val activeDays = dailyStats.filter { it.sessionCount > 0 }
    val avgAccuracy = if (activeDays.isNotEmpty()) activeDays.map { it.avgAccuracy }.average().toFloat() else 0f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("monthly_accuracy_chart_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldSuccess.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Accuracy Chart",
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "নির্ভুলতা অগ্রগতি (Accuracy Trend)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "শতকরা নির্ভুল টাইপিংয়ের ধারাবাহিকতা (%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldSuccess.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${String.format(Locale.US, "%.1f", avgAccuracy)}%",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = EmeraldSuccess,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Accuracy Bar & Curve
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height
                    val bottomPadding = 20f
                    val topPadding = 15f
                    val chartHeight = h - bottomPadding - topPadding

                    // 100% and 90% guide lines
                    val line100Y = topPadding
                    val line90Y = topPadding + chartHeight * 0.5f

                    drawLine(
                        color = EmeraldSuccess.copy(alpha = 0.3f),
                        start = Offset(0f, line100Y),
                        end = Offset(w, line100Y),
                        strokeWidth = 1.dp.toPx()
                    )

                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.2f),
                        start = Offset(0f, line90Y),
                        end = Offset(w, line90Y),
                        strokeWidth = 1.dp.toPx()
                    )

                    if (dailyStats.isEmpty()) return@Canvas

                    val barWidth = (w / dailyStats.size) * 0.65f
                    val slotWidth = w / dailyStats.size

                    dailyStats.forEachIndexed { index, stat ->
                        val x = index * slotWidth + (slotWidth - barWidth) / 2
                        if (stat.sessionCount > 0) {
                            // Scale 80% to 100% into height
                            val normalized = ((stat.avgAccuracy - 75f) / 25f).coerceIn(0.1f, 1f)
                            val barH = normalized * chartHeight
                            val topY = (topPadding + chartHeight) - barH

                            drawRoundRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(EmeraldSuccess, EmeraldSuccess.copy(alpha = 0.6f))
                                ),
                                topLeft = Offset(x, topY),
                                size = androidx.compose.ui.geometry.Size(barWidth, barH),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
                            )
                        } else {
                            // Empty day indicator
                            drawCircle(
                                color = Color.Gray.copy(alpha = 0.2f),
                                radius = 2.dp.toPx(),
                                center = Offset(x + barWidth / 2, topPadding + chartHeight)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "লক্ষ্যমাত্রা: ৯০%+",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "সর্বোচ্চ নির্ভুলতা: ১০০%",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldSuccess,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MonthlySummaryCards(
    summary: MonthlyReportSummary,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "গড় গতি",
                subTitle = "Average Speed",
                value = "${String.format(Locale.US, "%.1f", summary.averageWpm)}",
                unit = "WPM",
                color = CyanAccent,
                icon = Icons.Default.Speed,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "শীর্ষ গতি",
                subTitle = "Peak Speed",
                value = "${summary.highestWpm}",
                unit = "WPM",
                color = IndigoPrimary,
                icon = Icons.Default.TrendingUp,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "গড় নির্ভুলতা",
                subTitle = "Accuracy Avg",
                value = "${String.format(Locale.US, "%.1f", summary.averageAccuracy)}",
                unit = "%",
                color = EmeraldSuccess,
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "মোট পরীক্ষা",
                subTitle = "Completed Tests",
                value = "${summary.totalSessions}",
                unit = "বার",
                color = VioletSecondary,
                icon = Icons.Default.Star,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    subTitle: String,
    value: String,
    unit: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                text = subTitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}
