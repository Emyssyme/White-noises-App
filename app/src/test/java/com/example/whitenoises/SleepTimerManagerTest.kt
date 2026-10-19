package com.example.whitenoises

import com.example.whitenoises.audio.SleepTimerManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SleepTimerManagerTest {

    @Test
    fun `startTimer sets total and remaining millis and triggers onFinish when complete`() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)
        val timerManager = SleepTimerManager(testScope)

        var finished = false
        timerManager.startTimer(
            durationMinutes = 1,
            fadeOutSeconds = 5,
            onFinish = { finished = true }
        )

        assertTrue(timerManager.isActive.value)
        assertEquals(60_000L, timerManager.totalMillis.value)
        assertEquals(60_000L, timerManager.remainingMillis.value)

        testScheduler.advanceTimeBy(61_000L)

        assertFalse(timerManager.isActive.value)
        assertEquals(0L, timerManager.remainingMillis.value)
        assertTrue(finished)
    }

    @Test
    fun `cancelTimer stops running timer and resets values`() = runTest {
        val testDispatcher = UnconfinedTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)
        val timerManager = SleepTimerManager(testScope)

        var finished = false
        timerManager.startTimer(
            durationMinutes = 10,
            onFinish = { finished = true }
        )

        assertTrue(timerManager.isActive.value)
        assertEquals(600_000L, timerManager.totalMillis.value)

        timerManager.cancelTimer()

        assertFalse(timerManager.isActive.value)
        assertEquals(0L, timerManager.totalMillis.value)
        assertEquals(0L, timerManager.remainingMillis.value)
        assertFalse(finished)
    }
}
