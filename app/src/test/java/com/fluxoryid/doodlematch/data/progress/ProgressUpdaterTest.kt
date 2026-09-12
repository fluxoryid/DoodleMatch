package com.fluxoryid.doodlematch.data.progress

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressUpdaterTest {

    @Test
    fun `sticker is awarded only on the first completion`() {
        val fresh = CategoryProgress(categoryId = "animals")

        val first = ProgressUpdater.applyCompletion(fresh, attempts = 8, elapsedMillis = 40_000, nowMillis = 1_000)
        assertTrue(first.rewardNewlyUnlocked)
        assertTrue(first.progress.stickerUnlocked)
        assertEquals(1_000L, first.progress.earnedAt)

        val second = ProgressUpdater.applyCompletion(first.progress, attempts = 6, elapsedMillis = 30_000, nowMillis = 2_000)
        assertFalse(second.rewardNewlyUnlocked)
        assertTrue(second.progress.stickerUnlocked)
        // earnedAt must not be overwritten by a later completion.
        assertEquals(1_000L, second.progress.earnedAt)
    }

    @Test
    fun `best attempts and best time track the lowest value seen`() {
        val fresh = CategoryProgress(categoryId = "animals")

        val first = ProgressUpdater.applyCompletion(fresh, attempts = 10, elapsedMillis = 50_000, nowMillis = 1_000)
        val worse = ProgressUpdater.applyCompletion(first.progress, attempts = 14, elapsedMillis = 60_000, nowMillis = 2_000)
        val better = ProgressUpdater.applyCompletion(worse.progress, attempts = 6, elapsedMillis = 20_000, nowMillis = 3_000)

        assertEquals(6, better.progress.bestAttempts)
        assertEquals(20_000L, better.progress.bestTimeMillis)
        assertEquals(3, better.progress.timesPlayed)
    }
}
