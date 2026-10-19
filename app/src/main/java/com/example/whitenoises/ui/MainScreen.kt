package com.example.whitenoises.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.whitenoises.R
import com.example.whitenoises.model.PlaybackState
import com.example.whitenoises.model.SoundCategory
import com.example.whitenoises.model.SoundItem
import com.example.whitenoises.ui.components.CategoryFilterChips
import com.example.whitenoises.ui.components.PlaybackControlPanel
import com.example.whitenoises.ui.components.SleepTimerDialog
import com.example.whitenoises.ui.components.SoundGrid
import com.example.whitenoises.ui.theme.WhiteNoisesTheme

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    MainScreenContent(
        uiState = uiState,
        onCategorySelected = viewModel::selectCategory,
        onSoundClick = viewModel::onSoundClick,
        onPlayPauseClick = viewModel::togglePlayPause,
        onStopClick = viewModel::stop,
        onVolumeChange = viewModel::setVolume,
        onTimerClick = { viewModel.setTimerDialogOpen(true) },
        onStartTimer = viewModel::startSleepTimer,
        onCancelTimer = viewModel::cancelSleepTimer,
        onDismissTimerDialog = { viewModel.setTimerDialogOpen(false) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenContent(
    uiState: MainUiState,
    onCategorySelected: (SoundCategory) -> Unit,
    onSoundClick: (SoundItem) -> Unit,
    onPlayPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onTimerClick: () -> Unit,
    onStartTimer: (Int) -> Unit,
    onCancelTimer: () -> Unit,
    onDismissTimerDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.NightsStay,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "White Noises",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                CategoryFilterChips(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = onCategorySelected,
                    modifier = Modifier.fillMaxWidth()
                )

                SoundGrid(
                    sounds = uiState.sounds,
                    currentSound = uiState.playbackState.currentSound,
                    isPlaying = uiState.playbackState.isPlaying,
                    onSoundClick = onSoundClick,
                    modifier = Modifier.weight(1f)
                )
            }

            // Docked Control Panel at the bottom
            PlaybackControlPanel(
                playbackState = uiState.playbackState,
                onPlayPauseClick = onPlayPauseClick,
                onStopClick = onStopClick,
                onVolumeChange = onVolumeChange,
                onTimerClick = onTimerClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            if (uiState.isTimerDialogOpen) {
                SleepTimerDialog(
                    playbackState = uiState.playbackState,
                    onStartTimer = onStartTimer,
                    onCancelTimer = onCancelTimer,
                    onDismiss = onDismissTimerDialog
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun MainScreenPreviewLight() {
    WhiteNoisesTheme(darkTheme = false) {
        MainScreenContent(
            uiState = MainUiState(
                playbackState = PlaybackState(
                    isPlaying = true,
                    volume = 0.8f
                )
            ),
            onCategorySelected = {},
            onSoundClick = {},
            onPlayPauseClick = {},
            onStopClick = {},
            onVolumeChange = {},
            onTimerClick = {},
            onStartTimer = {},
            onCancelTimer = {},
            onDismissTimerDialog = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun MainScreenPreviewDark() {
    WhiteNoisesTheme(darkTheme = true) {
        MainScreenContent(
            uiState = MainUiState(
                playbackState = PlaybackState(
                    isPlaying = false,
                    volume = 0.6f
                )
            ),
            onCategorySelected = {},
            onSoundClick = {},
            onPlayPauseClick = {},
            onStopClick = {},
            onVolumeChange = {},
            onTimerClick = {},
            onStartTimer = {},
            onCancelTimer = {},
            onDismissTimerDialog = {}
        )
    }
}

@Preview(showBackground = true, name = "App Icon Preview")
@Composable
fun AppIconPreview() {
    Row(
        modifier = Modifier
            .background(Color(0xFF0F172A))
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Squircle Icon
        Box(
            modifier = Modifier
                .size(108.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Circle Icon
        Box(
            modifier = Modifier
                .size(108.dp)
                .clip(CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
