package com.fluxoryid.doodlematch.domain.assets

import com.fluxoryid.doodlematch.domain.DoodleCatalog

data class ObjectAssetSpec(
    val categoryId: String,
    val objectId: String,
    val officialAssetName: String,
    val traceAssetName: String,
)

data class CategoryAssetSpec(
    val categoryId: String,
    val coverAssetName: String,
    val stickerAssetName: String,
    val objects: List<ObjectAssetSpec>,
)

object ProductionAssetCatalog {
    val mascotAssetNames = listOf(
        "mascot_doodlematch_primary",
        "mascot_doodlematch_happy",
        "mascot_doodlematch_celebrate",
        "mascot_doodlematch_encourage",
    )

    const val appIconForeground = "ic_launcher_doodlematch"
    const val splashMascot = "splash_doodlematch"

    private val categoryIds = listOf(
        "animals",
        "dinosaurs",
        "vehicles",
        "fruits",
        "space",
        "abc_numbers",
    )

    val categories: List<CategoryAssetSpec> = categoryIds.map { categoryId ->
        CategoryAssetSpec(
            categoryId = categoryId,
            coverAssetName = "category_$categoryId",
            stickerAssetName = "sticker_$categoryId",
            objects = DoodleCatalog.objectsFor(categoryId).map { item ->
                ObjectAssetSpec(
                    categoryId = categoryId,
                    objectId = item.id,
                    officialAssetName = "official_${categoryId}_${item.id}",
                    traceAssetName = "trace_${categoryId}_${item.id}",
                )
            },
        )
    }

    val objects: List<ObjectAssetSpec> = categories.flatMap { it.objects }

    fun category(categoryId: String): CategoryAssetSpec? =
        categories.firstOrNull { it.categoryId == categoryId }

    fun objectAsset(categoryId: String, objectId: String): ObjectAssetSpec? =
        category(categoryId)?.objects?.firstOrNull { it.objectId == objectId }
}
