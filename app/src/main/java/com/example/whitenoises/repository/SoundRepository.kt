package com.example.whitenoises.repository

import android.content.Context
import com.example.whitenoises.R
import com.example.whitenoises.model.SoundCategory
import com.example.whitenoises.model.SoundItem

interface SoundRepository {
    fun getAvailableSounds(): List<SoundItem>
    fun getSoundsByCategory(category: SoundCategory): List<SoundItem>
    fun getSoundById(id: String): SoundItem?
    fun recordSoundPlay(soundId: String)
    fun getTotalPlays(): Int
}

class SoundRepositoryImpl(private val context: Context? = null) : SoundRepository {

    private val prefs = context?.getSharedPreferences("sound_usage_prefs", Context.MODE_PRIVATE)

    private val defaultSounds = listOf(
        // Color Noises
        SoundItem(
            id = "white_noise",
            title = "White Noise",
            category = SoundCategory.NOISE,
            resId = R.raw.white_noise,
            iconName = "GraphicEq",
            description = "Classic static white noise to block out distractions and background sounds."
        ),
        SoundItem(
            id = "pink_noise",
            title = "Pink Noise",
            category = SoundCategory.NOISE,
            resId = R.raw.pink_noise,
            iconName = "Equalizer",
            description = "Balanced deeper noise with smoother high frequencies for sleep and focus."
        ),
        SoundItem(
            id = "brown_noise",
            title = "Deep Brown Noise",
            category = SoundCategory.NOISE,
            resId = R.raw.brown_noise,
            iconName = "SurroundSound",
            description = "Warm low-frequency rumble reminiscent of distant thunder or heavy bass."
        ),

        // Fans
        SoundItem(
            id = "box_fan",
            title = "Box Fan",
            category = SoundCategory.FANS,
            resId = R.raw.box_fan,
            iconName = "Air",
            description = "Deep steady whirring of a box fan for deep sleep."
        ),
        SoundItem(
            id = "ceiling_fan",
            title = "Ceiling Fan",
            category = SoundCategory.FANS,
            resId = R.raw.fan,
            iconName = "Air",
            description = "Smooth mechanical whirring of a ceiling fan providing air motion."
        ),
        SoundItem(
            id = "oscillating_fan",
            title = "Oscillating Fan",
            category = SoundCategory.FANS,
            resId = R.raw.oscillating_fan,
            iconName = "Air",
            description = "Rhythmic sweeping airflow of an oscillating room fan."
        ),

        // Baby & Sleep
        SoundItem(
            id = "womb_sound",
            title = "Womb Sound",
            category = SoundCategory.BABY,
            resId = R.raw.womb,
            iconName = "Favorite",
            description = "Warm muffled prenatal womb sound that recreates the environment before birth."
        ),
        SoundItem(
            id = "shusher",
            title = "Baby Shusher",
            category = SoundCategory.BABY,
            resId = R.raw.shusher,
            iconName = "RecordVoiceOver",
            description = "Rhythmic soothing shushing sound to calm crying babies."
        ),
        SoundItem(
            id = "heartbeat",
            title = "Heartbeat",
            category = SoundCategory.BABY,
            resId = R.raw.heartbeat,
            iconName = "MonitorHeart",
            description = "Calming rhythmic heartbeat providing security and familiarity."
        ),
        SoundItem(
            id = "hairdryer",
            title = "Hairdryer",
            category = SoundCategory.BABY,
            resId = R.raw.hairdryer,
            iconName = "Dry",
            description = "Uniform hum of a hairdryer, highly effective for shushing newborns."
        ),
        SoundItem(
            id = "vacuum",
            title = "Vacuum Cleaner",
            category = SoundCategory.BABY,
            resId = R.raw.vacuum,
            iconName = "SurroundSound",
            description = "Deep consistent background rumble of a vacuum cleaner."
        ),
        SoundItem(
            id = "lullaby",
            title = "Music Box Lullaby",
            category = SoundCategory.BABY,
            resId = R.raw.lullaby,
            iconName = "MusicNote",
            description = "Gentle music box melody to soothe children into deep sleep."
        ),

        // Nature & Water
        SoundItem(
            id = "rain",
            title = "Heavy Rain",
            category = SoundCategory.NATURE,
            resId = R.raw.rain,
            iconName = "WaterDrop",
            description = "Soothing rainfall sound with gentle water droplets."
        ),
        SoundItem(
            id = "rain_roof",
            title = "Rain on Roof",
            category = SoundCategory.NATURE,
            resId = R.raw.rain_roof,
            iconName = "Home",
            description = "Cozy sound of rain tapping gently against the roof."
        ),
        SoundItem(
            id = "rain_tent",
            title = "Rain on Tent",
            category = SoundCategory.NATURE,
            resId = R.raw.rain_tent,
            iconName = "Cottage",
            description = "Calming pitter-patter of rain droplets hitting canvas."
        ),
        SoundItem(
            id = "thunder",
            title = "Distant Thunder",
            category = SoundCategory.NATURE,
            resId = R.raw.thunder,
            iconName = "Thunderstorm",
            description = "Deep rolling distant thunder accompanied by light rain."
        ),
        SoundItem(
            id = "ocean",
            title = "Ocean Waves",
            category = SoundCategory.NATURE,
            resId = R.raw.ocean,
            iconName = "Sailing",
            description = "Rhythmic ocean swells rolling gently onto the shore."
        ),
        SoundItem(
            id = "stream",
            title = "Forest Stream",
            category = SoundCategory.NATURE,
            resId = R.raw.stream,
            iconName = "Waves",
            description = "Bubbling freshwater stream flowing gently over river stones."
        ),
        SoundItem(
            id = "campfire",
            title = "Cozy Campfire",
            category = SoundCategory.NATURE,
            resId = R.raw.campfire,
            iconName = "LocalFireDepartment",
            description = "Crackling wood embers and warm ambient glow."
        ),
        SoundItem(
            id = "crickets",
            title = "Night Crickets",
            category = SoundCategory.NATURE,
            resId = R.raw.crickets,
            iconName = "Bedtime",
            description = "Peaceful night meadow sound with gentle crickets chirping."
        ),
        SoundItem(
            id = "wind",
            title = "Wind in Trees",
            category = SoundCategory.NATURE,
            resId = R.raw.wind,
            iconName = "Air",
            description = "Soft breeze rustling gently through forest leaves."
        ),

        // Household & Travel
        SoundItem(
            id = "ac",
            title = "Air Conditioner",
            category = SoundCategory.AMBIENCE,
            resId = R.raw.ac,
            iconName = "AcUnit",
            description = "Steady ambient hum of a home air conditioner."
        ),
        SoundItem(
            id = "train",
            title = "Train Ride",
            category = SoundCategory.AMBIENCE,
            resId = R.raw.train,
            iconName = "Train",
            description = "Rhythmic clack-clack sound of a train riding along tracks."
        ),
        SoundItem(
            id = "airplane",
            title = "Airplane Cabin",
            category = SoundCategory.AMBIENCE,
            resId = R.raw.airplane,
            iconName = "Flight",
            description = "Deep white noise jet engine rumble inside an airplane cabin."
        ),
        SoundItem(
            id = "clock",
            title = "Clock Ticking",
            category = SoundCategory.AMBIENCE,
            resId = R.raw.clock,
            iconName = "Schedule",
            description = "Steady rhythmic tick-tock of a vintage wall clock."
        )
    )

    override fun getAvailableSounds(): List<SoundItem> = defaultSounds

    override fun recordSoundPlay(soundId: String) {
        prefs?.let {
            val total = it.getInt("total_plays", 0) + 1
            val count = it.getInt("play_count_$soundId", 0) + 1
            it.edit()
                .putInt("total_plays", total)
                .putInt("play_count_$soundId", count)
                .apply()
        }
    }

    override fun getTotalPlays(): Int {
        return prefs?.getInt("total_plays", 0) ?: 0
    }

    override fun getSoundsByCategory(category: SoundCategory): List<SoundItem> {
        return when (category) {
            SoundCategory.ALL -> defaultSounds
            SoundCategory.MOST_USED -> {
                val sorted = defaultSounds.sortedByDescending { sound ->
                    prefs?.getInt("play_count_${sound.id}", 0) ?: 0
                }
                sorted.take(5)
            }
            else -> defaultSounds.filter { it.category == category }
        }
    }

    override fun getSoundById(id: String): SoundItem? {
        return defaultSounds.find { it.id == id }
    }
}
