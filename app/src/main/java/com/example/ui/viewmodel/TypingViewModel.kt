package com.example.ui.viewmodel

import android.app.Application
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.TypingDatabase
import com.example.data.datasource.LessonDataSource
import com.example.data.model.Lesson
import com.example.data.model.MonthlyReportSummary
import com.example.data.model.TypingLanguage
import com.example.data.model.TypingMode
import com.example.data.model.TypingResult
import com.example.data.repository.AdCreative
import com.example.data.repository.AdManager
import com.example.data.repository.AdStats
import com.example.data.repository.PaymentMethod
import com.example.data.repository.SubscriptionManager
import com.example.data.repository.SubscriptionState
import com.example.data.repository.TypingRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class TypingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TypingRepository
    private val subscriptionManager = SubscriptionManager.getInstance(application)
    private val adManager = AdManager.getInstance(application)
    private val authRepository = com.example.data.repository.AuthRepository(application)
    private val leaderboardRepository = com.example.data.repository.LeaderboardRepository(application)
    private val userDataSyncRepository = com.example.data.repository.UserDataSyncRepository(application)
    private val updateRepository = com.example.data.repository.AppUpdateRepository(application)
    private val vibrator = application.getSystemService(Vibrator::class.java)

    val subscriptionState: StateFlow<SubscriptionState> = subscriptionManager.subscriptionState
    val adStats: StateFlow<AdStats> = adManager.adStats
    val currentBannerCreative: StateFlow<AdCreative> = adManager.currentBannerCreative
    val showSubscriptionDialog = MutableStateFlow(false)
    val showInterstitialAd = MutableStateFlow(false)

    // In-App Update State
    val appUpdateInfo = MutableStateFlow<com.example.data.model.AppUpdateInfo?>(null)
    val isCheckingForUpdate = MutableStateFlow(false)
    val updateCheckStatusMessage = MutableStateFlow<String?>(null)
    private val inAppUpdateDownloader = com.example.data.repository.InAppUpdateDownloader(application)
    val updateDownloadState = MutableStateFlow<com.example.data.repository.UpdateDownloadState>(
        com.example.data.repository.UpdateDownloadState.Idle
    )
    private var downloadJob: Job? = null

    // Firebase Auth State
    val currentUser: StateFlow<com.google.firebase.auth.FirebaseUser?> = authRepository.getAuthStateFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = authRepository.currentUser
    )

    val isSigningIn = MutableStateFlow(false)
    val authMessage = MutableStateFlow<String?>(null)

    // Online Leaderboard State
    val leaderboardFilter = MutableStateFlow("ALL")

    val leaderboardEntries: StateFlow<List<com.example.data.model.LeaderboardEntry>> = combine(
        currentUser,
        leaderboardFilter
    ) { user, filter ->
        Pair(user, filter)
    }.flatMapLatest { (user, filter) ->
        if (user == null) {
            kotlinx.coroutines.flow.flowOf(emptyList())
        } else {
            leaderboardRepository.observeLeaderboard(filter)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun signInWithGoogle(context: android.content.Context) {
        viewModelScope.launch {
            isSigningIn.value = true
            authMessage.value = null
            val result = authRepository.signInWithGoogle(context)
            isSigningIn.value = false
            result.onSuccess { user ->
                val name = user.displayName ?: "ব্যবহারকারী"
                authMessage.value = "$name হিসেবে সফলভাবে লগইন হয়েছে!"
                try {
                    android.widget.Toast.makeText(context, "স্বাগতম, $name! সফলভাবে লগইন হয়েছে।", android.widget.Toast.LENGTH_SHORT).show()
                } catch (_: Throwable) {}
            }.onFailure { err ->
                val errorMsg = err.localizedMessage ?: err.message ?: "লগইন সম্পন্ন হয়নি"
                authMessage.value = errorMsg
                try {
                    android.widget.Toast.makeText(context, errorMsg, android.widget.Toast.LENGTH_LONG).show()
                } catch (_: Throwable) {}
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            authMessage.value = "লগআউট সম্পন্ন হয়েছে"
            try {
                repository.clearAll()
            } catch (_: Throwable) {}
        }
    }

    fun setLeaderboardFilter(filter: String) {
        leaderboardFilter.value = filter
    }

    fun syncBestLocalScoreToLeaderboard() {
        viewModelScope.launch {
            try {
                val user = authRepository.currentUser ?: return@launch
                val localResults = allResults.value
                if (localResults.isNotEmpty()) {
                    val peakResult = localResults.maxByOrNull { it.wpm }
                    if (peakResult != null && peakResult.wpm > 0) {
                        leaderboardRepository.recordPersonalBest(
                            wpm = peakResult.wpm,
                            accuracy = peakResult.accuracy,
                            language = peakResult.language
                        )
                    }
                }
            } catch (e: Throwable) {
                android.util.Log.e("TypingViewModel", "syncBestLocalScore error: ${e.message}")
            }
        }
    }

    fun openSubscriptionDialog() {
        showSubscriptionDialog.value = true
    }

    fun closeSubscriptionDialog() {
        showSubscriptionDialog.value = false
    }

    fun canAccessPractice(): Boolean {
        // App is 100% free and unlocked for all users with ads!
        return true
    }

    fun recordBannerImpression() = adManager.recordBannerImpression()

    fun recordInterstitialImpression() = adManager.recordInterstitialImpression()

    fun setAdFree(enabled: Boolean) = adManager.setAdFree(enabled)

    fun setUseGoogleTestUnits(enabled: Boolean) = adManager.setUseGoogleTestUnits(enabled)

    fun setLastAdStatus(status: String) = adManager.setLastAdStatus(status)

    fun updateCustomAdMobIds(bannerId: String?, interstitialId: String?) =
        adManager.updateCustomAdMobIds(bannerId, interstitialId)

    fun dismissInterstitialAd() {
        showInterstitialAd.value = false
    }

    fun activateSubscription(
        trxId: String,
        senderPhone: String,
        method: PaymentMethod
    ): Pair<Boolean, String> {
        val result = subscriptionManager.activateSubscriptionWithTrxId(trxId, senderPhone, method)
        if (result.first) {
            adManager.setAdFree(true)
        }
        return result
    }

    fun parseSmsOrClipboard(text: String) = subscriptionManager.parseSmsOrText(text)

    fun getPaymentRecords() = subscriptionManager.getPaymentRecords()

    init {
        val db = TypingDatabase.getDatabase(application)
        repository = TypingRepository(db.typingResultDao())

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            currentUser.collect { user ->
                if (user != null) {
                    userDataSyncRepository.syncCloudToLocal(user.uid)
                    syncBestLocalScoreToLeaderboard()
                }
            }
        }

        // Check for updates on startup
        checkForUpdate(isManual = false)
    }

    // App Navigation & Selected Language
    private val _selectedLanguage = MutableStateFlow(TypingLanguage.BANGLA)
    val selectedLanguage: StateFlow<TypingLanguage> = _selectedLanguage.asStateFlow()

    private val _selectedMode = MutableStateFlow(TypingMode.LESSON)
    val selectedMode: StateFlow<TypingMode> = _selectedMode.asStateFlow()

    // Session State
    private val _targetText = MutableStateFlow("")
    val targetText: StateFlow<String> = _targetText.asStateFlow()

    private val _sessionTitle = MutableStateFlow("")
    val sessionTitle: StateFlow<String> = _sessionTitle.asStateFlow()

    private val _userInput = MutableStateFlow("")
    val userInput: StateFlow<String> = _userInput.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private val _timeLimitSeconds = MutableStateFlow<Int?>(null)
    val timeLimitSeconds: StateFlow<Int?> = _timeLimitSeconds.asStateFlow()

    private val _isSessionActive = MutableStateFlow(false)
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _isSessionFinished = MutableStateFlow(false)
    val isSessionFinished: StateFlow<Boolean> = _isSessionFinished.asStateFlow()

    private val _liveWpm = MutableStateFlow(0)
    val liveWpm: StateFlow<Int> = _liveWpm.asStateFlow()

    private val _liveAccuracy = MutableStateFlow(100f)
    val liveAccuracy: StateFlow<Float> = _liveAccuracy.asStateFlow()

    private val _mistakeCount = MutableStateFlow(0)
    val mistakeCount: StateFlow<Int> = _mistakeCount.asStateFlow()

    private val _correctWordsCount = MutableStateFlow(0)
    val correctWordsCount: StateFlow<Int> = _correctWordsCount.asStateFlow()

    private val _incorrectWordsCount = MutableStateFlow(0)
    val incorrectWordsCount: StateFlow<Int> = _incorrectWordsCount.asStateFlow()

    // Bangladesh Govt Typing Rule (2024 Gazette): 5% Error Threshold
    private val _liveErrorRatePercent = MutableStateFlow(0f)
    val liveErrorRatePercent: StateFlow<Float> = _liveErrorRatePercent.asStateFlow()

    private val _isLiveFailingGovtRule = MutableStateFlow(false)
    val isLiveFailingGovtRule: StateFlow<Boolean> = _isLiveFailingGovtRule.asStateFlow()

    // Input layout for Bangla: Bijoy (PC / External Keyboard) or Avro / System
    val inputLayoutMode = MutableStateFlow(com.example.data.model.InputLayoutMode.BIJOY)

    // Paper Copy / Blind Typing Mode (typing from external physical paper with full-screen typing box)
    val isPaperMode = MutableStateFlow(false)

    fun togglePaperMode() {
        isPaperMode.value = !isPaperMode.value
    }

    fun setPaperMode(enabled: Boolean) {
        isPaperMode.value = enabled
    }

    private val _lastCompletedResult = MutableStateFlow<TypingResult?>(null)
    val lastCompletedResult: StateFlow<TypingResult?> = _lastCompletedResult.asStateFlow()

    // Preferences
    val hapticEnabled = MutableStateFlow(true)
    val virtualKeyboardEnabled = MutableStateFlow(false)

    // Target Scrolling Mode / Speed (0 = Auto by typing, > 0 = Constant WPM Pacer Scroll)
    val targetScrollWpm = MutableStateFlow(0)

    fun setTargetScrollWpm(wpm: Int) {
        targetScrollWpm.value = wpm
    }

    // Scoreboard Filtering
    private val _filterLanguage = MutableStateFlow("ALL")
    val filterLanguage: StateFlow<String> = _filterLanguage.asStateFlow()

    val allResults: StateFlow<List<TypingResult>> = repository.allResults.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val filteredResults: StateFlow<List<TypingResult>> = combine(allResults, _filterLanguage) { results, filter ->
        when (filter) {
            "ENGLISH" -> results.filter { it.language == "ENGLISH" }
            "BANGLA" -> results.filter { it.language == "BANGLA" }
            else -> results
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Monthly Report Date Selection (Default: Current Year and Month)
    private val calendar = Calendar.getInstance()
    private val _reportYear = MutableStateFlow(calendar.get(Calendar.YEAR))
    val reportYear: StateFlow<Int> = _reportYear.asStateFlow()

    private val _reportMonth = MutableStateFlow(calendar.get(Calendar.MONTH) + 1)
    val reportMonth: StateFlow<Int> = _reportMonth.asStateFlow()

    val monthlyReport: StateFlow<MonthlyReportSummary> = combine(_reportYear, _reportMonth) { y, m ->
        Pair(y, m)
    }.flatMapLatest { (y, m) ->
        repository.getMonthlyReport(y, m)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MonthlyReportSummary(
            year = calendar.get(Calendar.YEAR),
            month = calendar.get(Calendar.MONTH) + 1,
            totalSessions = 0,
            averageWpm = 0f,
            highestWpm = 0,
            averageAccuracy = 0f,
            totalTimeSeconds = 0,
            englishSessionsCount = 0,
            banglaSessionsCount = 0,
            dailyStats = emptyList()
        )
    )

    private var timerJob: Job? = null
    private var sessionStartTimeMs: Long = 0

    fun setSelectedLanguage(lang: TypingLanguage) {
        _selectedLanguage.value = lang
    }

    fun setSelectedMode(mode: TypingMode) {
        _selectedMode.value = mode
    }

    fun setFilterLanguage(filter: String) {
        _filterLanguage.value = filter
    }

    fun changeReportMonth(offset: Int) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, _reportYear.value)
            set(Calendar.MONTH, _reportMonth.value - 1)
            add(Calendar.MONTH, offset)
        }
        _reportYear.value = cal.get(Calendar.YEAR)
        _reportMonth.value = cal.get(Calendar.MONTH) + 1
    }

    fun startLesson(lesson: Lesson) {
        _selectedLanguage.value = lesson.language
        _selectedMode.value = TypingMode.LESSON
        startSession(
            title = if (lesson.language == TypingLanguage.BANGLA) lesson.titleBn else lesson.title,
            text = lesson.targetText,
            timeLimitSecs = null
        )
    }

    fun startSpeedTest(durationSecs: Int, difficulty: String = "Medium", chosenTitle: String? = null, scrollWpm: Int? = null) {
        if (scrollWpm != null) {
            targetScrollWpm.value = scrollWpm
        }
        val lang = _selectedLanguage.value
        val paragraphs = if (lang == TypingLanguage.BANGLA) {
            LessonDataSource.speedTestParagraphsBangla
        } else {
            LessonDataSource.speedTestParagraphsEnglish
        }
        val chosen = if (chosenTitle != null && chosenTitle.isNotBlank() && chosenTitle != "RANDOM") {
            paragraphs.find { it.first == chosenTitle } ?: paragraphs.first()
        } else {
            paragraphs.random()
        }
        _selectedMode.value = TypingMode.SPEED_TEST
        startSession(
            title = chosen.first,
            text = chosen.second,
            timeLimitSecs = durationSecs
        )
    }

    fun startCustomPractice(title: String, customText: String, timeLimitSecs: Int?) {
        _selectedMode.value = TypingMode.CUSTOM
        startSession(
            title = title.ifBlank { "Custom Practice" },
            text = customText.trim(),
            timeLimitSecs = timeLimitSecs
        )
    }

    fun startSession(title: String, text: String, timeLimitSecs: Int?) {
        timerJob?.cancel()
        _isTimerRunning.value = false
        _sessionTitle.value = title
        _targetText.value = text.replace("\r\n", " ").replace("\n", " ").trim()
        _userInput.value = ""
        com.example.data.bijoy.BijoyLayoutEngine.resetState()
        _elapsedSeconds.value = 0
        _timeLimitSeconds.value = timeLimitSecs
        _isSessionActive.value = true
        _isSessionFinished.value = false
        _liveWpm.value = 0
        _liveAccuracy.value = 100f
        _mistakeCount.value = 0
        _correctWordsCount.value = 0
        _incorrectWordsCount.value = 0
        _liveErrorRatePercent.value = 0f
        _isLiveFailingGovtRule.value = false
        _lastCompletedResult.value = null
        sessionStartTimeMs = 0L

        // Timer will start on candidate's first keypress
    }

    fun ensureTimerStarted() {
        if (!_isTimerRunning.value && _isSessionActive.value && !_isSessionFinished.value) {
            _isTimerRunning.value = true
            sessionStartTimeMs = System.currentTimeMillis()
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isSessionActive.value && !_isSessionFinished.value) {
                delay(1000)
                _elapsedSeconds.value += 1

                val limit = _timeLimitSeconds.value
                if (limit != null && _elapsedSeconds.value >= limit) {
                    finishSession()
                    break
                }
                updateLiveMetrics()
            }
        }
    }

    fun onUserInputChange(newText: String) {
        if (!_isSessionActive.value || _isSessionFinished.value) return
        ensureTimerStarted()

        val target = _targetText.value
        // If in Paper Mode, do not cap by target.length - allow full continuous transcription from physical paper
        val sanitized = if (isPaperMode.value || target.isEmpty()) {
            newText
        } else {
            if (newText.length > target.length) newText.take(target.length) else newText
        }

        // Play subtle haptic feedback on keypress
        if (hapticEnabled.value && sanitized.length > _userInput.value.length) {
            triggerKeyHaptic()
        }

        _userInput.value = sanitized
        updateLiveMetrics()

        // Check if finished by reaching full text length (only in standard on-screen sample mode)
        if (!isPaperMode.value && target.isNotEmpty() && sanitized.length >= target.length) {
            finishSession()
        }
    }

    fun handlePhysicalKey(keyChar: Char) {
        if (!_isSessionActive.value || _isSessionFinished.value) return
        ensureTimerStarted()

        if (keyChar == '\n') {
            onUserInputChange(_userInput.value + "\n")
            return
        }

        // If the character is already a Bengali Unicode character (from soft keyboard, IME, or Bengali physical layout)
        if (keyChar in '\u0980'..'\u09FF') {
            onUserInputChange(_userInput.value + keyChar)
            return
        }

        if (_selectedLanguage.value == TypingLanguage.BANGLA && inputLayoutMode.value == com.example.data.model.InputLayoutMode.BIJOY) {
            val newText = com.example.data.bijoy.BijoyLayoutEngine.processKeystroke(_userInput.value, keyChar)
            onUserInputChange(newText)
        } else {
            onUserInputChange(_userInput.value + keyChar)
        }
    }

    fun handleBackspace() {
        if (!_isSessionActive.value || _isSessionFinished.value) return
        ensureTimerStarted()
        if (_userInput.value.isNotEmpty()) {
            _userInput.value = _userInput.value.dropLast(1)
            com.example.data.bijoy.BijoyLayoutEngine.onBackspace()
            updateLiveMetrics()
            if (hapticEnabled.value) {
                triggerKeyHaptic()
            }
        }
    }

    fun onPhysicalKeyEvent(event: android.view.KeyEvent): Boolean {
        if (!_isSessionActive.value || _isSessionFinished.value) return false
        if (event.action == android.view.KeyEvent.ACTION_DOWN) {
            ensureTimerStarted()
        }

        val keyCode = event.keyCode

        if (keyCode == android.view.KeyEvent.KEYCODE_DEL) {
            handleBackspace()
            return true
        }

        if (keyCode == android.view.KeyEvent.KEYCODE_SPACE) {
            if (_selectedLanguage.value == TypingLanguage.BANGLA && inputLayoutMode.value == com.example.data.model.InputLayoutMode.BIJOY) {
                handlePhysicalKey(' ')
            } else {
                onUserInputChange(_userInput.value + " ")
            }
            return true
        }

        if (keyCode == android.view.KeyEvent.KEYCODE_ENTER || keyCode == android.view.KeyEvent.KEYCODE_NUMPAD_ENTER) {
            onUserInputChange(_userInput.value + "\n")
            return true
        }

        val unicodeChar = event.getUnicodeChar(event.metaState)
        if (unicodeChar != 0) {
            val char = unicodeChar.toChar()
            val isBengali = char in '\u0980'..'\u09FF'
            val isAsciiOrSymbol = char in 'a'..'z' || char in 'A'..'Z' || char in "0123456789.,;:'\"/\\`~!@#$%^&*()-=_+[]{}|<>?\n "
            if (isBengali || isAsciiOrSymbol || (!Character.isISOControl(char) && char != '\u0000')) {
                if (isBengali) {
                    onUserInputChange(_userInput.value + char)
                } else if (_selectedLanguage.value == TypingLanguage.BANGLA && inputLayoutMode.value == com.example.data.model.InputLayoutMode.BIJOY) {
                    handlePhysicalKey(char)
                } else {
                    onUserInputChange(_userInput.value + char)
                }
                return true
            }
        }

        return false
    }

    private fun triggerKeyHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(12)
            }
        } catch (_: Exception) { }
    }

    private fun updateLiveMetrics() {
        val target = _targetText.value
        val input = _userInput.value
        val totalTyped = input.length
        val elapsedSec = _elapsedSeconds.value.coerceAtLeast(1)
        val minutes = elapsedSec / 60.0f

        val normTarget = java.text.Normalizer.normalize(target, java.text.Normalizer.Form.NFC)
        val normInput = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFC)

        // 1. Word Analysis: ONLY analyze words typed so far (never penalize untyped text)
        val targetWords = if (normTarget.isBlank()) emptyList() else normTarget.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        val typedWords = if (normInput.isBlank()) emptyList() else normInput.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }

        var correctWords = 0
        var incorrectWords = 0

        for (i in typedWords.indices) {
            val typedW = typedWords[i]
            if (i < targetWords.size) {
                val targetW = targetWords[i]
                if (typedW == targetW) {
                    correctWords++
                } else {
                    val isLastWordBeingTyped = (i == typedWords.size - 1) && !input.endsWith(" ") && !input.endsWith("\n")
                    if (isLastWordBeingTyped && targetW.startsWith(typedW)) {
                        // Word in progress of being typed correctly
                    } else {
                        incorrectWords++
                    }
                }
            } else {
                incorrectWords++
            }
        }

        _correctWordsCount.value = correctWords
        _incorrectWordsCount.value = incorrectWords

        // Bangladesh Govt 5% Error Rule calculation
        val totalWordsTyped = correctWords + incorrectWords
        val errorRate = if (totalWordsTyped > 0) {
            ((incorrectWords.toFloat() / totalWordsTyped.toFloat()) * 100f).coerceIn(0f, 100f)
        } else {
            0f
        }
        _liveErrorRatePercent.value = errorRate
        _isLiveFailingGovtRule.value = (errorRate > 5.0f && totalWordsTyped >= 3)

        // 2. Character mistakes analysis (strictly for typed portion)
        var mistakes = 0
        val evalLen = minOf(normTarget.length, normInput.length)
        for (i in 0 until evalLen) {
            if (normTarget[i] != normInput[i]) {
                mistakes++
            }
        }
        if (normInput.length > normTarget.length) {
            mistakes += (normInput.length - normTarget.length)
        }
        _mistakeCount.value = mistakes

        // 3. WPM calculation: Pure typing pace, mistakes have NO negative effect on WPM
        // "শব্দ ভুল থাকলেও WPM বিশ্লেষণে কোনো প্রভাব পড়বে না"
        val wpm = if (minutes > 0f && totalTyped > 0) {
            ((totalTyped / 5.0f) / minutes).toInt().coerceAtLeast(0)
        } else {
            0
        }
        _liveWpm.value = wpm

        // 4. Accuracy calculation: based strictly on what has been typed so far
        val correctChars = (totalTyped - mistakes).coerceAtLeast(0)
        val accuracy = if (totalTyped > 0) {
            ((correctChars.toFloat() / totalTyped.toFloat()) * 100f).coerceIn(0f, 100f)
        } else {
            100f
        }
        _liveAccuracy.value = accuracy
    }

    fun finishSession() {
        if (_isSessionFinished.value) return
        timerJob?.cancel()
        _isTimerRunning.value = false
        _isSessionActive.value = false
        _isSessionFinished.value = true

        updateLiveMetrics()

        val duration = _elapsedSeconds.value.coerceAtLeast(1)
        val totalChars = _userInput.value.length
        val mistakes = _mistakeCount.value
        val correctChars = (totalChars - mistakes).coerceAtLeast(0)
        val cpm = ((correctChars / duration.toFloat()) * 60f).toInt()
        val wpm = _liveWpm.value
        val accuracy = _liveAccuracy.value
        val correctWords = _correctWordsCount.value
        val incorrectWords = _incorrectWordsCount.value

        // Official Bangladesh Govt Typing Test Rules (২০২৪ সালের বিভাগীয় কমিশনার অফিসের নিয়োগবিধির সংশোধিত গেজেট):
        // 1. ৫টি কী-স্ট্রোক = ১টি শব্দ (Word = Keystrokes / 5)
        // 2. মিনিটে ২০ WPM অর্জনের জন্য প্রয়োজনীয় কী-স্ট্রোক: ২০ × ৫ = ১০০টি
        // 3. ৫% ভুলের নিয়ম: ৫%-এর বেশি ভুল শব্দ হলে প্রয়োজনীয় গতি অর্জিত হয়নি বলে গণ্য হবে (ফেল / FAILED)
        // 4. ভুল শব্দ ৫% বা তার কম এবং ন্যূনতম গতি (২০ WPM) অর্জিত হলে কৃতকার্য (PASSED)
        val totalWords = correctWords + incorrectWords
        val errorRate = if (totalWords > 0) {
            ((incorrectWords.toFloat() / totalWords.toFloat()) * 100f).coerceIn(0f, 100f)
        } else {
            0f
        }
        val requiredTargetWpm = 20
        val isPassed = (errorRate <= 5.0f) && (wpm >= requiredTargetWpm)
        val failureReason = when {
            errorRate > 5.0f -> "ভুলের হার ৫%-এর বেশি (${String.format(Locale.US, "%.1f", errorRate)}%) — ২০২৪ গেজেট অনুযায়ী গতি অর্জিত হয়নি (ফেল)"
            wpm < requiredTargetWpm -> "গতি ন্যূনতম ২০ WPM-এর কম (${wpm} WPM)"
            else -> ""
        }

        val result = TypingResult(
            timestamp = System.currentTimeMillis(),
            language = _selectedLanguage.value.name,
            mode = _selectedMode.value.name,
            title = _sessionTitle.value,
            wpm = wpm,
            cpm = cpm,
            accuracy = accuracy,
            durationSeconds = duration,
            totalCharsTyped = totalChars,
            correctChars = correctChars,
            mistakeCount = mistakes,
            correctWords = correctWords,
            incorrectWords = incorrectWords,
            errorRatePercent = errorRate,
            isPassedGovtRule = isPassed,
            failureReason = failureReason
        )

        _lastCompletedResult.value = result

        // Trigger Interstitial Ad if not Ad-Free
        if (!adManager.adStats.value.isAdFree) {
            showInterstitialAd.value = true
            adManager.recordInterstitialImpression()
        }

        // Save into Room Database and sync to Firestore Leaderboard & User Storage
        viewModelScope.launch {
            try {
                repository.saveResult(result)
            } catch (e: Throwable) {
                android.util.Log.e("TypingViewModel", "saveResult error: ${e.message}")
            }
            try {
                val user = authRepository.currentUser
                if (user != null) {
                    userDataSyncRepository.uploadResult(user.uid, result)
                    if (result.wpm > 0) {
                        leaderboardRepository.recordPersonalBest(
                            wpm = result.wpm,
                            accuracy = result.accuracy,
                            language = result.language
                        )
                    }
                }
            } catch (e: Throwable) {
                android.util.Log.e("TypingViewModel", "sync cloud error: ${e.message}")
            }
        }
    }

    fun dismissResultDialog() {
        _lastCompletedResult.value = null
    }

    fun resetSession() {
        startSession(_sessionTitle.value, _targetText.value, _timeLimitSeconds.value)
    }

    fun deleteResult(id: Long) {
        viewModelScope.launch {
            repository.deleteResult(id)
        }
    }

    fun clearAllResults() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    fun getCurrentAppVersion(): Pair<Long, String> {
        return updateRepository.getCurrentVersion()
    }

    fun checkForUpdate(isManual: Boolean = false) {
        viewModelScope.launch {
            try {
                isCheckingForUpdate.value = true
                if (isManual) {
                    updateCheckStatusMessage.value = "আপডেট চেক করা হচ্ছে..."
                }
                val info = updateRepository.fetchUpdateInfo()
                if (info.isUpdateAvailable) {
                    appUpdateInfo.value = info
                    val formattedLatest = if (info.latestVersionName.startsWith("v", ignoreCase = true) || info.latestVersionName.startsWith("Build", ignoreCase = true)) {
                        info.latestVersionName
                    } else {
                        "v${info.latestVersionName}"
                    }
                    updateCheckStatusMessage.value = "নতুন ভার্সন পাওয়া গেছে: $formattedLatest"
                } else {
                    if (isManual) {
                        updateCheckStatusMessage.value = "আপনার অ্যাপটি আপ-টু-ডেট আছে (ভার্সন v${info.currentVersionName}, বিল্ড ${info.currentVersionCode})"
                    }
                }
            } catch (e: Exception) {
                if (isManual) {
                    updateCheckStatusMessage.value = "আপডেট চেক করতে সমস্যা হয়েছে: ইন্টারনেট সংযোগ চেক করুন"
                }
            } finally {
                isCheckingForUpdate.value = false
            }
        }
    }

    fun dismissUpdateDialog() {
        appUpdateInfo.value = null
        resetDownloadState()
    }

    fun startDownloadUpdate(url: String) {
        downloadJob?.cancel()
        downloadJob = viewModelScope.launch {
            val targetUrl = url.ifBlank {
                "https://github.com/shimulshekh311-cmd/Typing-Master/releases/latest/download/app-debug.apk"
            }
            inAppUpdateDownloader.downloadApk(targetUrl) { state ->
                updateDownloadState.value = state
            }
        }
    }

    fun installDownloadedApk(context: android.content.Context) {
        val state = updateDownloadState.value
        val apkFile = when (state) {
            is com.example.data.repository.UpdateDownloadState.ReadyToInstall -> state.apkFile
            else -> inAppUpdateDownloader.getDestinationApkFile().takeIf { it.exists() }
        }
        if (apkFile != null && apkFile.exists()) {
            inAppUpdateDownloader.installApk(apkFile)
        } else {
            android.widget.Toast.makeText(context, "ইনস্টলার ফাইল পাওয়া যায়নি, অনুগ্রহ করে পুনরায় ডাউনলোড করুন", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    fun resetDownloadState() {
        downloadJob?.cancel()
        updateDownloadState.value = com.example.data.repository.UpdateDownloadState.Idle
    }

    fun simulateUpdateDialogForTesting() {
        val (currentCode, currentName) = getCurrentAppVersion()
        resetDownloadState()
        appUpdateInfo.value = com.example.data.model.AppUpdateInfo(
            currentVersionCode = currentCode,
            currentVersionName = currentName,
            latestVersionCode = currentCode + 1,
            latestVersionName = "Build #32",
            updateUrl = "https://github.com/shimulshekh311-cmd/Typing-Master/releases/download/debug-apk-build-32/app-debug.apk",
            releaseNotes = "১. নতুন ইন-অ্যাপ ডাউনলোড ও প্রগ্রেস বার\n২. ডাউনলোড শেষে এক ক্লিকে Install Now\n৩. নতুন স্পিড টেস্ট ড্যাশবোর্ড আপডেট",
            isForceUpdate = false,
            isUpdateAvailable = true
        )
    }

    fun publishRemoteVersion(
        latestCode: Long,
        latestName: String,
        updateUrl: String,
        releaseNotes: String,
        isForceUpdate: Boolean = false
    ) {
        viewModelScope.launch {
            val success = updateRepository.setRemoteVersionConfig(
                latestCode = latestCode,
                latestName = latestName,
                updateUrl = updateUrl,
                releaseNotes = releaseNotes,
                isForceUpdate = isForceUpdate
            )
            if (success) {
                updateCheckStatusMessage.value = "ক্লাউডে নতুন ভার্সন $latestName সফলভাবে সেভ করা হয়েছে"
                checkForUpdate(isManual = true)
            } else {
                updateCheckStatusMessage.value = "ভার্সন সেভ করা যায়নি। নেটওয়ার্ক চেক করুন।"
            }
        }
    }
}
