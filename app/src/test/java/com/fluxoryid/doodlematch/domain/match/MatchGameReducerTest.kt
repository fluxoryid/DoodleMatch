package com.fluxoryid.doodlematch.domain.match

import com.fluxoryid.doodlematch.domain.model.CardImageSource
import com.fluxoryid.doodlematch.domain.model.CardType
import com.fluxoryid.doodlematch.domain.model.MatchCard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchGameReducerTest {

    private fun childCard(objectId: String) = MatchCard(
        id = "${objectId}_child",
        objectId = objectId,
        categoryId = "animals",
        cardType = CardType.CHILD_DRAWING,
        imageSource = CardImageSource.ChildDrawing,
    )

    private fun officialCard(objectId: String) = MatchCard(
        id = "${objectId}_official",
        objectId = objectId,
        categoryId = "animals",
        cardType = CardType.OFFICIAL,
        imageSource = CardImageSource.Official(
            com.fluxoryid.doodlematch.domain.model.OfficialArtwork.Placeholder("🐘"),
        ),
    )

    private fun twoPairState() = MatchGameState(
        categoryId = "animals",
        cards = listOf(
            childCard("elephant"), officialCard("elephant"),
            childCard("lion"), officialCard("lion"),
        ),
        totalPairs = 2,
    )

    @Test
    fun `correct pair detection marks both cards matched and face up`() {
        var state = twoPairState()
        state = MatchGameReducer.selectCard(state, "elephant_child")
        state = MatchGameReducer.selectCard(state, "elephant_official")
        state = MatchGameReducer.resolveComparison(state)

        val elephantCards = state.cards.filter { it.objectId == "elephant" }
        assertTrue(elephantCards.all { it.isMatched })
        assertTrue(elephantCards.all { it.isFaceUp })
        assertEquals(1, state.matchedPairCount)
        assertEquals(0, state.incorrectAttempts)
    }

    @Test
    fun `incorrect pair detection flips both cards back down`() {
        var state = twoPairState()
        state = MatchGameReducer.selectCard(state, "elephant_child")
        state = MatchGameReducer.selectCard(state, "lion_official")
        state = MatchGameReducer.resolveComparison(state)

        val touchedCards = state.cards.filter { it.id == "elephant_child" || it.id == "lion_official" }
        assertTrue(touchedCards.none { it.isFaceUp })
        assertTrue(touchedCards.none { it.isMatched })
        assertEquals(0, state.matchedPairCount)
        assertEquals(1, state.incorrectAttempts)
    }

    @Test
    fun `same card cannot be selected as its own second card`() {
        var state = twoPairState()
        state = MatchGameReducer.selectCard(state, "elephant_child")
        val afterSameTapAgain = MatchGameReducer.selectCard(state, "elephant_child")

        // The reducer must treat this as a no-op: no second selection is recorded.
        assertSame(state, afterSameTapAgain)
    }

    @Test
    fun `matched cards cannot be selected again`() {
        var state = twoPairState()
        state = MatchGameReducer.selectCard(state, "elephant_child")
        state = MatchGameReducer.selectCard(state, "elephant_official")
        state = MatchGameReducer.resolveComparison(state)

        val afterTappingMatchedCard = MatchGameReducer.selectCard(state, "elephant_child")
        assertSame(state, afterTappingMatchedCard)
    }

    @Test
    fun `game completes when all pairs are matched`() {
        var state = twoPairState()
        state = MatchGameReducer.selectCard(state, "elephant_child")
        state = MatchGameReducer.selectCard(state, "elephant_official")
        state = MatchGameReducer.resolveComparison(state)
        assertFalse(state.isComplete)

        state = MatchGameReducer.selectCard(state, "lion_child")
        state = MatchGameReducer.selectCard(state, "lion_official")
        state = MatchGameReducer.resolveComparison(state)

        assertTrue(state.isComplete)
        assertEquals(2, state.matchedPairCount)
    }

    @Test
    fun `attempt count increments once per pair comparison, not per tap`() {
        var state = twoPairState()
        assertEquals(0, state.attempts)

        state = MatchGameReducer.selectCard(state, "elephant_child")
        assertEquals(0, state.attempts) // first tap of a pair is not yet an "attempt"

        state = MatchGameReducer.selectCard(state, "lion_child")
        assertEquals(1, state.attempts) // second tap completes one attempt

        state = MatchGameReducer.resolveComparison(state)
        state = MatchGameReducer.selectCard(state, "elephant_official")
        state = MatchGameReducer.selectCard(state, "lion_official")
        assertEquals(2, state.attempts)
    }

    @Test
    fun `board is locked during comparison so extra taps are ignored`() {
        var state = twoPairState()
        state = MatchGameReducer.selectCard(state, "elephant_child")
        state = MatchGameReducer.selectCard(state, "lion_child")
        assertTrue(state.isBoardLocked)

        val afterExtraTap = MatchGameReducer.selectCard(state, "elephant_official")
        assertSame(state, afterExtraTap)
    }
}
