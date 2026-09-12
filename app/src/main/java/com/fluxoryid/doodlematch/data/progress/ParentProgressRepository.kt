package com.fluxoryid.doodlematch.data.progress

import android.content.Context
import com.fluxoryid.doodlematch.data.DrawingStore
import com.fluxoryid.doodlematch.domain.DoodleCatalog
import com.fluxoryid.doodlematch.domain.rewards.StickerCatalog

class ParentProgressRepository(
    private val context: Context,
    private val matchProgressStore: MatchProgressStore = FileMatchProgressStore(context),
) {
    fun loadSnapshot(): OverallProgressSnapshot {
        val inputs = StickerCatalog.all.map { sticker ->
            val objects = DoodleCatalog.objectsFor(sticker.categoryId)
            val completedDrawings = objects.count { drawObject ->
                DrawingStore.exists(context, sticker.categoryId, drawObject.id)
            }
            CategoryProgressInput(
                categoryId = sticker.categoryId,
                title = DoodleCatalog.categoryTitle(sticker.categoryId),
                drawingsCompleted = completedDrawings,
                totalDrawings = objects.size,
                matchProgress = matchProgressStore.load(sticker.categoryId),
            )
        }
        return ProgressAggregator.aggregate(inputs)
    }
}
