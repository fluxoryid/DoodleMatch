package com.fluxoryid.doodlematch.ui.assets

import android.content.Context

/**
 * Resolves production drawable names at runtime so Phase 5 assets can be added
 * incrementally without introducing compile-time R.drawable references.
 *
 * Missing assets intentionally resolve to null, allowing the UI to keep its
 * existing emoji/placeholder fallback until the approved production artwork is
 * present in res/drawable.
 */
object DrawableAssetResolver {
    fun resolve(assetName: String, lookup: (String) -> Int): Int? =
        lookup(assetName).takeIf { it > 0 }

    fun resolve(context: Context, assetName: String): Int? =
        resolve(assetName) { name ->
            context.resources.getIdentifier(name, "drawable", context.packageName)
        }
}
