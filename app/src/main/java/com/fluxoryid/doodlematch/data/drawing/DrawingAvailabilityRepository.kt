package com.fluxoryid.doodlematch.data.drawing

import android.content.Context
import com.fluxoryid.doodlematch.data.DrawingStore

/**
 * Tells Match which objects in a category already have a saved drawing. Deliberately just a
 * thin wrapper around the existing [DrawingStore] — no parallel persistence system, per Phase 3
 * scope. Behind an interface so [MatchViewModel][com.fluxoryid.doodlematch.ui.screens.match.MatchViewModel]
 * doesn't need a real [Context] in unit tests.
 */
interface DrawingAvailabilityRepository {
    fun completedObjectIds(categoryId: String, objectIds: List<String>): Set<String>
}

class FileDrawingAvailabilityRepository(private val context: Context) : DrawingAvailabilityRepository {
    override fun completedObjectIds(categoryId: String, objectIds: List<String>): Set<String> =
        objectIds.filterTo(mutableSetOf()) { objectId ->
            DrawingStore.exists(context, categoryId, objectId)
        }
}
