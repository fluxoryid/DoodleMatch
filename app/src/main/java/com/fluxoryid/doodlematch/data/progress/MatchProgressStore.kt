package com.fluxoryid.doodlematch.data.progress

import android.content.Context
import org.json.JSONObject
import java.io.File

/**
 * Persists Match completion progress per category. Same plain-JSON-file approach as
 * [com.fluxoryid.doodlematch.data.DrawingStore] — chosen deliberately over Room: Room is a
 * declared but currently-unused dependency in this project (no KSP/room-compiler is even
 * configured), and introducing it just because it's on the classpath would add real setup risk
 * for no benefit at this phase's scope. This can migrate to Room later if progress data grows
 * more relational; the interface below is what makes that swap possible without touching
 * [com.fluxoryid.doodlematch.ui.screens.match.MatchViewModel].
 */
interface MatchProgressStore {
    fun load(categoryId: String): CategoryProgress
    fun recordCompletion(categoryId: String, attempts: Int, elapsedMillis: Long): ProgressUpdateResult
}

class FileMatchProgressStore(private val context: Context) : MatchProgressStore {

    override fun load(categoryId: String): CategoryProgress {
        val file = targetFile(categoryId)
        if (!file.exists()) return CategoryProgress(categoryId = categoryId)

        return runCatching {
            val root = JSONObject(file.readText())
            CategoryProgress(
                categoryId = categoryId,
                timesPlayed = root.optInt("timesPlayed", 0),
                bestAttempts = root.optInt("bestAttempts", -1).takeIf { it >= 0 },
                bestTimeMillis = root.optLong("bestTimeMillis", -1L).takeIf { it >= 0L },
                stickerUnlocked = root.optBoolean("stickerUnlocked", false),
                earnedAt = root.optLong("earnedAt", -1L).takeIf { it >= 0L },
            )
        }.getOrDefault(CategoryProgress(categoryId = categoryId))
    }

    override fun recordCompletion(categoryId: String, attempts: Int, elapsedMillis: Long): ProgressUpdateResult {
        val result = ProgressUpdater.applyCompletion(
            current = load(categoryId),
            attempts = attempts,
            elapsedMillis = elapsedMillis,
            nowMillis = System.currentTimeMillis(),
        )
        save(result.progress)
        return result
    }

    private fun save(progress: CategoryProgress) {
        val root = JSONObject().apply {
            put("categoryId", progress.categoryId)
            put("timesPlayed", progress.timesPlayed)
            progress.bestAttempts?.let { put("bestAttempts", it) }
            progress.bestTimeMillis?.let { put("bestTimeMillis", it) }
            put("stickerUnlocked", progress.stickerUnlocked)
            progress.earnedAt?.let { put("earnedAt", it) }
        }
        targetFile(progress.categoryId).writeText(root.toString())
    }

    private fun targetFile(categoryId: String): File {
        val directory = File(context.filesDir, "progress").apply { mkdirs() }
        return File(directory, "$categoryId.json")
    }
}
