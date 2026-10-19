package com.example.whitenoises.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.whitenoises.model.PlaybackState
import com.example.whitenoises.model.SoundCategory
import com.example.whitenoises.model.SoundItem
import com.example.whitenoises.repository.AudioRepository
import com.example.whitenoises.repository.SoundRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class MainUiState(
    val selectedCategory: SoundCategory = SoundCategory.ALL,
    val sounds: List<SoundItem> = emptyList(),
    val playbackState: PlaybackState = PlaybackState(),
    val isTimerDialogOpen: Boolean = false,
)

class MainViewModel(
    private val soundRepository: SoundRepository,
    private val audioRepository: AudioRepository,
) : ViewModel() {

    private val initialCategory = if (soundRepository.getTotalPlays() >= 3) {
        SoundCategory.MOST_USED
    } else {
        SoundCategory.ALL
    }

    private val _selectedCategory = MutableStateFlow(initialCategory)
    val selectedCategory: StateFlow<SoundCategory> = _selectedCategory.asStateFlow()

    private val _isTimerDialogOpen = MutableStateFlow(value = false)
    val isTimerDialogOpen: StateFlow<Boolean> = _isTimerDialogOpen.asStateFlow()

    val playbackState: StateFlow<PlaybackState> = audioRepository.playbackState

    val uiState: StateFlow<MainUiState> = combine(
        _selectedCategory,
        audioRepository.playbackState,
        _isTimerDialogOpen
    ) { category, playback, timerDialogOpen ->
        MainUiState(
            selectedCategory = category,
            sounds = soundRepository.getSoundsByCategory(category),
            playbackState = playback,
            isTimerDialogOpen = timerDialogOpen,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState(
            selectedCategory = initialCategory,
            sounds = soundRepository.getSoundsByCategory(initialCategory)
        )
    )

    fun selectCategory(category: SoundCategory) {
        _selectedCategory.value = category
    }

    fun onSoundClick(soundItem: SoundItem) {
        val currentSound = playbackState.value.currentSound
        val isPlaying = playbackState.value.isPlaying

        if (currentSound?.id == soundItem.id) {
            if (isPlaying) {
                audioRepository.pause()
            } else {
                audioRepository.resume()
            }
        } else {
            soundRepository.recordSoundPlay(soundItem.id)
            audioRepository.play(soundItem)
        }
    }

    fun play() {
        if (playbackState.value.currentSound != null) {
            audioRepository.resume()
        } else {
            soundRepository.getAvailableSounds().firstOrNull()?.let { firstSound ->
                audioRepository.play(firstSound)
            }
        }
    }

    fun pause() {
        audioRepository.pause()
    }

    fun togglePlayPause() {
        audioRepository.togglePlayPause()
    }

    fun stop() {
        audioRepository.stop()
    }

    fun setVolume(volume: Float) {
        audioRepository.setVolume(volume)
    }

    fun startSleepTimer(durationMinutes: Int) {
        audioRepository.startSleepTimer(durationMinutes)
        _isTimerDialogOpen.value = false
    }

    fun cancelSleepTimer() {
        audioRepository.cancelSleepTimer()
        _isTimerDialogOpen.value = false
    }

    fun setTimerDialogOpen(isOpen: Boolean) {
        _isTimerDialogOpen.value = isOpen
    }

    override fun onCleared() {
        audioRepository.release()
    }
}

class MainViewModelFactory(
    private val soundRepository: SoundRepository,
    private val audioRepository: AudioRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(soundRepository, audioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
