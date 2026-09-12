package com.fluxoryid.doodlematch.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class NormalizedPoint(val x: Float, val y: Float)

data class SavedStroke(
    val points: List<NormalizedPoint>,
    val colorArgb: Int,
    val widthFraction: Float
)

object DrawingStore {
    private const val VERSION = 1

    fun save(
        context: Context,
        categoryId: String,
        objectId: String,
        strokes: List<SavedStroke>
    ) {
        val root = JSONObject().apply {
            put("version", VERSION)
            put("categoryId", categoryId)
            put("objectId", objectId)
            put("strokes", JSONArray().apply {
                strokes.forEach { stroke ->
                    put(JSONObject().apply {
                        put("colorArgb", stroke.colorArgb)
                        put("widthFraction", stroke.widthFraction.toDouble())
                        put("points", JSONArray().apply {
                            stroke.points.forEach { point ->
                                put(JSONArray().apply {
                                    put(point.x.toDouble())
                                    put(point.y.toDouble())
                                })
                            }
                        })
                    })
                }
            })
        }

        targetFile(context, categoryId, objectId).writeText(root.toString())
    }

    fun load(
        context: Context,
        categoryId: String,
        objectId: String
    ): List<SavedStroke> {
        val file = targetFile(context, categoryId, objectId)
        if (!file.exists()) return emptyList()

        return runCatching {
            val root = JSONObject(file.readText())
            val strokeArray = root.optJSONArray("strokes") ?: return@runCatching emptyList()

            buildList {
                for (i in 0 until strokeArray.length()) {
                    val strokeObject = strokeArray.getJSONObject(i)
                    val pointArray = strokeObject.optJSONArray("points") ?: JSONArray()
                    val points = buildList {
                        for (j in 0 until pointArray.length()) {
                            val pair = pointArray.getJSONArray(j)
                            add(
                                NormalizedPoint(
                                    x = pair.getDouble(0).toFloat().coerceIn(0f, 1f),
                                    y = pair.getDouble(1).toFloat().coerceIn(0f, 1f)
                                )
                            )
                        }
                    }
                    add(
                        SavedStroke(
                            points = points,
                            colorArgb = strokeObject.getInt("colorArgb"),
                            widthFraction = strokeObject
                                .optDouble("widthFraction", 0.018)
                                .toFloat()
                                .coerceIn(0.005f, 0.08f)
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    fun exists(context: Context, categoryId: String, objectId: String): Boolean =
        targetFile(context, categoryId, objectId).exists()

    private fun targetFile(context: Context, categoryId: String, objectId: String): File {
        val directory = File(context.filesDir, "drawings/$categoryId").apply { mkdirs() }
        return File(directory, "$objectId.json")
    }
}
