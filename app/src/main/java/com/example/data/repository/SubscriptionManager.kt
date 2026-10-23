package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.ceil
import kotlin.math.max

enum class PaymentMethod(val displayName: String, val titleBn: String) {
    BKASH("bKash", "বিকাশ"),
    NAGAD("Nagad", "নগদ")
}

enum class PaymentStatus(val code: String, val titleBn: String) {
    PENDING("pending", "যাচাইকরণাধীন"),
    APPROVED("approved", "অনুমোদিত ও সক্রিয়")
}

data class PaymentRecord(
    val id: String = UUID.randomUUID().toString(),
    val trxId: String,
    val senderPhone: String,
    val method: PaymentMethod,
    val amountBdt: Int = 45,
    val timestamp: Long = System.currentTimeMillis(),
    val status: PaymentStatus = PaymentStatus.APPROVED,
    val formattedDate: String = ""
)

data class SmsParseResult(
    val trxId: String? = null,
    val phone: String? = null,
    val method: PaymentMethod? = null,
    val amount: Int? = null,
    val isRecognized: Boolean = false
)

data class SubscriptionState(
    val isSubscribed: Boolean = false,
    val isTrialActive: Boolean = true,
    val trialDaysLeft: Int = 7,
    val subscriptionDaysLeft: Int = 0,
    val expiryDateFormatted: String = "",
    val statusMessageBn: String = "",
    val activeTrxId: String? = null,
    val senderPhone: String? = null,
    val paymentMethod: String? = null,
    val recentPayments: List<PaymentRecord> = emptyList()
)

