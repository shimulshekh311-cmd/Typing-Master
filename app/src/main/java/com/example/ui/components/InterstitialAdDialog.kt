package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletSecondary
import kotlinx.coroutines.delay

@Composable
fun InterstitialAdDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var countdown by remember { mutableIntStateOf(3) }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000L)
            countdown--
        }
    }

    AlertDialog(
        onDismissRequest = {
            if (countdown <= 0) onDismiss()
        },
        modifier = modifier.testTag("interstitial_ad_dialog"),
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header Row with Ad badge and Skip / Countdown button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFFFB300)
                    ) {
                        Text(
                            text = "বিজ্ঞাপন (Sponsored Ad)",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (countdown > 0) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "স্কিপ: $countdown সে.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CyanAccent.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent),
                            modifier = Modifier.clickable { onDismiss() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "বিজ্ঞাপন বন্ধ করুন",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = CyanAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ad Creative Visual Box
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(IndigoPrimary, CyanAccent))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "১৯,০০০+ সদস্যের টাইপিং গ্রুপে যোগ দিন!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = CyanAccent
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "আমাদের ফেসবুক গ্রুপে দেশের ১৯,০০০ এর বেশি শিক্ষার্থী ও চাকরিপ্রার্থীরা একসাথে টাইপিং স্পিড বাড়াচ্ছে। আপনিও যোগ দিন সম্পূর্ণ বিনামূল্যে!",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Feature Highlights
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "✓ সাপ্তাহিক টাইপিং স্পিড প্রতিযোগিতা ও পুরষ্কার", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "✓ বিসিএস ও মন্ত্রণালয়ের টাইপিং পরীক্ষার সাজেশন", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "✓ বিজয় ও অভ্র শর্টকাট গাইড ও সমাধান", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"))
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        Toast.makeText(context, "ফেসবুক ওপেন করা যাচ্ছে না", Toast.LENGTH_SHORT).show()
                    }
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("interstitial_action_button"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
            ) {
                Icon(imageVector = Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("এখনই গ্রুপে যুক্ত হোন", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        dismissButton = {
            if (countdown <= 0) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("interstitial_skip_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ফলাফল দেখুন (স্কিপ করুন)")
                }
            }
        }
    )
}
