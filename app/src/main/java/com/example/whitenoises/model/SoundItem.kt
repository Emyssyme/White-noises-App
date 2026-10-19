package com.example.whitenoises.model

import androidx.annotation.RawRes

data class SoundItem(
    val id: String,
    val title: String,
    val category: SoundCategory,
    @get:RawRes val resId: Int,
    val iconName: String,
    val description: String,
    val defaultVolume: Float = 0.8f,
    val isPreset: Boolean = true
)