class SubscriptionManager private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _subscriptionState = MutableStateFlow(calculateCurrentState())
    val subscriptionState: StateFlow<SubscriptionState> = _subscriptionState.asStateFlow()

    init {
        // Ensure first launch time is recorded
        if (!prefs.contains(KEY_FIRST_LAUNCH_TIME)) {
            val now = System.currentTimeMillis()
            prefs.edit().putLong(KEY_FIRST_LAUNCH_TIME, now).apply()
        }
        refreshState()
    }

    fun refreshState() {
        _subscriptionState.value = calculateCurrentState()
    }

    fun canAccessApp(): Boolean {
        val state = calculateCurrentState()
        return state.isSubscribed || state.isTrialActive
    }

    fun resetForTesting() {
        prefs.edit().clear().apply()
        val now = System.currentTimeMillis()
        prefs.edit().putLong(KEY_FIRST_LAUNCH_TIME, now).apply()
        refreshState()
    }

    fun getPaymentRecords(): List<PaymentRecord> {
        val jsonStr = prefs.getString(KEY_PAYMENT_RECORDS_JSON, null) ?: return emptyList()
        val records = mutableListOf<PaymentRecord>()
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("bn", "BD"))
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val ts = obj.optLong("timestamp", System.currentTimeMillis())
                records.add(
                    PaymentRecord(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        trxId = obj.optString("trxId", ""),
                        senderPhone = obj.optString("senderPhone", ""),
                        method = try {
                            PaymentMethod.valueOf(obj.optString("method", "BKASH"))
                        } catch (_: Exception) {
                            PaymentMethod.BKASH
                        },
                        amountBdt = obj.optInt("amountBdt", 45),
                        timestamp = ts,
                        status = try {
                            PaymentStatus.valueOf(obj.optString("status", "APPROVED"))
                        } catch (_: Exception) {
                            PaymentStatus.APPROVED
                        },
                        formattedDate = dateFormat.format(Date(ts))
                    )
                )
            }
        } catch (_: Exception) {
            return emptyList()
        }
        return records.sortedByDescending { it.timestamp }
    }

    private fun savePaymentRecord(record: PaymentRecord) {
        val currentRecords = getPaymentRecords().toMutableList()
        currentRecords.removeAll { it.trxId.equals(record.trxId, ignoreCase = true) }
        currentRecords.add(0, record)

        val array = JSONArray()
        for (r in currentRecords.take(20)) { // keep last 20 records
            val obj = JSONObject().apply {
                put("id", r.id)
                put("trxId", r.trxId)
                put("senderPhone", r.senderPhone)
                put("method", r.method.name)
                put("amountBdt", r.amountBdt)
                put("timestamp", r.timestamp)
                put("status", r.status.name)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_PAYMENT_RECORDS_JSON, array.toString()).apply()
    }

    private fun calculateCurrentState(): SubscriptionState {
        val now = System.currentTimeMillis()
        val firstLaunch = prefs.getLong(KEY_FIRST_LAUNCH_TIME, now)
        val trialEndTime = firstLaunch + (TRIAL_DURATION_DAYS * 24 * 60 * 60 * 1000L)
        val premiumExpiry = prefs.getLong(KEY_PREMIUM_EXPIRY_TIME, 0L)
        val isSubscribed = premiumExpiry > now

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("bn", "BD"))
        val records = getPaymentRecords()

        if (isSubscribed) {
            val daysLeft = max(1, ceil((premiumExpiry - now) / (24f * 60 * 60 * 1000f)).toInt())
            val expiryDateStr = dateFormat.format(Date(premiumExpiry))
            return SubscriptionState(
                isSubscribed = true,
                isTrialActive = false,
                trialDaysLeft = 0,
                subscriptionDaysLeft = daysLeft,
                expiryDateFormatted = expiryDateStr,
                statusMessageBn = "⭐ প্রিমিয়াম সদস্য (মেয়াদ: $expiryDateStr পর্যন্ত, বাকি $daysLeft দিন)",
                activeTrxId = prefs.getString(KEY_ACTIVE_TRX_ID, null),
                senderPhone = prefs.getString(KEY_SENDER_PHONE, null),
                paymentMethod = prefs.getString(KEY_PAYMENT_METHOD, null),
                recentPayments = records
            )
        }

        val isTrialActive = now < trialEndTime
        if (isTrialActive) {
            val daysLeft = max(1, ceil((trialEndTime - now) / (24f * 60 * 60 * 1000f)).toInt())
            val trialEndStr = dateFormat.format(Date(trialEndTime))
            return SubscriptionState(
                isSubscribed = false,
                isTrialActive = true,
                trialDaysLeft = daysLeft,
                subscriptionDaysLeft = 0,
                expiryDateFormatted = trialEndStr,
                statusMessageBn = "🎁 ৭ দিনের ফ্রি ট্রায়াল চলছে (বাকি $daysLeft দিন • মেয়াদ: $trialEndStr)",
                activeTrxId = null,
                senderPhone = null,
                paymentMethod = null,
                recentPayments = records
            )
        }

        // Trial has expired
        return SubscriptionState(
            isSubscribed = false,
            isTrialActive = false,
            trialDaysLeft = 0,
            subscriptionDaysLeft = 0,
            expiryDateFormatted = "",
            statusMessageBn = "⚠️ ফ্রি ট্রায়ালের মেয়াদ শেষ! ফুল অ্যাক্সেস পেতে মাসিক ৪৫ টাকা সাবস্ক্রিপশন নিন।",
            activeTrxId = null,
            senderPhone = null,
            paymentMethod = null,
            recentPayments = records
        )
    }

    /**
     * SMS and Clipboard text parser: extracts TrxID, Phone number, Payment Method, and amount
     */
    fun parseSmsOrText(rawText: String): SmsParseResult {
        val clean = rawText.trim()
        if (clean.isBlank()) return SmsParseResult()

        // 1. TrxID regex detection (bKash "TrxID BL83K9M2P1", Nagad "TxnId: 72K9M1PQ", etc.)
        val trxRegex = Regex("""(?i)(?:TrxID|TxnId|TxId|TransID|Trx\s*Id)[\s:]*([A-Za-z0-9]{6,16})""")
        val trxMatch = trxRegex.find(clean)?.groupValues?.getOrNull(1)

        // Fallback: If user pasted only the TrxID or alphanumeric token (e.g. BL83K9M2P1)
        val fallbackTrx = if (trxMatch == null) {
            val words = clean.split(Regex("""[\s,.:;!]+"""))
            words.firstOrNull { token ->
                token.length in 7..14 &&
                        token.any { it.isDigit() } &&
                        token.any { it.isLetter() } &&
                        token.all { it.isLetterOrDigit() }
            }
        } else null

        val resolvedTrx = trxMatch ?: fallbackTrx

        // 2. Phone detection (e.g. 017..., 019...)
        val phoneRegex = Regex("""01[3-9]\d{8}""")
        val phoneMatches = phoneRegex.findAll(clean).map { it.value }.toList()
        // If the merchant number 01984251178 is present, pick the customer/sender phone if available
        val senderPhone = phoneMatches.firstOrNull { it != BKASH_NAGAD_NUMBER } ?: phoneMatches.firstOrNull()

        // 3. Payment method detection
        val detectedMethod = when {
            clean.contains("nagad", ignoreCase = true) || clean.contains("নগদ") || clean.contains("txnid", ignoreCase = true) -> PaymentMethod.NAGAD
            clean.contains("bkash", ignoreCase = true) || clean.contains("বিকাশ") || clean.contains("trxid", ignoreCase = true) -> PaymentMethod.BKASH
            else -> null
        }

        // 4. Amount detection
        val amountRegex = Regex("""(?i)(?:Tk|BDT|টাকা)[\s.:]*([0-9]{2,4})""")
        val amountMatch = amountRegex.find(clean)?.groupValues?.getOrNull(1)?.toIntOrNull()

        val isRecognized = resolvedTrx != null || senderPhone != null

        return SmsParseResult(
            trxId = resolvedTrx?.uppercase(),
            phone = senderPhone,
            method = detectedMethod,
            amount = amountMatch ?: SUBSCRIPTION_FEE_BDT,
            isRecognized = isRecognized
        )
    }

    /**
     * Instantly activates a 30-day subscription when user provides TrxID and Phone,
     * saving the payment record into local database storage.
     */
    fun activateSubscriptionWithTrxId(
        trxId: String,
        senderPhone: String,
        method: PaymentMethod
    ): Pair<Boolean, String> {
        val cleanTrxId = trxId.trim().uppercase()
        val cleanPhone = senderPhone.trim()

        if (cleanPhone.length < 11 || !cleanPhone.startsWith("01")) {
            return Pair(false, "দয়া করে সঠিক ১১ ডিজিটের মোবাইল নম্বর দিন (যেমন: 017xxxxxxxx)")
        }

        if (cleanTrxId.length < 6) {
            return Pair(false, "দয়া করে সঠিক ট্রানজেকশন আইডি (TrxID) দিন (কমপক্ষে ৬-১০ অক্ষর)")
        }

        val usedTrxIds = prefs.getStringSet(KEY_USED_TRX_IDS, emptySet())?.toMutableSet() ?: mutableSetOf()
        if (usedTrxIds.contains(cleanTrxId)) {
            return Pair(false, "এই TrxID টি ইতিমধ্যে একবার ব্যবহার করা হয়েছে!")
        }

        usedTrxIds.add(cleanTrxId)

        val now = System.currentTimeMillis()
        val currentExpiry = prefs.getLong(KEY_PREMIUM_EXPIRY_TIME, 0L)
        val baseTime = if (currentExpiry > now) currentExpiry else now
        val newExpiry = baseTime + (SUBSCRIPTION_DURATION_DAYS * 24 * 60 * 60 * 1000L)

        prefs.edit()
            .putLong(KEY_PREMIUM_EXPIRY_TIME, newExpiry)
            .putString(KEY_ACTIVE_TRX_ID, cleanTrxId)
            .putString(KEY_SENDER_PHONE, cleanPhone)
            .putString(KEY_PAYMENT_METHOD, method.name)
            .putStringSet(KEY_USED_TRX_IDS, usedTrxIds)
            .apply()

        // Create and save payment database record
        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("bn", "BD"))
        val record = PaymentRecord(
            trxId = cleanTrxId,
            senderPhone = cleanPhone,
            method = method,
            amountBdt = SUBSCRIPTION_FEE_BDT,
            timestamp = now,
            status = PaymentStatus.APPROVED,
            formattedDate = dateFormat.format(Date(now))
        )
        savePaymentRecord(record)

        refreshState()
        return Pair(true, "অভিনন্দন! আপনার TrxID ($cleanTrxId) গ্রহণ করা হয়েছে এবং ৩০ দিনের প্রিমিয়াম অ্যাক্সেস সক্রিয় হয়েছে।")
    }

    /**
     * Generate WhatsApp confirmation URL
     */
    fun createWhatsAppMessage(trxId: String?, phone: String?, method: String?): String {
        val actualTrx = trxId ?: prefs.getString(KEY_ACTIVE_TRX_ID, "N/A") ?: "N/A"
        val actualPhone = phone ?: prefs.getString(KEY_SENDER_PHONE, "N/A") ?: "N/A"
        val actualMethod = method ?: prefs.getString(KEY_PAYMENT_METHOD, "bKash") ?: "bKash"

        return "আসসালামু আলাইকুম, আমি টাইপিং মাস্টার অ্যাপে ৪৫ টাকা সাবস্ক্রিপশন ফি পাঠিয়েছি।\n" +
                "পেমেন্ট মাধ্যম: $actualMethod\n" +
                "প্রেরকের নম্বর: $actualPhone\n" +
                "TrxID: $actualTrx\n" +
                "আমার অ্যাপে সাবস্ক্রিপশন সক্রিয় হয়েছে। দয়া করে রেকর্ড আপডেট রাখবেন।"
    }

    companion object {
        const val PREFS_NAME = "typing_master_subscription_prefs"
        const val KEY_FIRST_LAUNCH_TIME = "key_first_launch_time"
        const val KEY_PREMIUM_EXPIRY_TIME = "key_premium_expiry_time"
        const val KEY_ACTIVE_TRX_ID = "key_active_trx_id"
        const val KEY_SENDER_PHONE = "key_sender_phone"
        const val KEY_PAYMENT_METHOD = "key_payment_method"
        const val KEY_USED_TRX_IDS = "key_used_trx_ids"
        const val KEY_PAYMENT_RECORDS_JSON = "key_payment_records_json"

        const val BKASH_NAGAD_NUMBER = "01984251178"
        const val WHATSAPP_NUMBER = "01635586380"
        const val SUBSCRIPTION_FEE_BDT = 45
        const val TRIAL_DURATION_DAYS = 7
        const val SUBSCRIPTION_DURATION_DAYS = 30

        @Volatile
        private var instance: SubscriptionManager? = null

        fun getInstance(context: Context): SubscriptionManager {
            return instance ?: synchronized(this) {
                instance ?: SubscriptionManager(context).also { instance = it }
            }
        }
    }
}

