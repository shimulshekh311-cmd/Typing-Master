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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.TypingViewModel
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun AdBannerView(
    viewModel: TypingViewModel,
    modifier: Modifier = Modifier,
    onNavigateToSpeedTest: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val adStats by viewModel.adStats.collectAsStateWithLifecycle()

    var isDismissedForSession by remember { mutableStateOf(false) }
    var isAdMobLoaded by remember { mutableStateOf(false) }
    var showDiagnosisDialog by remember { mutableStateOf(false) }
    var adMobErrorDetails by remember { mutableStateOf<String?>(null) }
    var adMobErrorCode by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.recordBannerImpression()
    }

    if (adStats.isAdFree || isDismissedForSession) {
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Diagnosis modal when user clicks status or help icon
        if (showDiagnosisDialog) {
            AlertDialog(
                onDismissRequest = { showDiagnosisDialog = false },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "বিজ্ঞাপন স্ট্যাটাস ও তথ্য",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "কেনো এখনো ফোনে রিয়েল বিজ্ঞাপন দেখাচ্ছে না?",
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent,
                            fontSize = 13.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "১. অ্যাকাউন্ট রিভিউ ও ভেরিফিকেশন:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = AmberWarning
                                )
                                Text(
                                    text = "নতুন AdMob অ্যাকাউন্ট ও অ্যাড ইউনিট তৈরি করলে গুগল তা সক্রিয় করতে ২৪ থেকে ৪৮ ঘণ্টা সময় নেয়। এই সময়ে গুগল সার্ভার থেকে কোনো বিজ্ঞাপন পাঠানো হয় না (Error Code 3: No-Fill)।",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "২. আপনার বর্তমান AdMob কনফিগারেশন:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "• App ID: ca-app-pub-2983938252837230~4640265693\n• Banner ID: ${adStats.activeBannerId}\n• অবস্থা: ${adStats.lastAdStatus}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Test Mode Switcher row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = if (adStats.useGoogleTestUnits) EmeraldSuccess.copy(alpha = 0.15f) else IndigoPrimary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (adStats.useGoogleTestUnits) "গুগল অফিসিয়াল টেস্ট মোড চালু আছে" else "গুগল টেস্ট ব্যানার দিয়ে দেখতে চান?",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (adStats.useGoogleTestUnits) EmeraldSuccess else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (adStats.useGoogleTestUnits) "এখন গুগল টেস্ট বিজ্ঞাপন লোড হচ্ছে" else "চালু করলে সাথে সাথে গুগল টেস্ট অ্যাড দেখতে পাবেন",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = adStats.useGoogleTestUnits,
                                onCheckedChange = { isChecked ->
                                    viewModel.setUseGoogleTestUnits(isChecked)
                                    isAdMobLoaded = false
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = EmeraldSuccess,
                                    checkedTrackColor = EmeraldSuccess.copy(alpha = 0.4f)
                                )
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDiagnosisDialog = false }) {
                        Text("ঠিক আছে", color = CyanAccent, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // Real Google AdMob AdView Container (Exclusively AdMob)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("google_admob_banner_card"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = if (isAdMobLoaded) EmeraldSuccess.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Google AdMob AdView Box (Fixed standard banner height 50.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(),
                        factory = { ctx ->
                            AdView(ctx).apply {
                                setAdSize(AdSize.BANNER)
                                adUnitId = adStats.activeBannerId
                                adListener = object : AdListener() {
                                    override fun onAdLoaded() {
                                        isAdMobLoaded = true
                                        viewModel.recordBannerImpression()
                                        viewModel.setLastAdStatus("গুগল বিজ্ঞাপন সফলভাবে লোড হয়েছে (Ad Loaded)")
                                    }

                                    override fun onAdFailedToLoad(error: LoadAdError) {
                                        isAdMobLoaded = false
                                        adMobErrorCode = error.code
                                        adMobErrorDetails = error.message

                                        val msg = when (error.code) {
                                            3 -> "গুগল অ্যাকাউন্ট পর্যালোচনাধীন (Error 3: No-Fill) — ২৪-৪৮ ঘণ্টার মধ্যে লাইভ বিজ্ঞাপন আসবে"
                                            2 -> "নেটওয়ার্ক সমস্যা (Error 2: Network Error)"
                                            1 -> "অ্যাড ইউনিট কনফিগারেশন সঠিক নয় (Error 1: Invalid Request)"
                                            0 -> "অভ্যন্তরীণ ত্রুটি (Error 0: Internal Error)"
                                            else -> error.message
                                        }
                                        viewModel.setLastAdStatus(msg)
                                    }
                                }
                                loadAd(AdRequest.Builder().build())
                            }
                        },
                        update = { view ->
                            if (view.adUnitId != adStats.activeBannerId) {
                                view.adUnitId = adStats.activeBannerId
                                view.loadAd(AdRequest.Builder().build())
                            }
                        }
                    )

                    // Minimal AdMob placeholder while loading or reviewing
                    if (!isAdMobLoaded) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "Google AdMob",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = if (adStats.useGoogleTestUnits) "গুগল টেস্ট ব্যানার লোড হচ্ছে..." else "বিজ্ঞাপন লোড হচ্ছে...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = { showDiagnosisDialog = true },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "AdMob Status & Diagnosis",
                                        tint = AmberWarning,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { isDismissedForSession = true },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close Ad",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
