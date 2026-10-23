package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max

enum class PaymentMethod(val displayName: String, val titleBn: String) {
    BKASH("bKash", "বিকাশ"),
    NAGAD("Nagad", "নগদ")
}

data class SubscriptionState(
    val isSubscribed: Boolean = false,
    val isTrialActive: Boolean = true,
    val trialDaysLeft: Int = 7,
    val subscriptionDaysLeft: Int = 0,
    val expiryDateFormatted: String = "",
    val statusMessageBn: String = "",
    val activeTrxId: String? = null,
    val senderPhone: String? = null,
    val paymentMethod: String? = null
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

    private fun calculateCurrentState(): SubscriptionState {
        val now = System.currentTimeMillis()
        val firstLaunch = prefs.getLong(KEY_FIRST_LAUNCH_TIME, now)
        val trialEndTime = firstLaunch + (TRIAL_DURATION_DAYS * 24 * 60 * 60 * 1000L)
        val premiumExpiry = prefs.getLong(KEY_PREMIUM_EXPIRY_TIME, 0L)
        val isSubscribed = premiumExpiry > now

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("bn", "BD"))

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
                paymentMethod = prefs.getString(KEY_PAYMENT_METHOD, null)
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
                paymentMethod = null
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
            paymentMethod = null
        )
    }

    /**
     * Instantly activates a 30-day subscription when user provides TrxID and Phone
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

        refreshState()
        return Pair(true, "অভিনন্দন! আপনার ৩০ দিনের প্রিমিয়াম সাবস্ক্রিপশন সফলভাবে সক্রিয় হয়েছে।")
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
