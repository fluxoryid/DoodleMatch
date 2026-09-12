package com.fluxoryid.doodlematch.ui.screens.match

import com.fluxoryid.doodlematch.domain.match.MatchGameState

sealed class MatchUiState {
    object Loading : MatchUiState()

    /** Fewer than [com.fluxoryid.doodlematch.domain.match.MatchAvailabilityRules.MIN_COMPLETED_TO_PLAY]
     * drawings exist for this category yet. */
    data class NotEnoughDrawings(val completedCount: Int) : MatchUiState()

    data class Playing(
        val state: MatchGameState,
        /** Set once, right after the completion that triggered it — read by MatchScreen to
         * show "New sticker!" only on that one completion, not every subsequent replay. */
        val rewardNewlyUnlocked: Boolean = false,
    ) : MatchUiState()
}
