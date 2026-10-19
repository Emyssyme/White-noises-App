package com.example.whitenoises.model

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentSound: SoundItem? = null,
    val volume: Float = 0.8f,
    val sleepTimerRemainingMillis: Long = 0L,
    val sleepTimerTotalMillis: Long = 0L,
    val isSleepTimerActive: Boolean = false,
    val isFadingOut: Boolean = false
) {
    val progress: Float
        get() = if (sleepTimerTotalMillis > 0L) {
            (sleepTimerRemainingMillis.toFloat() / sleepTimerTotalMillis.toFloat()).coerceIn(0f, 1f)
        } else 0f
}
