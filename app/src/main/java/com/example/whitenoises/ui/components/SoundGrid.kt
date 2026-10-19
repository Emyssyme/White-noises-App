package com.example.whitenoises.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.whitenoises.model.SoundItem

@Composable
fun SoundGrid(
    sounds: List<SoundItem>,
    currentSound: SoundItem?,
    isPlaying: Boolean,
    onSoundClick: (SoundItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 280.dp),
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 200.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = sounds,
            key = { sound -> sound.id }
        ) { sound ->
            val isCurrent = currentSound?.id == sound.id
            SoundCard(
                soundItem = sound,
                isCurrentSound = isCurrent,
                isPlaying = isPlaying && isCurrent,
                onClick = { onSoundClick(sound) }
            )
        }
    }
}
