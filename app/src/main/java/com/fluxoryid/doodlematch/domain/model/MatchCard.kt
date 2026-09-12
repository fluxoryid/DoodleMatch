package com.fluxoryid.doodlematch.domain.model

import com.fluxoryid.doodlematch.domain.DrawObject

/** Which side of a matching pair a card represents. */
enum class CardType { CHILD_DRAWING, OFFICIAL }

/**
 * How a card's official-art side should be rendered.
 *
 * [Placeholder] is what the whole catalog uses today (an emoji/letter symbol from
 * [DrawObject.symbol]). [Resource] is the drop-in slot for real Canva-exported artwork later —
 * swapping a category's objects from Placeholder to Resource requires no change anywhere in the
 * Match game logic or UI, only in whatever maps a [DrawObject] to an [OfficialArtwork].
 */
sealed class OfficialArtwork {
    data class Resource(val resourceId: Int) : OfficialArtwork()
    data class Placeholder(val emoji: String) : OfficialArtwork()
}

/**
 * Which image a card shows. [ChildDrawing] doesn't carry its own category/object id — it always
 * renders using the owning [MatchCard]'s [MatchCard.categoryId]/[MatchCard.objectId], so there's
 * only one place those ids live per card.
 */
sealed class CardImageSource {
    object ChildDrawing : CardImageSource()
    data class Official(val artwork: OfficialArtwork) : CardImageSource()
}

data class MatchCard(
    val id: String,
    val objectId: String,
    val categoryId: String,
    val cardType: CardType,
    val imageSource: CardImageSource,
    val isFaceUp: Boolean = false,
    val isMatched: Boolean = false,
)

/**
 * Default official-artwork mapping: today every object only has its catalog emoji/letter, so
 * every card falls back to [OfficialArtwork.Placeholder]. This is the single place that
 * decision lives — nothing else in the Match feature reasons about emojis directly.
 */
fun DrawObject.toOfficialArtwork(): OfficialArtwork = OfficialArtwork.Placeholder(symbol)
