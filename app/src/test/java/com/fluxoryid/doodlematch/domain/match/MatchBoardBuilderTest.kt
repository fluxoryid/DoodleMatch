package com.fluxoryid.doodlematch.domain.match

import com.fluxoryid.doodlematch.domain.DrawObject
import com.fluxoryid.doodlematch.domain.model.CardType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchBoardBuilderTest {

    private val fiveObjects = listOf(
        DrawObject("elephant", "Elephant", "🐘"),
        DrawObject("lion", "Lion", "🦁"),
        DrawObject("cat", "Cat", "🐱"),
        DrawObject("dog", "Dog", "🐶"),
        DrawObject("fish", "Fish", "🐟"),
    )

    @Test
    fun `no playable board when fewer than two drawings exist`() {
        val zeroResult = MatchBoardBuilder.build("animals", fiveObjects, completedObjectIds = emptySet())
        val oneResult = MatchBoardBuilder.build("animals", fiveObjects, completedObjectIds = setOf("elephant"))

        assertTrue(zeroResult is MatchBoardResult.NotEnoughDrawings)
        assertTrue(oneResult is MatchBoardResult.NotEnoughDrawings)
        assertEquals(0, (zeroResult as MatchBoardResult.NotEnoughDrawings).completedCount)
        assertEquals(1, (oneResult as MatchBoardResult.NotEnoughDrawings).completedCount)
    }

    @Test
    fun `board handles fewer than five completed drawings`() {
        val result = MatchBoardBuilder.build(
            "animals",
            fiveObjects,
            completedObjectIds = setOf("elephant", "lion", "cat"),
        )

        assertTrue(result is MatchBoardResult.Board)
        val cards = (result as MatchBoardResult.Board).cards
        assertEquals(6, cards.size) // 3 completed objects * 2 cards each
        assertEquals(setOf("elephant", "lion", "cat"), cards.map { it.objectId }.toSet())
    }

    @Test
    fun `full category produces the full ten card board`() {
        val result = MatchBoardBuilder.build(
            "animals",
            fiveObjects,
            completedObjectIds = fiveObjects.map { it.id }.toSet(),
        )

        assertTrue(result is MatchBoardResult.Board)
        val cards = (result as MatchBoardResult.Board).cards
        assertEquals(10, cards.size)
        assertEquals(5, cards.count { it.cardType == CardType.CHILD_DRAWING })
        assertEquals(5, cards.count { it.cardType == CardType.OFFICIAL })
    }

    @Test
    fun `cards are randomized via the injected shuffle function`() {
        // Inject a reversing "shuffle" so the test is deterministic rather than asserting
        // against real Random output, while still proving the board builder actually applies
        // whatever shuffle function it's given rather than always returning input order.
        val result = MatchBoardBuilder.build(
            "animals",
            fiveObjects,
            completedObjectIds = setOf("elephant", "lion"),
            shuffle = { it.reversed() },
        )

        assertTrue(result is MatchBoardResult.Board)
        val cards = (result as MatchBoardResult.Board).cards
        assertEquals("lion_official", cards.first().id)
        assertEquals("elephant_child", cards.last().id)
    }
}
