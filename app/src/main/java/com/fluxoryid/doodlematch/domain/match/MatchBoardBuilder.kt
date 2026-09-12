package com.fluxoryid.doodlematch.domain.match

import com.fluxoryid.doodlematch.domain.DrawObject
import com.fluxoryid.doodlematch.domain.model.CardImageSource
import com.fluxoryid.doodlematch.domain.model.CardType
import com.fluxoryid.doodlematch.domain.model.MatchCard
import com.fluxoryid.doodlematch.domain.model.OfficialArtwork
import com.fluxoryid.doodlematch.domain.model.toOfficialArtwork

sealed class MatchBoardResult {
    /** Fewer than [MatchAvailabilityRules.MIN_COMPLETED_TO_PLAY] drawings exist yet. */
    data class NotEnoughDrawings(val completedCount: Int) : MatchBoardResult()
    data class Board(val cards: List<MatchCard>) : MatchBoardResult()
}

/**
 * Builds the card list for a Match game: one CHILD_DRAWING card and one OFFICIAL card per
 * completed object, then shuffles them.
 *
 * Pure by design — takes the set of already-completed object ids rather than touching any
 * storage itself, and takes [shuffle] as a parameter (defaulting to real shuffling) so tests can
 * inject a deterministic ordering instead of asserting against real randomness.
 */
object MatchBoardBuilder {
    fun build(
        categoryId: String,
        objects: List<DrawObject>,
        completedObjectIds: Set<String>,
        officialArtworkFor: (DrawObject) -> OfficialArtwork = { it.toOfficialArtwork() },
        shuffle: (List<MatchCard>) -> List<MatchCard> = { it.shuffled() },
    ): MatchBoardResult {
        val completedObjects = objects.filter { it.id in completedObjectIds }

        if (!MatchAvailabilityRules.isMatchAvailable(completedObjects.size)) {
            return MatchBoardResult.NotEnoughDrawings(completedObjects.size)
        }

        val cards = completedObjects.flatMap { obj ->
            listOf(
                MatchCard(
                    id = "${obj.id}_child",
                    objectId = obj.id,
                    categoryId = categoryId,
                    cardType = CardType.CHILD_DRAWING,
                    imageSource = CardImageSource.ChildDrawing,
                ),
                MatchCard(
                    id = "${obj.id}_official",
                    objectId = obj.id,
                    categoryId = categoryId,
                    cardType = CardType.OFFICIAL,
                    imageSource = CardImageSource.Official(officialArtworkFor(obj)),
                ),
            )
        }

        return MatchBoardResult.Board(shuffle(cards))
    }
}
