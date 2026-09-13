package com.fluxoryid.doodlematch.domain.assets

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductionAssetCatalogTest {
    @Test
    fun `catalog covers all six categories and thirty objects`() {
        assertEquals(6, ProductionAssetCatalog.categories.size)
        assertEquals(30, ProductionAssetCatalog.objects.size)
    }

    @Test
    fun `all generated asset names are unique`() {
        val names = buildList {
            ProductionAssetCatalog.categories.forEach { category ->
                add(category.coverAssetName)
                add(category.stickerAssetName)
                category.objects.forEach { item ->
                    add(item.officialAssetName)
                    add(item.traceAssetName)
                }
            }
            addAll(ProductionAssetCatalog.mascotAssetNames)
            add(ProductionAssetCatalog.appIconForeground)
            add(ProductionAssetCatalog.splashMascot)
        }
        assertEquals(names.size, names.distinct().size)
    }

    @Test
    fun `asset names are android resource safe`() {
        val resourceName = Regex("^[a-z][a-z0-9_]*$")
        val names = ProductionAssetCatalog.categories.flatMap { category ->
            listOf(category.coverAssetName, category.stickerAssetName) +
                category.objects.flatMap { listOf(it.officialAssetName, it.traceAssetName) }
        } + ProductionAssetCatalog.mascotAssetNames +
            listOf(ProductionAssetCatalog.appIconForeground, ProductionAssetCatalog.splashMascot)

        assertTrue(names.all { resourceName.matches(it) })
    }

    @Test
    fun `lookup returns expected official and trace asset names`() {
        val elephant = ProductionAssetCatalog.objectAsset("animals", "elephant")
        assertNotNull(elephant)
        assertEquals("official_animals_elephant", elephant?.officialAssetName)
        assertEquals("trace_animals_elephant", elephant?.traceAssetName)
    }
}
