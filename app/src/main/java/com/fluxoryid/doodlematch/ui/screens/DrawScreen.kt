package com.fluxoryid.doodlematch.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.fluxoryid.doodlematch.data.DrawingStore
import com.fluxoryid.doodlematch.data.NormalizedPoint
import com.fluxoryid.doodlematch.data.SavedStroke
import com.fluxoryid.doodlematch.domain.DoodleCatalog
import com.fluxoryid.doodlematch.ui.theme.Coral
import com.fluxoryid.doodlematch.ui.theme.Mint
import com.fluxoryid.doodlematch.ui.theme.Navy
import com.fluxoryid.doodlematch.ui.theme.SkyBlue
import com.fluxoryid.doodlematch.ui.theme.WarmCream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.min

@Composable
fun DrawScreen(
    categoryId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val objects = remember(categoryId) { DoodleCatalog.objectsFor(categoryId) }

    if (objects.isEmpty()) {
        PlaceholderScreen(
            title = "Draw",
            subtitle = "This category is not available yet.",
            onBack = onBack
        )
        return
    }

    var selectedObjectId by remember(categoryId) { mutableStateOf(objects.first().id) }
    val selectedObject = objects.first { it.id == selectedObjectId }
    var strokes by remember { mutableStateOf<List<SavedStroke>>(emptyList()) }
    var currentPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var brushColorArgb by remember { mutableStateOf(Navy.toArgb()) }
    var brushWidthFraction by remember { mutableStateOf(0.018f) }
    var statusText by remember { mutableStateOf("") }

    LaunchedEffect(categoryId, selectedObjectId) {
        currentPoints = emptyList()
        strokes = withContext(Dispatchers.IO) {
            DrawingStore.load(context, categoryId, selectedObjectId)
        }
        statusText = if (strokes.isNotEmpty()) "Saved drawing loaded" else ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = onBack) {
                Text("← Home", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = "Draw · ${DoodleCatalog.categoryTitle(categoryId)}",
                    color = Navy,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Pick an object, trace it, then save.",
                    color = Navy.copy(alpha = 0.68f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(objects, key = { it.id }) { item ->
                val selected = item.id == selectedObjectId
                OutlinedButton(
                    onClick = {
                        selectedObjectId = item.id
                        statusText = ""
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (selected) SkyBlue.copy(alpha = 0.30f) else Color.White,
                        contentColor = Navy
                    )
                ) {
                    Text("${item.symbol} ${item.title}", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = selectedObject.symbol,
                    modifier = Modifier.align(Alignment.Center),
                    color = Navy.copy(alpha = 0.10f),
                    fontSize = if (categoryId == "abc_numbers") 140.sp else 112.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { canvasSize = it }
                        .pointerInput(
                            selectedObjectId,
                            brushColorArgb,
                            brushWidthFraction,
                            canvasSize
                        ) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPoints = listOf(offset)
                                    statusText = ""
                                },
                                onDragCancel = { currentPoints = emptyList() },
                                onDragEnd = {
                                    if (
                                        currentPoints.size >= 2 &&
                                        canvasSize.width > 0 &&
                                        canvasSize.height > 0
                                    ) {
                                        val width = canvasSize.width.toFloat()
                                        val height = canvasSize.height.toFloat()
                                        val normalized = currentPoints.map { point ->
                                            NormalizedPoint(
                                                x = (point.x / width).coerceIn(0f, 1f),
                                                y = (point.y / height).coerceIn(0f, 1f)
                                            )
                                        }
                                        strokes = strokes + SavedStroke(
                                            points = normalized,
                                            colorArgb = brushColorArgb,
                                            widthFraction = brushWidthFraction
                                        )
                                    }
                                    currentPoints = emptyList()
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentPoints = currentPoints + change.position
                                }
                            )
                        }
                ) {
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
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }

                    if (currentPoints.size >= 2) {
                        val path = Path().apply {
                            moveTo(currentPoints.first().x, currentPoints.first().y)
                            currentPoints.drop(1).forEach { point ->
                                lineTo(point.x, point.y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = Color(brushColorArgb),
                            style = Stroke(
                                width = brushWidthFraction * minimumSide,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                Text(
                    text = "Trace ${selectedObject.title}",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .background(WarmCream.copy(alpha = 0.86f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    color = Navy.copy(alpha = 0.68f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val palette = listOf(
                    Navy,
                    Color(0xFFE85C4A),
                    Color(0xFF2E9D68),
                    Color(0xFF7356C8),
                    Color(0xFF222222)
                )
                palette.forEach { color ->
                    val selected = brushColorArgb == color.toArgb()
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(color, CircleShape)
                            .border(
                                width = if (selected) 3.dp else 1.dp,
                                color = if (selected) SkyBlue else Navy.copy(alpha = 0.20f),
                                shape = CircleShape
                            )
                            .clickable { brushColorArgb = color.toArgb() }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                BrushSizeButton(
                    label = "Thin",
                    selected = brushWidthFraction == 0.012f,
                    onClick = { brushWidthFraction = 0.012f }
                )
                BrushSizeButton(
                    label = "Thick",
                    selected = brushWidthFraction == 0.028f,
                    onClick = { brushWidthFraction = 0.028f }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    if (strokes.isNotEmpty()) {
                        strokes = strokes.dropLast(1)
                        statusText = ""
                    }
                },
                enabled = strokes.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Undo")
            }
            OutlinedButton(
                onClick = {
                    strokes = emptyList()
                    currentPoints = emptyList()
                    statusText = "Canvas cleared"
                },
                enabled = strokes.isNotEmpty() || currentPoints.isNotEmpty(),
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }
            Button(
                onClick = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            DrawingStore.save(
                                context = context,
                                categoryId = categoryId,
                                objectId = selectedObjectId,
                                strokes = strokes
                            )
                        }
                        statusText = "Saved ✓"
                    }
                },
                enabled = strokes.isNotEmpty(),
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Mint,
                    contentColor = Navy
                )
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        }

        if (statusText.isNotBlank()) {
            Text(
                text = statusText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                color = if (statusText.startsWith("Saved")) Color(0xFF267A50) else Coral,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun BrushSizeButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        contentPadding = ButtonDefaults.ContentPadding,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) SkyBlue.copy(alpha = 0.28f) else Color.White,
            contentColor = Navy
        )
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
