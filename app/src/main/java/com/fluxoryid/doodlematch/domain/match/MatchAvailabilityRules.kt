package com.fluxoryid.doodlematch.domain.match

/**
 * Single source of truth for "how many saved drawings does a category need before Match is
 * playable, and how big is the board". Centralized here so the rule can change later without
 * hunting through the board builder or the UI for a hardcoded number.
 *
 * Current rule: 0-1 completed drawings -> Match unavailable; 2-4 -> board built from whatever
 * is completed; 5 (a full category) -> full 10-card board.
 */
object MatchAvailabilityRules {
    const val MIN_COMPLETED_TO_PLAY = 2
    const val FULL_CATEGORY_SIZE = 5

    fun isMatchAvailable(completedCount: Int): Boolean = completedCount >= MIN_COMPLETED_TO_PLAY
}
