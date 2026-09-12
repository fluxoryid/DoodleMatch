package com.fluxoryid.doodlematch.domain.match

import com.fluxoryid.doodlematch.domain.model.MatchCard

data class MatchGameState(
    val categoryId: String,
    val cards: List<MatchCard>,
    val totalPairs: Int,
    val firstSelectedCardId: String? = null,
    val secondSelectedCardId: String? = null,
    val matchedPairCount: Int = 0,
    val attempts: Int = 0,
    val incorrectAttempts: Int = 0,
    val elapsedMillis: Long = 0L,
    val isComplete: Boolean = false,
    /** True while two face-up cards are being compared — taps are ignored during this window. */
    val isBoardLocked: Boolean = false,
) {
    /**
     * accuracy = totalPairs / attempts * 100, guarded against division by zero (no attempts made
     * yet -> 0, not NaN/Infinity). Intentionally never shown as a harsh score to the child —
     * see MatchScreen's supportive completion messaging.
     */
    val accuracyPercent: Int
        get() = if (attempts == 0) 0 else ((totalPairs.toFloat() / attempts) * 100).toInt().coerceIn(0, 100)
}
