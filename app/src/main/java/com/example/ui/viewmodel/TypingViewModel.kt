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
    private val vibrator = application.getSystemService(Vibrator::class.java)

    val subscriptionState: StateFlow<SubscriptionState> = subscriptionManager.subscriptionState
    val showSubscriptionDialog = MutableStateFlow(false)

    fun openSubscriptionDialog() {
        showSubscriptionDialog.value = true
    }

    fun closeSubscriptionDialog() {
        showSubscriptionDialog.value = false
    }

    fun canAccessPractice(): Boolean {
        return subscriptionManager.canAccessApp()
    }

    fun activateSubscription(
        trxId: String,
        senderPhone: String,
        method: PaymentMethod
    ): Pair<Boolean, String> {
        return subscriptionManager.activateSubscriptionWithTrxId(trxId, senderPhone, method)
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
    val virtualKeyboardEnabled = MutableStateFlow(true)

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

    fun startSpeedTest(durationSecs: Int, difficulty: String) {
        val lang = _selectedLanguage.value
        val paragraphs = if (lang == TypingLanguage.BANGLA) {
            LessonDataSource.speedTestParagraphsBangla
        } else {
            LessonDataSource.speedTestParagraphsEnglish
        }
        val chosen = paragraphs.random()
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
        _sessionTitle.value = title
        _targetText.value = text.replace("\r\n", " ").replace("\n", " ").trim()
        _userInput.value = ""
        _elapsedSeconds.value = 0
        _timeLimitSeconds.value = timeLimitSecs
        _isSessionActive.value = true
        _isSessionFinished.value = false
        _liveWpm.value = 0
        _liveAccuracy.value = 100f
        _mistakeCount.value = 0
        _lastCompletedResult.value = null
        sessionStartTimeMs = System.currentTimeMillis()

        startTimer()
    }

    private fun startTimer() {
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
        if (_userInput.value.isNotEmpty()) {
            _userInput.value = _userInput.value.dropLast(1)
            updateLiveMetrics()
            if (hapticEnabled.value) {
                triggerKeyHaptic()
            }
        }
    }

    fun onPhysicalKeyEvent(event: android.view.KeyEvent): Boolean {
        if (!_isSessionActive.value || _isSessionFinished.value) return false

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

        if (isPaperMode.value) {
            // Paper Copy Mode: transcribe from physical paper
            var mistakes = 0
            if (target.isNotEmpty()) {
                val minLen = minOf(target.length, input.length)
                for (i in 0 until minLen) {
                    if (target[i] != input[i]) {
                        mistakes++
                    }
                }
            }
            _mistakeCount.value = mistakes

            val correctChars = (totalTyped - mistakes).coerceAtLeast(0)
            val accuracy = if (totalTyped > 0) {
                if (target.isNotEmpty()) {
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
            val minLen = minOf(target.length, input.length)
            for (i in 0 until minLen) {
                if (target[i] != input[i]) {
                    mistakes++
                }
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

        // Save into Room Database
        viewModelScope.launch {
            repository.saveResult(result)
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
