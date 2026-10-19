package com.example.whitenoises

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.whitenoises.audio.AudioPlayerEngine
import com.example.whitenoises.repository.AudioRepositoryImpl
import com.example.whitenoises.repository.SoundRepositoryImpl
import com.example.whitenoises.ui.MainScreen
import com.example.whitenoises.ui.MainViewModel
import com.example.whitenoises.ui.MainViewModelFactory
import com.example.whitenoises.ui.theme.WhiteNoisesTheme

class MainActivity : ComponentActivity() {

    private lateinit var audioPlayerEngine: AudioPlayerEngine

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        // Permission result handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        audioPlayerEngine = AudioPlayerEngine(applicationContext)
        val audioRepository = AudioRepositoryImpl(audioPlayerEngine)
        val soundRepository = SoundRepositoryImpl(applicationContext)

        val factory = MainViewModelFactory(soundRepository, audioRepository)
        val viewModel: MainViewModel by viewModels { factory }

        setContent {
            WhiteNoisesTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
