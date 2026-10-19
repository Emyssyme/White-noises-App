package com.example.whitenoises.audio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.whitenoises.model.SoundItem
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.CompletableFuture

class AudioPlayerEngine(private val context: Context) {
    private var mediaController: MediaController? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentSound = MutableStateFlow<SoundItem?>(null)
    val currentSound: StateFlow<SoundItem?> = _currentSound.asStateFlow()

    private val _volume = MutableStateFlow(0.8f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    init {
        initController()
    }

    private fun initController() {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            try {
                val controller = controllerFuture.get()
                mediaController = controller
                controller.volume = _volume.value
                controller.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                    }
                })
                _isPlaying.value = controller.isPlaying
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, MoreExecutors.directExecutor())
    }

    @OptIn(UnstableApi::class)
    fun playSound(soundItem: SoundItem, targetVolume: Float = _volume.value) {
        _currentSound.value = soundItem
        setVolume(targetVolume)

        val uri: Uri = try {
            RawResourceDataSource.buildRawResourceUri(soundItem.resId)
        } catch (_: Exception) {
            Uri.parse("android.resource://${context.packageName}/${soundItem.resId}")
        }

        val artworkUri = Uri.parse("android.resource://${context.packageName}/drawable/placeholder")

        val mediaItem = MediaItem.Builder()
            .setMediaId(soundItem.id)
            .setUri(uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(soundItem.title)
                    .setArtist("White Noise Player")
                    .setArtworkUri(artworkUri)
                    .build()
            )
            .build()

        val controller = mediaController
        if (controller != null) {
            controller.setMediaItem(mediaItem)
            controller.prepare()
            controller.play()
        } else {
            val intent = Intent(context, PlaybackService::class.java)
            try {
                ContextCompat.startForegroundService(context, intent)
            } catch (_: Exception) {
                context.startService(intent)
            }
            val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
            val future = MediaController.Builder(context, sessionToken).buildAsync()
            future.addListener({
                try {
                    val c = future.get()
                    mediaController = c
                    c.volume = _volume.value
                    c.addListener(object : Player.Listener {
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            _isPlaying.value = isPlaying
                        }
                    })
                    c.setMediaItem(mediaItem)
                    c.prepare()
                    c.play()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, MoreExecutors.directExecutor())
        }
    }

    fun pause() {
        mediaController?.pause()
        _isPlaying.value = false
    }

    fun resume() {
        mediaController?.play()
        _isPlaying.value = true
    }

    fun stop() {
        mediaController?.stop()
        mediaController?.clearMediaItems()
        _isPlaying.value = false
        _currentSound.value = null
    }

    fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        _volume.value = clamped
        mediaController?.volume = clamped
    }

    fun release() {
        mediaController?.let {
            try {
                MediaController.releaseFuture(CompletableFuture.completedFuture(it))
            } catch (_: Exception) {
                it.release()
            }
        }
        mediaController = null
        _isPlaying.value = false
        _currentSound.value = null
    }
}
