package com.example.whitenoises.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SleepTimerManager(
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _remainingMillis = MutableStateFlow(0L)
    val remainingMillis: StateFlow<Long> = _remainingMillis.asStateFlow()

    private val _totalMillis = MutableStateFlow(0L)
    val totalMillis: StateFlow<Long> = _totalMillis.asStateFlow()

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    private val _isFadingOut = MutableStateFlow(false)
    val isFadingOut: StateFlow<Boolean> = _isFadingOut.asStateFlow()

    private var timerJob: Job? = null

    fun startTimer(
        durationMinutes: Int,
        fadeOutSeconds: Int = 10,
        onVolumeFade: ((Float) -> Unit)? = null,
        onFinish: () -> Unit
    ) {
        cancelTimer()

        val totalMs = durationMinutes * 60 * 1000L
        if (totalMs <= 0L) return

        _totalMillis.value = totalMs
        _remainingMillis.value = totalMs
        _isActive.value = true
        _isFadingOut.value = false

        timerJob = coroutineScope.launch {
            var currentRemaining = totalMs
            val tickInterval = 1000L
            val fadeOutMs = fadeOutSeconds * 1000L

            while (currentRemaining > 0L) {
                delay(tickInterval)
                currentRemaining = (currentRemaining - tickInterval).coerceAtLeast(0L)
                _remainingMillis.value = currentRemaining

                if (currentRemaining in 1..fadeOutMs) {
                    _isFadingOut.value = true
                    val fadeFactor = (currentRemaining.toFloat() / fadeOutMs.toFloat()).coerceIn(0f, 1f)
                    onVolumeFade?.invoke(fadeFactor)
                }
            }

            _isActive.value = false
            _isFadingOut.value = false
            _remainingMillis.value = 0L
            _totalMillis.value = 0L

            onFinish()
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        _isActive.value = false
        _isFadingOut.value = false
        _remainingMillis.value = 0L
        _totalMillis.value = 0L
    }
}
