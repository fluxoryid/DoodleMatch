package com.fluxoryid.doodlematch.domain.match

/**
 * Pure state transitions for card selection. No coroutines, no delays, no Android — the
 * "wait, then flip back or lock in the match" timing lives in the ViewModel, which calls
 * [resolveComparison] after its own delay. Kept this way specifically so the actual matching
 * rules are unit-testable without touching a ViewModel or a dispatcher.
 */
object MatchGameReducer {

    /**
     * Applies a single card tap. Returns [state] unchanged (by reference) for every tap that
     * should be a no-op: the board is locked mid-comparison, the card doesn't exist, it's
     * already matched, it's already face-up (covers "same card twice" and "already selected"),
     * or two cards are already open awaiting resolution.
     */
    fun selectCard(state: MatchGameState, cardId: String): MatchGameState {
        if (state.isBoardLocked) return state
        if (state.secondSelectedCardId != null) return state

        val card = state.cards.find { it.id == cardId } ?: return state
        if (card.isMatched || card.isFaceUp) return state

        val flipped = state.cards.map { if (it.id == cardId) it.copy(isFaceUp = true) else it }

        return if (state.firstSelectedCardId == null) {
            state.copy(cards = flipped, firstSelectedCardId = cardId)
        } else {
            state.copy(
                cards = flipped,
                secondSelectedCardId = cardId,
                isBoardLocked = true,
                attempts = state.attempts + 1,
            )
        }
    }

    /**
     * Resolves the two currently-selected cards once the comparison delay has elapsed:
     * matching object ids keep both cards face-up and matched; a mismatch flips both back down.
     * Either way the selection slots are cleared and the board unlocks. No-ops if the state
     * doesn't actually have two cards selected (defensive; shouldn't happen in normal flow).
     */
    fun resolveComparison(state: MatchGameState): MatchGameState {
        val firstId = state.firstSelectedCardId ?: return state
        val secondId = state.secondSelectedCardId ?: return state
        val first = state.cards.find { it.id == firstId } ?: return state
        val second = state.cards.find { it.id == secondId } ?: return state

        val isMatch = first.objectId == second.objectId

        val updatedCards = state.cards.map {
            when (it.id) {
                firstId, secondId -> it.copy(isFaceUp = isMatch, isMatched = it.isMatched || isMatch)
                else -> it
            }
        }
        val matchedPairCount = if (isMatch) state.matchedPairCount + 1 else state.matchedPairCount

        return state.copy(
            cards = updatedCards,
            firstSelectedCardId = null,
            secondSelectedCardId = null,
            matchedPairCount = matchedPairCount,
            incorrectAttempts = if (isMatch) state.incorrectAttempts else state.incorrectAttempts + 1,
            isBoardLocked = false,
            isComplete = matchedPairCount >= state.totalPairs,
        )
    }
}
