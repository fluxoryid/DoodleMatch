package com.fluxoryid.doodlematch.domain.rewards

/** Visual/reward metadata only. Persistence remains in MatchProgressStore so Phase 4 does not
 * introduce a second source of truth for whether a category reward has been earned. */
data class StickerDefinition(
    val id: String,
    val categoryId: String,
    val title: String,
    val symbol: String,
    val encouragement: String,
)

object StickerCatalog {
    val all: List<StickerDefinition> = listOf(
        StickerDefinition("animal_explorer", "animals", "Animal Explorer", "🐘", "You matched your animal doodles!"),
        StickerDefinition("dino_discoverer", "dinosaurs", "Dino Discoverer", "🦕", "You matched your dinosaur doodles!"),
        StickerDefinition("vehicle_voyager", "vehicles", "Vehicle Voyager", "🚗", "You matched your vehicle doodles!"),
        StickerDefinition("fruit_finder", "fruits", "Fruit Finder", "🍎", "You matched your fruit doodles!"),
        StickerDefinition("space_scout", "space", "Space Scout", "🚀", "You matched your space doodles!"),
        StickerDefinition("symbol_star", "abc_numbers", "ABC & Number Star", "⭐", "You matched letters and numbers!"),
    )

    fun forCategory(categoryId: String): StickerDefinition? = all.firstOrNull { it.categoryId == categoryId }
}
