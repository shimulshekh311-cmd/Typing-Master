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

class TypingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TypingRepository
    private val subscriptionManager = SubscriptionManager.getInstance(application)
    private val adManager = AdManager.getInstance(application)
    private val authRepository = com.example.data.repository.AuthRepository(application)
    private val leaderboardRepository = com.example.data.repository.LeaderboardRepository(application)
    private val vibrator = application.getSystemService(Vibrator::class.java)

    val subscriptionState: StateFlow<SubscriptionState> = subscriptionManager.subscriptionState
    val adStats: StateFlow<AdStats> = adManager.adStats
    val currentBannerCreative: StateFlow<AdCreative> = adManager.currentBannerCreative
    val showSubscriptionDialog = MutableStateFlow(false)
    val showInterstitialAd = MutableStateFlow(false)

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
                authMessage.value = "${user.displayName ?: "ব্যবহারকারী"} হিসেবে সফলভাবে লগইন হয়েছে!"
                // Sync any peak scores from local history
                syncBestLocalScoreToLeaderboard()
            }.onFailure { err ->
                if (err !is androidx.credentials.exceptions.GetCredentialCancellationException) {
                    authMessage.value = "লগইন ব্যর্থ হয়েছে: ${err.localizedMessage ?: "অজানা ত্রুটি"}"
                }
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        authMessage.value = "লগআউট সম্পন্ন হয়েছে"
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
            if (char in 'a'..'z' || char in 'A'..'Z' || char in "0123456789.,;:'\"/\\`~!@#$%^&*()-=_+[]{}|<>?\n") {
                if (_selectedLanguage.value == TypingLanguage.BANGLA && inputLayoutMode.value == com.example.data.model.InputLayoutMode.BIJOY) {
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

        if (isPaperMode.value) {
            // Paper Copy Mode: transcribe from physical paper
            var mistakes = 0
            if (normTarget.isNotEmpty()) {
                val minLen = minOf(normTarget.length, normInput.length)
                for (i in 0 until minLen) {
                    if (normTarget[i] != normInput[i]) {
                        mistakes++
                    }
                }
            }
            _mistakeCount.value = mistakes

            val correctChars = (totalTyped - mistakes).coerceAtLeast(0)
            val accuracy = if (totalTyped > 0) {
                if (normTarget.isNotEmpty()) {
                    ((correctChars.toFloat() / totalTyped.toFloat()) * 100f).coerceIn(0f, 100f)
                } else {
                    100f
                }
            } else {
                100f
            }
            _liveAccuracy.value = accuracy

            val wpm = if (minutes > 0f) {
                ((totalTyped / 5.0f) / minutes).toInt().coerceAtLeast(0)
            } else {
                0
            }
            _liveWpm.value = wpm
        } else {
            var mistakes = 0
            val minLen = minOf(normTarget.length, normInput.length)
            for (i in 0 until minLen) {
                if (normTarget[i] != normInput[i]) {
                    mistakes++
                }
            }
            if (normInput.length > normTarget.length) {
                mistakes += (normInput.length - normTarget.length)
            }
            _mistakeCount.value = mistakes

            val correctChars = (totalTyped - mistakes).coerceAtLeast(0)

            // Accuracy calculation
            val accuracy = if (totalTyped > 0) {
                ((correctChars.toFloat() / totalTyped.toFloat()) * 100f).coerceIn(0f, 100f)
            } else {
                100f
            }
            _liveAccuracy.value = accuracy

            // WPM calculation: 1 word = 5 characters
            val wpm = if (minutes > 0f) {
                ((correctChars / 5.0f) / minutes).toInt().coerceAtLeast(0)
            } else {
                0
            }
            _liveWpm.value = wpm
        }
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
            mistakeCount = mistakes
        )

        _lastCompletedResult.value = result

        // Trigger Interstitial Ad if not Ad-Free
        if (!adManager.adStats.value.isAdFree) {
            showInterstitialAd.value = true
            adManager.recordInterstitialImpression()
        }

        // Save into Room Database and sync to Firestore Leaderboard
        viewModelScope.launch {
            try {
                repository.saveResult(result)
            } catch (e: Throwable) {
                android.util.Log.e("TypingViewModel", "saveResult error: ${e.message}")
            }
            try {
                if (result.wpm > 0 && authRepository.currentUser != null) {
                    leaderboardRepository.recordPersonalBest(
                        wpm = result.wpm,
                        accuracy = result.accuracy,
                        language = result.language
                    )
                }
            } catch (e: Throwable) {
                android.util.Log.e("TypingViewModel", "recordPersonalBest error: ${e.message}")
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
}
