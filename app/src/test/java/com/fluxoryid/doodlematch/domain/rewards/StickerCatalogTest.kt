package com.fluxoryid.doodlematch.domain.rewards

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class StickerCatalogTest {

    @Test
    fun catalog_hasOneStickerPerCategory() {
        assertEquals(6, StickerCatalog.all.size)
        assertEquals(6, StickerCatalog.all.map { it.categoryId }.toSet().size)
    }

    @Test
    fun categoryLookup_returnsExpectedSticker() {
        val sticker = StickerCatalog.forCategory("space")
        assertNotNull(sticker)
        assertEquals("Space Scout", sticker?.title)
    }
}
