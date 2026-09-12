package com.fluxoryid.doodlematch.data.progress

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressAggregatorTest {

    @Test
    fun aggregate_calculatesOverallTotals() {
        val snapshot = ProgressAggregator.aggregate(
            listOf(
                CategoryProgressInput(
                    categoryId = "animals",
                    title = "Animals",
                    drawingsCompleted = 5,
                    totalDrawings = 5,
                    matchProgress = CategoryProgress(
                        categoryId = "animals",
                        timesPlayed = 3,
                        bestAttempts = 6,
                        bestTimeMillis = 42_000L,
                        stickerUnlocked = true,
                        earnedAt = 100L,
                    ),
                ),
                CategoryProgressInput(
                    categoryId = "space",
                    title = "Space",
                    drawingsCompleted = 2,
                    totalDrawings = 5,
                    matchProgress = CategoryProgress(categoryId = "space"),
                ),
            )
        )

        assertEquals(7, snapshot.drawingsCompleted)
        assertEquals(10, snapshot.totalDrawings)
        assertEquals(1, snapshot.completedMatchCategories)
        assertEquals(1, snapshot.stickersUnlocked)
        assertEquals(3, snapshot.totalCompletedMatches)
        assertEquals(2, snapshot.activeCategories)
    }

    @Test
    fun aggregate_clampsDrawingCountToCategoryTotal() {
        val snapshot = ProgressAggregator.aggregate(
            listOf(
                CategoryProgressInput(
                    categoryId = "fruits",
                    title = "Fruits",
                    drawingsCompleted = 99,
                    totalDrawings = 5,
                    matchProgress = CategoryProgress(categoryId = "fruits"),
                )
            )
        )

        assertEquals(5, snapshot.categories.single().drawingsCompleted)
        assertEquals(1f, snapshot.categories.single().drawingProgressFraction)
    }

    @Test
    fun aggregate_preservesBestPerformanceFacts() {
        val snapshot = ProgressAggregator.aggregate(
            listOf(
                CategoryProgressInput(
                    categoryId = "vehicles",
                    title = "Vehicles",
                    drawingsCompleted = 4,
                    totalDrawings = 5,
                    matchProgress = CategoryProgress(
                        categoryId = "vehicles",
                        timesPlayed = 2,
                        bestAttempts = 7,
                        bestTimeMillis = 31_000L,
                        stickerUnlocked = true,
                    ),
                )
            )
        )

        val category = snapshot.categories.single()
        assertEquals(7, category.bestAttempts)
        assertEquals(31_000L, category.bestTimeMillis)
        assertTrue(category.stickerUnlocked)
    }
}
