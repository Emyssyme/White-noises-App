package com.example.whitenoises.ui

import com.example.whitenoises.model.PlaybackState
import com.example.whitenoises.model.SoundCategory
import com.example.whitenoises.model.SoundItem
import com.example.whitenoises.repository.AudioRepository
import com.example.whitenoises.repository.SoundRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var soundRepository: SoundRepositoryImpl
    private lateinit var fakeAudioRepository: FakeAudioRepository
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        soundRepository = SoundRepositoryImpl()
        fakeAudioRepository = FakeAudioRepository()
        viewModel = MainViewModel(soundRepository, fakeAudioRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState has default values`() = runTest {
        val collectJob = backgroundScope.launch(testDispatcher) {
            viewModel.uiState.collect {}
        }
        val state = viewModel.uiState.value
        assertEquals(SoundCategory.ALL, state.selectedCategory)
        assertTrue(state.sounds.isNotEmpty())
        assertFalse(state.isTimerDialogOpen)
        assertFalse(state.playbackState.isPlaying)
        collectJob.cancel()
    }

    @Test
    fun `selectCategory filters sounds correctly`() = runTest {
        val collectJob = backgroundScope.launch(testDispatcher) {
            viewModel.uiState.collect {}
        }

        viewModel.selectCategory(SoundCategory.NOISE)

        val state = viewModel.uiState.value
        assertEquals(SoundCategory.NOISE, state.selectedCategory)
        assertTrue(state.sounds.all { it.category == SoundCategory.NOISE })

        collectJob.cancel()
    }

    @Test
    fun `onSoundClick plays new sound`() = runTest {
        val sound = soundRepository.getAvailableSounds().first()
        viewModel.onSoundClick(sound)

        assertEquals(sound, fakeAudioRepository.playCalledWith)
        assertEquals(sound, viewModel.playbackState.value.currentSound)
        assertTrue(viewModel.playbackState.value.isPlaying)
    }

    @Test
    fun `onSoundClick pauses currently playing sound`() = runTest {
        val sound = soundRepository.getAvailableSounds().first()
        viewModel.onSoundClick(sound) // Play
        viewModel.onSoundClick(sound) // Pause

        assertTrue(fakeAudioRepository.pauseCalled)
        assertFalse(viewModel.playbackState.value.isPlaying)
    }

    @Test
    fun `setVolume delegates to audio repository`() = runTest {
        viewModel.setVolume(0.5f)
        assertEquals(0.5f, fakeAudioRepository.volumeSet)
        assertEquals(0.5f, viewModel.playbackState.value.volume)
    }

    @Test
    fun `startSleepTimer triggers timer and closes dialog`() = runTest {
        val collectJob = backgroundScope.launch(testDispatcher) {
            viewModel.uiState.collect {}
        }

        viewModel.setTimerDialogOpen(true)
        viewModel.startSleepTimer(15)

        assertEquals(15, fakeAudioRepository.timerStartedMinutes)
        assertFalse(viewModel.uiState.value.isTimerDialogOpen)

        collectJob.cancel()
    }

    @Test
    fun `cancelSleepTimer cancels timer and closes dialog`() = runTest {
        val collectJob = backgroundScope.launch(testDispatcher) {
            viewModel.uiState.collect {}
        }

        viewModel.setTimerDialogOpen(true)
        viewModel.cancelSleepTimer()

        assertTrue(fakeAudioRepository.timerCanceled)
        assertFalse(viewModel.uiState.value.isTimerDialogOpen)

        collectJob.cancel()
    }

    @Test
    fun `stop stops audio playback`() = runTest {
        val sound = soundRepository.getAvailableSounds().first()
        viewModel.onSoundClick(sound)
        viewModel.stop()

        assertTrue(fakeAudioRepository.stopCalled)
        assertFalse(viewModel.playbackState.value.isPlaying)
    }
}

class FakeAudioRepository : AudioRepository {
    private val _playbackState = MutableStateFlow(PlaybackState())
    override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    var playCalledWith: SoundItem? = null
    var pauseCalled = false
    var resumeCalled = false
    var stopCalled = false
    var volumeSet: Float? = null
    var timerStartedMinutes: Int? = null
    var timerCanceled = false
    var released = false

    override fun play(soundItem: SoundItem) {
        playCalledWith = soundItem
        _playbackState.value = _playbackState.value.copy(
            isPlaying = true,
            currentSound = soundItem
        )
    }

    override fun pause() {
        pauseCalled = true
        _playbackState.value = _playbackState.value.copy(isPlaying = false)
    }

    override fun resume() {
        resumeCalled = true
        _playbackState.value = _playbackState.value.copy(isPlaying = true)
    }

    override fun togglePlayPause() {
        if (playbackState.value.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    override fun stop() {
        stopCalled = true
        _playbackState.value = PlaybackState()
    }

    override fun setVolume(volume: Float) {
        volumeSet = volume
        _playbackState.value = _playbackState.value.copy(volume = volume)
    }

    override fun startSleepTimer(durationMinutes: Int) {
        timerStartedMinutes = durationMinutes
        _playbackState.value = _playbackState.value.copy(isSleepTimerActive = true)
    }

    override fun cancelSleepTimer() {
        timerCanceled = true
        _playbackState.value = _playbackState.value.copy(isSleepTimerActive = false)
    }

    override fun release() {
        released = true
    }
}
