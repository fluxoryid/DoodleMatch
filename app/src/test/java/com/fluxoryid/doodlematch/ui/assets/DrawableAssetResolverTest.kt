package com.fluxoryid.doodlematch.ui.assets

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DrawableAssetResolverTest {
    @Test
    fun `returns resource id when drawable exists`() {
        val result = DrawableAssetResolver.resolve("category_animals") { name ->
            if (name == "category_animals") 42 else 0
        }

        assertEquals(42, result)
    }

    @Test
    fun `returns null when drawable is missing`() {
        val result = DrawableAssetResolver.resolve("category_space") { 0 }

        assertNull(result)
    }

    @Test
    fun `treats negative lookup values as missing`() {
        val result = DrawableAssetResolver.resolve("sticker_animals") { -1 }

        assertNull(result)
    }
}
