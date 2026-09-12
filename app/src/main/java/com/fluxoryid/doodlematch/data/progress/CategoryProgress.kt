package com.fluxoryid.doodlematch.data.progress

/**
 * Everything persisted for one category's Match progress. [stickerUnlocked]/[earnedAt] is the
 * Phase 3 "reward bridge" — a full sticker collection UI is Phase 4 scope, but the earned/not
 * state has to exist now so a completion doesn't lose it.
 */
data class CategoryProgress(
    val categoryId: String,
    val timesPlayed: Int = 0,
    val bestAttempts: Int? = null,
    val bestTimeMillis: Long? = null,
    val stickerUnlocked: Boolean = false,
    val earnedAt: Long? = null,
)

data class ProgressUpdateResult(
    val progress: CategoryProgress,
    /** True only on the completion call that actually unlocked the sticker (i.e. the first
     * time this category was ever completed) — false on every later completion, so callers
     * never re-announce or re-award a reward the child already has. */
    val rewardNewlyUnlocked: Boolean,
)

/**
 * Pure update logic, deliberately separated from [MatchProgressStore]'s file I/O so "sticker
 * awarded only once" and the best-attempt/best-time bookkeeping are unit-testable without a
 * [android.content.Context].
 */
object ProgressUpdater {
    fun applyCompletion(
        current: CategoryProgress,
        attempts: Int,
        elapsedMillis: Long,
        nowMillis: Long,
    ): ProgressUpdateResult {
        val rewardNewlyUnlocked = !current.stickerUnlocked
        val updated = current.copy(
            timesPlayed = current.timesPlayed + 1,
            bestAttempts = current.bestAttempts?.let { minOf(it, attempts) } ?: attempts,
            bestTimeMillis = current.bestTimeMillis?.let { minOf(it, elapsedMillis) } ?: elapsedMillis,
            stickerUnlocked = true,
            earnedAt = current.earnedAt ?: nowMillis,
        )
        return ProgressUpdateResult(updated, rewardNewlyUnlocked)
    }
}
