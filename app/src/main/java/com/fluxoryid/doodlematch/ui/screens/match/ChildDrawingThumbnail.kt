package com.fluxoryid.doodlematch.ui.screens.match

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import com.fluxoryid.doodlematch.data.DrawingStore
import com.fluxoryid.doodlematch.data.SavedStroke
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.min

/**
 * Renders a child's saved drawing read-only, at whatever size the caller gives it. Reuses the
 * exact same normalized-point-to-pixel scaling [DrawScreen][com.fluxoryid.doodlematch.ui.screens.DrawScreen]
 * uses when drawing live, so a card never stretches a drawing disproportionately relative to how
 * it looked while being drawn — both read the same [SavedStroke] data through
 * [DrawingStore], with no second/parallel drawing-storage format.
 *
 * Deliberately not a Composable-level `remember` cache across recompositions of the whole grid:
 * each card loads its own strokes once (keyed on category+object) and holds nothing in memory
 * beyond that small list of normalized points — there's no bitmap decoding or duplication here.
 */
@Composable
fun ChildDrawingThumbnail(
    categoryId: String,
    objectId: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var strokes by remember(categoryId, objectId) { mutableStateOf<List<SavedStroke>>(emptyList()) }

    LaunchedEffect(categoryId, objectId) {
        strokes = withContext(Dispatchers.IO) {
            DrawingStore.load(context, categoryId, objectId)
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val minimumSide = min(size.width, size.height)
            strokes.forEach { stroke ->
                if (stroke.points.size >= 2) {
                    val path = Path()
                    val first = stroke.points.first()
                    path.moveTo(first.x * size.width, first.y * size.height)
                    stroke.points.drop(1).forEach { point ->
                        path.lineTo(point.x * size.width, point.y * size.height)
                    }
                    drawPath(
                        path = path,
                        color = Color(stroke.colorArgb),
                        style = Stroke(
                            width = stroke.widthFraction * minimumSide,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                        ),
                    )
                }
            }
        }
    }
}
