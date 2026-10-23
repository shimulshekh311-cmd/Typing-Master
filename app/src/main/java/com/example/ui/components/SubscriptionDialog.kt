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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.PaymentMethod
import com.example.data.repository.PaymentStatus
import com.example.data.repository.SubscriptionManager
import com.example.data.repository.SubscriptionState
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletSecondary
import java.net.URLEncoder

@Composable
fun SubscriptionDialog(
    subscriptionState: SubscriptionState,
    onDismiss: () -> Unit,
    onActivate: (trxId: String, phone: String, method: PaymentMethod) -> Pair<Boolean, String>
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val manager = remember { SubscriptionManager.getInstance(context) }

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
        val parsed = manager.parseSmsOrText(text)
        if (parsed.isRecognized) {
            parsed.trxId?.let { trxId = it }
            parsed.phone?.let { senderPhone = it }
            parsed.method?.let { selectedMethod = it }
            detectedFeedback = "✓ SMS থেকে শনাক্ত হয়েছে: TrxID: ${parsed.trxId ?: "N/A"}, নম্বর: ${parsed.phone ?: "N/A"}"
            errorMessage = null
        } else {
            detectedFeedback = "পাস করা লেখায় TrxID বা মোবাইল নম্বর পাওয়া যায়নি।"
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(listOf(CyanAccent, VioletSecondary)),
                    shape = RoundedCornerShape(24.dp)
                ),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(CyanAccent, IndigoPrimary))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = "Premium",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "টাইপিং মাস্টার প্রিমিয়াম",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "মাসিক মাত্র ৪৫ টাকা (Send Money)",
                                style = MaterialTheme.typography.labelMedium,
                                color = EmeraldSuccess,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_subscription_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Current Subscription / Trial Status Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (subscriptionState.isSubscribed) {
                            EmeraldSuccess.copy(alpha = 0.15f)
                        } else if (subscriptionState.isTrialActive) {
                            IndigoPrimary.copy(alpha = 0.15f)
                        } else {
                            AmberWarning.copy(alpha = 0.15f)
                        }
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = if (subscriptionState.isSubscribed) Icons.Default.Verified else Icons.Default.Star,
                            contentDescription = null,
                            tint = if (subscriptionState.isSubscribed) EmeraldSuccess else AmberWarning
                        )
                        Column {
                            Text(
                                text = if (subscriptionState.isSubscribed) {
                                    "আপনার সাবস্ক্রিপশন সক্রিয় রয়েছে"
                                } else if (subscriptionState.isTrialActive) {
                                    "৭ দিনের ফ্রি ট্রায়াল চলছে (বাকি ${subscriptionState.trialDaysLeft} দিন)"
                                } else {
                                    "ফ্রি ট্রায়ালের মেয়াদ শেষ হয়েছে!"
                                },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (subscriptionState.isSubscribed) {
                                    "মেয়াদ: ${subscriptionState.expiryDateFormatted} (বাকি ${subscriptionState.subscriptionDaysLeft} দিন)"
                                } else {
                                    "ফুল অ্যাক্সেস ও সরকারি চাকরির স্পিড টেস্টের জন্য সাবস্ক্রিপশন নিন।"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payment Instruction & Official Numbers Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "💳 পেমেন্ট করার নিয়মাবলী (Send Money):",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyanAccent
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "১. বিকাশ বা নগদ অ্যাপে ঢুকে 'Send Money' নির্বাচন করুন।",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "২. নিচের নম্বরে মাসিক ফি ৪৫ টাকা পাঠান:",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Number Copy Box
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "বিকাশ ও নগদ পার্সোনাল নম্বর:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = SubscriptionManager.BKASH_NAGAD_NUMBER,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = CyanAccent
                                    )
                                }

                                Button(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(SubscriptionManager.BKASH_NAGAD_NUMBER))
                                        Toast.makeText(context, "নম্বর ${SubscriptionManager.BKASH_NAGAD_NUMBER} কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.testTag("copy_phone_number_button"),
                                    contentPadding = ButtonDefaults.TextButtonContentPadding,
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "কপি", fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "৩. টাকা পাঠানোর পর ফিরতি SMS এর TrxID নিচে লিখুন অথবা SMS পেস্ট করে অটো-পার্স করুন।",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SMS Parsing Automation Helper Card (Gemini recommended)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = IndigoPrimary.copy(alpha = 0.08f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showSmsParser = !showSmsParser },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = CyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "⚡ এসএমএস পার্সিং অটোমেশন (SMS Auto-Fill)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent
                                )
                            }
                            Icon(
                                imageVector = if (showSmsParser) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        AnimatedVisibility(visible = showSmsParser) {
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                Text(
                                    text = "বিকাশ বা নগদ থেকে আসা পুরো SMS টি কপি করে পেস্ট করুন। অ্যাপ স্বয়ংক্রিয়ভাবে TrxID ও নম্বর শনাক্ত করবে:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = smsInputText,
                                    onValueChange = {
                                        smsInputText = it
                                        processSmsText(it)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("sms_raw_input_field"),
                                    label = { Text("বিকাশ/নগদ ফিরতি SMS এখানে পেস্ট করুন") },
                                    placeholder = { Text("যেমন: Send Money Tk 45 to 01984251178... TrxID BL83K9M2P1") },
                                    minLines = 2,
                                    maxLines = 4,
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            val clip = clipboardManager.getText()?.text
                                            if (!clip.isNullOrBlank()) {
                                                smsInputText = clip
                                                processSmsText(clip)
                                                Toast.makeText(context, "ক্লিপবোর্ড থেকে SMS স্ক্যান সম্পন্ন!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ক্লিপবোর্ডে কোনো লেখা পাওয়া যায়নি।", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("ক্লিপবোর্ড থেকে নিন", fontSize = 12.sp)
                                    }
                                }

                                if (detectedFeedback != null) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = detectedFeedback ?: "",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (detectedFeedback?.startsWith("✓") == true) EmeraldSuccess else AmberWarning,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payment Method Selector
                Text(
                    text = "পেমেন্টের মাধ্যম নির্বাচন করুন:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PaymentMethod.entries.forEach { method ->
                        val isSelected = selectedMethod == method
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedMethod = method },
                            color = if (isSelected) IndigoPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) CyanAccent else Color.Transparent
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (method == PaymentMethod.BKASH) "🌸 বিকাশ (bKash)" else "🔶 নগদ (Nagad)",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) CyanAccent else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sender Phone Input Field
                OutlinedTextField(
                    value = senderPhone,
                    onValueChange = {
                        senderPhone = it.filter { char -> char.isDigit() }.take(11)
                        errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sender_phone_input"),
                    label = { Text("যে নম্বর থেকে টাকা পাঠিয়েছেন (যেমন: 017...)") },
                    placeholder = { Text("01XXXXXXXXX") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // TrxID Input Field (etTrxId as in Gemini's layout)
                OutlinedTextField(
                    value = trxId,
                    onValueChange = {
                        trxId = it.uppercase()
                        errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trx_id_input"),
                    label = { Text("ট্রানজেকশন আইডি (TrxID) লিখুন") },
                    placeholder = { Text("যেমন: BL83K9M2P1 বা 72K9M1PQ") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Error Message
                AnimatedVisibility(visible = errorMessage != null) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Success Message
                AnimatedVisibility(visible = successMessage != null) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = EmeraldSuccess.copy(alpha = 0.2f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldSuccess)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldSuccess
                                )
                                Text(
                                    text = successMessage ?: "",
                                    color = EmeraldSuccess,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Submit Payment Button (btnSubmitPayment as in Gemini's layout)
                Button(
                    onClick = {
                        val result = onActivate(trxId, senderPhone, selectedMethod)
                        if (result.first) {
                            successMessage = result.second
                            errorMessage = null
                            Toast.makeText(context, result.second, Toast.LENGTH_LONG).show()
                        } else {
                            errorMessage = result.second
                            successMessage = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btnSubmitPayment"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "পেমেন্ট কনফার্ম করুন (Confirm Payment)",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // WhatsApp Business Help & Instant Confirmation Button (01635586380)
                OutlinedButton(
                    onClick = {
                        val message = "আসসালামু আলাইকুম, আমি টাইপিং মাস্টার অ্যাপে ৪৫ টাকা সাবস্ক্রিপশন ফি পাঠিয়েছি।\n" +
                                "পেমেন্ট মাধ্যম: ${selectedMethod.titleBn}\n" +
                                "প্রেরকের নম্বর: ${if (senderPhone.isNotBlank()) senderPhone else "N/A"}\n" +
                                "TrxID: ${if (trxId.isNotBlank()) trxId else "N/A"}\n" +
                                "দয়া করে পেমেন্ট যাচাই করে রেকর্ড কনফার্ম করবেন।"

                        try {
                            val encodedMsg = URLEncoder.encode(message, "UTF-8")
                            val uri = Uri.parse("https://wa.me/8801635586380?text=$encodedMsg")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "WhatsApp ওপেন করা যাচ্ছে না। দয়া করে 01635586380 নম্বরে কল/মেসেজ করুন।",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("whatsapp_support_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "WhatsApp",
                        tint = CyanAccent
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "💬 WhatsApp-এ পেমেন্ট কনফার্ম করুন (01635586380)",
                        color = CyanAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Payment History & Saved Database Records (Gemini's database persistence feature)
                if (subscriptionState.recentPayments.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showHistory = !showHistory },
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = CyanAccent
                                    )
                                    Text(
                                        text = "📜 পেমেন্ট ডাটাবেস রেকর্ড (${subscriptionState.recentPayments.size}টি)",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Icon(
                                    imageVector = if (showHistory) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            AnimatedVisibility(visible = showHistory) {
                                Column(modifier = Modifier.padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    subscriptionState.recentPayments.forEach { record ->
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            border = androidx.compose.foundation.BorderStroke(0.5.dp, IndigoPrimary.copy(alpha = 0.3f))
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        text = "TrxID: ${record.trxId} (${record.method.titleBn})",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "নম্বর: ${record.senderPhone} • ${record.formattedDate}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }

                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = if (record.status == PaymentStatus.APPROVED) EmeraldSuccess.copy(alpha = 0.2f) else AmberWarning.copy(alpha = 0.2f)
                                                ) {
                                                    Text(
                                                        text = record.status.titleBn,
                                                        color = if (record.status == PaymentStatus.APPROVED) EmeraldSuccess else AmberWarning,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Text(
                    text = "বিঃদ্রঃ পেমেন্ট সংক্রান্ত যেকোনো সহায়তায় আমাদের হোয়াটসঅ্যাপে (01635586380) সার্বক্ষণিক যোগাযোগ করতে পারেন।",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
