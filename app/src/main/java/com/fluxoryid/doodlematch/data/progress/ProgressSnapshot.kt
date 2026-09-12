package com.fluxoryid.doodlematch.data.progress

data class CategoryProgressSnapshot(
    val categoryId: String,
    val title: String,
    val drawingsCompleted: Int,
    val totalDrawings: Int,
    val completedMatches: Int,
    val bestAttempts: Int?,
    val bestTimeMillis: Long?,
    val stickerUnlocked: Boolean,
    val earnedAt: Long?,
) {
    val drawingProgressFraction: Float
        get() = if (totalDrawings <= 0) 0f else drawingsCompleted.toFloat() / totalDrawings.toFloat()
}

data class OverallProgressSnapshot(
    val categories: List<CategoryProgressSnapshot>,
) {
    val drawingsCompleted: Int = categories.sumOf { it.drawingsCompleted }
    val totalDrawings: Int = categories.sumOf { it.totalDrawings }
    val completedMatchCategories: Int = categories.count { it.completedMatches > 0 }
    val totalCategories: Int = categories.size
    val stickersUnlocked: Int = categories.count { it.stickerUnlocked }
    val totalCompletedMatches: Int = categories.sumOf { it.completedMatches }
    val activeCategories: Int = categories.count { it.drawingsCompleted > 0 || it.completedMatches > 0 }
}

data class CategoryProgressInput(
    val categoryId: String,
    val title: String,
    val drawingsCompleted: Int,
    val totalDrawings: Int,
    val matchProgress: CategoryProgress,
)

/** Pure aggregation used by the Android repository and unit tests. */
object ProgressAggregator {
    fun aggregate(inputs: List<CategoryProgressInput>): OverallProgressSnapshot =
        OverallProgressSnapshot(
            categories = inputs.map { input ->
                CategoryProgressSnapshot(
                    categoryId = input.categoryId,
                    title = input.title,
                    drawingsCompleted = input.drawingsCompleted.coerceIn(0, input.totalDrawings.coerceAtLeast(0)),
                    totalDrawings = input.totalDrawings.coerceAtLeast(0),
                    completedMatches = input.matchProgress.timesPlayed.coerceAtLeast(0),
                    bestAttempts = input.matchProgress.bestAttempts,
                    bestTimeMillis = input.matchProgress.bestTimeMillis,
                    stickerUnlocked = input.matchProgress.stickerUnlocked,
                    earnedAt = input.matchProgress.earnedAt,
                )
            }
        )
}
