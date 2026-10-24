package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AdCreative(
    val id: String,
    val headline: String,
    val description: String,
    val actionText: String,
    val targetUrl: String,
    val category: String = "কমিউনিটি স্পনসর"
)

data class AdStats(
    val totalBannerImpressions: Int = 0,
    val totalInterstitialImpressions: Int = 0,
    val estimatedEarningsBdt: Double = 0.0,
    val isAdFree: Boolean = false,
    val adMobBannerId: String = AdManager.DEFAULT_BANNER_ID,
    val adMobInterstitialId: String = AdManager.DEFAULT_INTERSTITIAL_ID
) {
    val totalImpressions: Int get() = totalBannerImpressions + totalInterstitialImpressions
}

class AdManager private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _adStats = MutableStateFlow(loadStats())
    val adStats: StateFlow<AdStats> = _adStats.asStateFlow()

    private val _currentBannerCreative = MutableStateFlow(sampleCreatives.first())
    val currentBannerCreative: StateFlow<AdCreative> = _currentBannerCreative.asStateFlow()

    private var creativeIndex = 0

    init {
        rotateCreative()
    }

    fun rotateCreative(): AdCreative {
        if (sampleCreatives.isEmpty()) return sampleCreatives.first()
        creativeIndex = (creativeIndex + 1) % sampleCreatives.size
        val next = sampleCreatives[creativeIndex]
        _currentBannerCreative.value = next
        return next
    }

    fun recordBannerImpression() {
        if (_adStats.value.isAdFree) return
        val current = prefs.getInt(KEY_BANNER_IMPRESSIONS, 0) + 1
        prefs.edit().putInt(KEY_BANNER_IMPRESSIONS, current).apply()
        updateStatsState()
    }

    fun recordInterstitialImpression() {
        if (_adStats.value.isAdFree) return
        val current = prefs.getInt(KEY_INTERSTITIAL_IMPRESSIONS, 0) + 1
        prefs.edit().putInt(KEY_INTERSTITIAL_IMPRESSIONS, current).apply()
        updateStatsState()
    }

    fun setAdFree(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_IS_AD_FREE, enabled).apply()
        updateStatsState()
    }

    fun resetForTesting() {
        prefs.edit().clear().apply()
        updateStatsState()
    }

    fun updateCustomAdMobIds(bannerId: String?, interstitialId: String?) {
        val editor = prefs.edit()
        if (!bannerId.isNullOrBlank()) {
            editor.putString(KEY_CUSTOM_BANNER_ID, bannerId.trim())
        }
        if (!interstitialId.isNullOrBlank()) {
            editor.putString(KEY_CUSTOM_INTERSTITIAL_ID, interstitialId.trim())
        }
        editor.apply()
        updateStatsState()
    }

    private fun loadStats(): AdStats {
        val banners = prefs.getInt(KEY_BANNER_IMPRESSIONS, 0)
        val interstitials = prefs.getInt(KEY_INTERSTITIAL_IMPRESSIONS, 0)
        val isAdFree = prefs.getBoolean(KEY_IS_AD_FREE, false)
        val savedBanner = prefs.getString(KEY_CUSTOM_BANNER_ID, null)
        val bannerId = if (savedBanner.isNullOrBlank() || savedBanner.contains("3940256099942544")) {
            DEFAULT_BANNER_ID
        } else {
            savedBanner
        }
        val savedInterstitial = prefs.getString(KEY_CUSTOM_INTERSTITIAL_ID, null)
        val interstitialId = if (savedInterstitial.isNullOrBlank() || savedInterstitial.contains("3940256099942544")) {
            DEFAULT_INTERSTITIAL_ID
        } else {
            savedInterstitial
        }

        // Estimated eCPM calculation (avg ~৳120 - ৳180 per 1,000 mixed impressions)
        val total = banners + interstitials
        val estimatedEarnings = (total * 0.16)

        return AdStats(
            totalBannerImpressions = banners,
            totalInterstitialImpressions = interstitials,
            estimatedEarningsBdt = estimatedEarnings,
            isAdFree = isAdFree,
            adMobBannerId = bannerId,
            adMobInterstitialId = interstitialId
        )
    }

    private fun updateStatsState() {
        _adStats.value = loadStats()
    }

    companion object {
        private const val PREFS_NAME = "typing_master_ad_prefs"
        private const val KEY_BANNER_IMPRESSIONS = "key_banner_impressions"
        private const val KEY_INTERSTITIAL_IMPRESSIONS = "key_interstitial_impressions"
        private const val KEY_IS_AD_FREE = "key_is_ad_free"
        private const val KEY_CUSTOM_BANNER_ID = "key_custom_banner_id"
        private const val KEY_CUSTOM_INTERSTITIAL_ID = "key_custom_interstitial_id"

        // Official Google AdMob Live Production IDs for Typing Master App
        const val ADMOB_APP_ID = "ca-app-pub-2983938252837230~4640265693"
        const val DEFAULT_BANNER_ID = "ca-app-pub-2983938252837230/6080632034"
        const val DEFAULT_INTERSTITIAL_ID = "ca-app-pub-2983938252837230/8387939014"

        val sampleCreatives = listOf(
            AdCreative(
                id = "group_19k",
                headline = "👥 ১৯,০০০ সদস্যের টাইপিং গ্রুপ",
                description = "গ্রুপে যুক্ত হয়ে লাইভ টাইপিং প্রতিযোগিতা ও চাকরির প্র্যাকটিস করুন!",
                actionText = "গ্রুপে যুক্ত হোন",
                targetUrl = "https://www.facebook.com",
                category = "কমিউনিটি প্রচার"
            ),
            AdCreative(
                id = "fb_page",
                headline = "🌐 অফিসিয়াল ফেসবুক পেজ",
                description = "দৈনিক বিজয় ও অভ্র শর্টকাট টিপস এবং আপডেট পেতে পেজে ফলো দিন।",
                actionText = "পেজ ফলো করুন",
                targetUrl = "https://www.facebook.com",
                category = "অফিসিয়াল পেজ"
            ),
            AdCreative(
                id = "wa_biz",
                headline = "💬 WhatsApp বিজনেস সাপোর্ট",
                description = "সরাসরি অ্যাডমিনের সাথে যোগাযোগ বা ফিডব্যাক দিতে WhatsApp করুন।",
                actionText = "মেসেজ দিন (01635586380)",
                targetUrl = "https://wa.me/8801635586380",
                category = "সরাসরি সাপোর্ট"
            ),
            AdCreative(
                id = "pro_typing",
                headline = "⚡ সরকারি চাকরি টাইপিং স্পিড টেস্ট",
                description = "বিজয় লেআউটে নির্ভুল টাইপিং প্র্যাকটিস করে গতি ৪০+ WPM করুন।",
                actionText = "স্পিড পরীক্ষা দিন",
                targetUrl = "app://speed_test",
                category = "স্পনসরড বিজ্ঞাপন"
            )
        )

        @Volatile
        private var instance: AdManager? = null

        fun getInstance(context: Context): AdManager {
            return instance ?: synchronized(this) {
                instance ?: AdManager(context).also { instance = it }
            }
        }
    }
}
