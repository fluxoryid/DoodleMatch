package com.fluxoryid.doodlematch.domain.match

/**
 * Hook points for sound effects tied to Match gameplay. No audio assets exist yet, so the
 * default implementation is a no-op — this exists purely so a future phase can inject a real
 * sound-playing implementation (card flip / correct match / completion chime) without touching
 * any Match game logic or UI call sites, which already call these at the right moments.
 *
 * Haptic feedback doesn't need this abstraction: Compose's built-in `LocalHapticFeedback` is
 * used directly in MatchScreen, since it requires no asset and is already lightweight.
 */
interface MatchSoundEffects {
    fun onCardFlipped() {}
    fun onMatchFound() {}
    fun onMismatch() {}
    fun onGameCompleted() {}
}

object NoOpMatchSoundEffects : MatchSoundEffects
