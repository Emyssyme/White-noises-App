package com.example.whitenoises.repository

import com.example.whitenoises.audio.AudioPlayerEngine
import com.example.whitenoises.audio.SleepTimerManager
import com.example.whitenoises.model.PlaybackState
import com.example.whitenoises.model.SoundItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

interface AudioRepository {
    val playbackState: StateFlow<PlaybackState>

    fun play(soundItem: SoundItem)
    fun pause()
    fun resume()
    fun togglePlayPause()
    fun stop()
    fun setVolume(volume: Float)
    fun startSleepTimer(durationMinutes: Int)
    fun cancelSleepTimer()
    fun release()
}

class AudioRepositoryImpl(
    private val audioPlayerEngine: AudioPlayerEngine,
    private val sleepTimerManager: SleepTimerManager = SleepTimerManager(),
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : AudioRepository {

    private var baseVolume: Float = 0.8f

    private val playerInfoFlow = combine(
        audioPlayerEngine.isPlaying,
        audioPlayerEngine.currentSound,
        audioPlayerEngine.volume
    ) { isPlaying, currentSound, volume ->
        Triple(isPlaying, currentSound, volume)
    }

    private val timerInfoFlow = combine(
        sleepTimerManager.remainingMillis,
        sleepTimerManager.totalMillis,
        sleepTimerManager.isActive,
        sleepTimerManager.isFadingOut
    ) { remainingMs, totalMs, isActive, isFading ->
        Quad(remainingMs, totalMs, isActive, isFading)
    }

    override val playbackState: StateFlow<PlaybackState> = combine(
        playerInfoFlow,
        timerInfoFlow
    ) { playerInfo, timerInfo ->
        PlaybackState(
            isPlaying = playerInfo.first,
            currentSound = playerInfo.second,
            volume = playerInfo.third,
            sleepTimerRemainingMillis = timerInfo.first,
            sleepTimerTotalMillis = timerInfo.second,
            isSleepTimerActive = timerInfo.third,
            isFadingOut = timerInfo.fourth
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlaybackState()
    )

    override fun play(soundItem: SoundItem) {
        baseVolume = soundItem.defaultVolume
        audioPlayerEngine.playSound(soundItem, baseVolume)
    }

    override fun pause() {
        audioPlayerEngine.pause()
    }

    override fun resume() {
        if (playbackState.value.currentSound != null) {
            audioPlayerEngine.resume()
        }
    }

    override fun togglePlayPause() {
        if (playbackState.value.isPlaying) {
            pause()
        } else if (playbackState.value.currentSound != null) {
            resume()
        }
    }

    override fun stop() {
        audioPlayerEngine.stop()
        sleepTimerManager.cancelTimer()
    }

    override fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        baseVolume = clamped
        audioPlayerEngine.setVolume(clamped)
    }

    override fun startSleepTimer(durationMinutes: Int) {
        sleepTimerManager.startTimer(
            durationMinutes = durationMinutes,
            fadeOutSeconds = 10,
            onVolumeFade = { fadeFactor ->
                audioPlayerEngine.setVolume(baseVolume * fadeFactor)
            },
            onFinish = {
                stop()
            }
        )
    }

    override fun cancelSleepTimer() {
        sleepTimerManager.cancelTimer()
        audioPlayerEngine.setVolume(baseVolume)
    }

    override fun release() {
        sleepTimerManager.cancelTimer()
        audioPlayerEngine.release()
    }

    private data class Quad<A, B, C, D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
    )
}
