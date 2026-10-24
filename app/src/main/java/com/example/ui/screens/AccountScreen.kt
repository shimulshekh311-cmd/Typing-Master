package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.repository.AdManager
import com.example.data.repository.PaymentMethod
import com.example.data.repository.PaymentStatus
import com.example.data.repository.SubscriptionManager
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletSecondary
import com.example.ui.viewmodel.TypingViewModel
import java.net.URLEncoder
import java.util.Locale

@Composable
fun AccountScreen(
    viewModel: TypingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val subscriptionState by viewModel.subscriptionState.collectAsStateWithLifecycle()
    val adStats by viewModel.adStats.collectAsStateWithLifecycle()

    var showAdSettings by remember { mutableStateOf(false) }
    var showProDonation by remember { mutableStateOf(false) }
    var customBannerIdInput by remember { mutableStateOf(adStats.adMobBannerId) }
    var customInterstitialIdInput by remember { mutableStateOf(adStats.adMobInterstitialId) }

    // Pro / Donation form states
    var selectedMethod by remember { mutableStateOf(PaymentMethod.BKASH) }
    var senderPhone by remember { mutableStateOf("") }
    var trxId by remember { mutableStateOf("") }
    var smsInputText by remember { mutableStateOf("") }
    var showSmsParser by remember { mutableStateOf(false) }
    var showHistory by remember { mutableStateOf(false) }
    var detectedFeedback by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    fun processSmsText(text: String) {
        val parsed = viewModel.parseSmsOrClipboard(text)
        if (parsed.isRecognized) {
            parsed.trxId?.let { trxId = it }
            parsed.phone?.let { senderPhone = it }
            parsed.method?.let { selectedMethod = it }
            detectedFeedback = "✓ SMS থেকে শনাক্ত: TrxID: ${parsed.trxId ?: "N/A"}, নম্বর: ${parsed.phone ?: "N/A"}"
            errorMessage = null
        } else {
            detectedFeedback = "পাস করা লেখায় TrxID বা মোবাইল নম্বর পাওয়া যায়নি।"
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hub Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(CyanAccent, IndigoPrimary))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account Hub",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "অ্যাকাউন্ট ও কমিউনিটি হাব",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = CyanAccent
                        )
                        Text(
                            text = "১০০% ফ্রি টাইপিং • বিজ্ঞাপন ও কমিউনিটি",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (adStats.isAdFree) EmeraldSuccess.copy(alpha = 0.2f) else CyanAccent.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (adStats.isAdFree) EmeraldSuccess else CyanAccent)
                ) {
                    Text(
                        text = if (adStats.isAdFree) "বিজ্ঞাপন-মুক্ত প্রো" else "ফ্রি (বিজ্ঞাপন সমর্থিত)",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (adStats.isAdFree) EmeraldSuccess else CyanAccent
                    )
                }
            }
        }

        // Free Model & 100% Unlocked Status Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = EmeraldSuccess.copy(alpha = 0.12f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EmeraldSuccess),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "সকল পাঠ ও গতি পরীক্ষা সম্পূর্ণ ফ্রি!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ব্যবহারকারীদের কোনো ফি বা টাকা দিতে হবে না। অ্যাপে বিজ্ঞাপন প্রচারের মাধ্যমে আয় হচ্ছে এবং সবার জন্য শিক্ষা সম্পূর্ণ উন্মুক্ত রাখা হয়েছে।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Community Connect Card (19,000 Member Group, Facebook Page, WhatsApp Business)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = IndigoPrimary.copy(alpha = 0.12f)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "কমিউনিটি ও সোশ্যাল মিডিয়া হাব",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "আপনার ফেসবুক গ্রুপ, অফিশিয়াল পেজ এবং হোয়াটসঅ্যাপ বিজনেসে সরাসরি যুক্ত হয়ে ব্যবহারকারী বৃদ্ধি ও ইনকাম বাড়ান:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 19,000 Group Button
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"))
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "ফেসবুক ওপেন করা যাচ্ছে না", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("👥 ১৯,০০০ সদস্যের ফেসবুক গ্রুপে যুক্ত হোন", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Facebook Page Button
                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"))
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "ফেসবুক ওপেন করা যাচ্ছে না", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🌐 অফিশিয়াল ফেসবুক পেজ ফলো করুন", color = CyanAccent, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // WhatsApp Business Button
                    OutlinedButton(
                        onClick = {
                            val message = "আসসালামু আলাইকুম, আমি টাইピング মাস্টার অ্যাপের ব্যাপারে যোগাযোগ করতে চাই।"
                            try {
                                val encodedMsg = URLEncoder.encode(message, "UTF-8")
                                val uri = Uri.parse("https://wa.me/8801635586380?text=$encodedMsg")
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "WhatsApp ওপেন করা যাচ্ছে না (01635586380)", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("account_whatsapp_support_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.SupportAgent, contentDescription = "WhatsApp", tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("💬 WhatsApp বিজনেস (01635586380)", color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // AdMob Monetization & Earnings Dashboard
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "বিজ্ঞাপন ও আনুমানিক আয় ড্যাশবোর্ড",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(onClick = { showAdSettings = !showAdSettings }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Ad Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "মোট বিজ্ঞাপন ভিউ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "${adStats.totalImpressions} বার",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CyanAccent
                                )
                                Text(
                                    text = "ব্যানার: ${adStats.totalBannerImpressions} • ফুলস্ক্রিন: ${adStats.totalInterstitialImpressions}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "আনুমানিক আয় (AdMob)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = "৳ ${String.format(Locale.US, "%.2f", adStats.estimatedEarningsBdt)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldSuccess
                                )
                                Text(
                                    text = "eCPM ভিত্তিক প্রাক্কলন",
                                    fontSize = 10.sp,
                                    color = EmeraldSuccess
                                )
                            }
                        }
                    }

                    // Test Ad Toggle
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "বিজ্ঞাপন-মুক্ত মোড টেস্ট টগল", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    text = if (adStats.isAdFree) "বিজ্ঞাপন বর্তমানে বন্ধ রয়েছে" else "বিজ্ঞাপন সক্রিয় রয়েছে (টাকা আয় হচ্ছে)",
                                    fontSize = 11.sp,
                                    color = if (adStats.isAdFree) EmeraldSuccess else CyanAccent
                                )
                            }
                            Switch(
                                checked = adStats.isAdFree,
                                onCheckedChange = { viewModel.setAdFree(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = EmeraldSuccess)
                            )
                        }
                    }

                    // Configurable AdMob Unit IDs
                    AnimatedVisibility(visible = showAdSettings) {
                        Column(modifier = Modifier.padding(top = 14.dp)) {
                            Text(
                                text = "গুগল অ্যাডমব (AdMob) লাইভ কনফিগারেশন:",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldSuccess.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "লাইভ App ID: ${AdManager.ADMOB_APP_ID}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSuccess
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "আপনার তৈরি করা আসল ব্যানার ও ইন্টারস্টিশিয়াল বিজ্ঞাপন আইডি অ্যাপে সক্রিয় করা হয়েছে। প্রয়োজনে যেকোনো সময় পরিবর্তন করতে পারেন:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = customBannerIdInput,
                                onValueChange = { customBannerIdInput = it },
                                label = { Text("Banner Ad Unit ID") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = customInterstitialIdInput,
                                onValueChange = { customInterstitialIdInput = it },
                                label = { Text("Interstitial Ad Unit ID") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    viewModel.updateCustomAdMobIds(customBannerIdInput, customInterstitialIdInput)
                                    Toast.makeText(context, "অ্যাডমব আইডি সফলভাবে সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("আইডি সেভ করুন")
                            }
                        }
                    }
                }
            }
        }

        // Voluntary Support & Pro Ad-Free Option (bKash / Nagad 01984251178)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gemini_payment_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showProDonation = !showProDonation },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = null,
                                tint = VioletSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = "বিজ্ঞাপন বন্ধ করতে চান? (প্রো সাপোর্ট)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "ঐচ্ছিক বিকাশ/নগদ অনুদান দিয়ে বিজ্ঞাপন বন্ধ করুন",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = if (showProDonation) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }

                    AnimatedVisibility(visible = showProDonation) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${SubscriptionManager.BKASH_NAGAD_NUMBER} (Send Money)",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFFE53935)
                                        )
                                        Text(
                                            text = "বিকাশ বা নগদ পার্সোনাল নম্বর (ঐচ্ছিক ৪৫৳)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(SubscriptionManager.BKASH_NAGAD_NUMBER))
                                            Toast.makeText(context, "নম্বর ${SubscriptionManager.BKASH_NAGAD_NUMBER} কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.testTag("btn_copy_account_phone"),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("কপি", fontSize = 12.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Payment Method
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                PaymentMethod.entries.forEach { method ->
                                    val isSelected = selectedMethod == method
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { selectedMethod = method },
                                        color = if (isSelected) IndigoPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.5.dp,
                                            if (isSelected) CyanAccent else Color.Transparent
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = if (method == PaymentMethod.BKASH) "🌸 বিকাশ" else "🔶 নগদ",
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) CyanAccent else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = senderPhone,
                                onValueChange = {
                                    senderPhone = it.filter { c -> c.isDigit() }.take(11)
                                    errorMessage = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("sender_phone_input"),
                                label = { Text("প্রেরকের বিকাশ/নগদ নম্বর") },
                                placeholder = { Text("01XXXXXXXXX") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = trxId,
                                onValueChange = {
                                    trxId = it.uppercase()
                                    errorMessage = null
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("etTrxId"),
                                label = { Text("ট্রানজ্যাকশন আইডি (TrxID) লিখুন") },
                                placeholder = { Text("BL83K9M2P1") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            // Error/Success
                            if (errorMessage != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            if (successMessage != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = successMessage ?: "", color = EmeraldSuccess, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    val result = viewModel.activateSubscription(trxId, senderPhone, selectedMethod)
                                    if (result.first) {
                                        successMessage = "${result.second} (বিজ্ঞাপন সফলভাবে বন্ধ করা হয়েছে!)"
                                        errorMessage = null
                                        Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show()
                                    } else {
                                        errorMessage = result.second
                                        successMessage = null
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("btnSubmitPayment"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                            ) {
                                Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("কনফার্ম করুন ও বিজ্ঞাপন বন্ধ করুন", color = Color.White, fontWeight = FontWeight.Bold)
                            }

                            // SMS Parser
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { showSmsParser = !showSmsParser },
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "⚡ SMS অটো-ফিল ও রিডার টুল", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = CyanAccent)
                                    Icon(imageVector = if (showSmsParser) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }

                            AnimatedVisibility(visible = showSmsParser) {
                                Column(modifier = Modifier.padding(top = 8.dp)) {
                                    OutlinedTextField(
                                        value = smsInputText,
                                        onValueChange = {
                                            smsInputText = it
                                            processSmsText(it)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("sms_raw_input_field"),
                                        label = { Text("SMS টেক্সট পেস্ট করুন") },
                                        minLines = 2,
                                        maxLines = 3,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedButton(
                                        onClick = {
                                            val clip = clipboardManager.getText()?.text
                                            if (!clip.isNullOrBlank()) {
                                                smsInputText = clip
                                                processSmsText(clip)
                                                Toast.makeText(context, "ক্লিপবোর্ড থেকে SMS স্ক্যান সম্পন্ন!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("ক্লিপবোর্ড থেকে অটো-ফিল করুন", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
