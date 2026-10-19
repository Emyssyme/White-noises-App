package com.example.whitenoises

import com.example.whitenoises.model.SoundCategory
import com.example.whitenoises.repository.SoundRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SoundRepositoryTest {

    private lateinit var repository: SoundRepositoryImpl

    @Before
    fun setUp() {
        repository = SoundRepositoryImpl()
    }

    @Test
    fun `getAvailableSounds returns non-empty list of default sounds`() {
        val sounds = repository.getAvailableSounds()
        assertTrue(sounds.isNotEmpty())
    }

    @Test
    fun `getSoundsByCategory ALL returns all sounds`() {
        val allSounds = repository.getSoundsByCategory(SoundCategory.ALL)
        assertTrue(allSounds.isNotEmpty())
    }

    @Test
    fun `getSoundsByCategory NOISE returns only noise category sounds`() {
        val noiseSounds = repository.getSoundsByCategory(SoundCategory.NOISE)
        assertTrue(noiseSounds.all { it.category == SoundCategory.NOISE })
    }

    @Test
    fun `getSoundsByCategory NATURE returns nature category sounds`() {
        val natureSounds = repository.getSoundsByCategory(SoundCategory.NATURE)
        assertTrue(natureSounds.all { it.category == SoundCategory.NATURE })
    }

    @Test
    fun `getSoundsByCategory BABY returns baby category sounds`() {
        val babySounds = repository.getSoundsByCategory(SoundCategory.BABY)
        assertTrue(babySounds.isNotEmpty())
        assertTrue(babySounds.all { it.category == SoundCategory.BABY })
        assertEquals(6, babySounds.size)
    }

    @Test
    fun `getSoundsByCategory MOST_USED returns at most 5 sounds`() {
        val topSounds = repository.getSoundsByCategory(SoundCategory.MOST_USED)
        assertTrue(topSounds.isNotEmpty())
        assertTrue(topSounds.size <= 5)
    }

    @Test
    fun `getSoundById with valid ID returns correct SoundItem`() {
        val sound = repository.getSoundById("white_noise")
        assertNotNull(sound)
        assertEquals("White Noise", sound?.title)
        assertEquals(SoundCategory.NOISE, sound?.category)
    }

    @Test
    fun `getSoundById with invalid ID returns null`() {
        val sound = repository.getSoundById("non_existent_id")
        assertNull(sound)
    }
}
