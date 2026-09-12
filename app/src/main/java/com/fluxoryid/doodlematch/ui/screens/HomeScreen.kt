package com.fluxoryid.doodlematch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fluxoryid.doodlematch.ui.theme.Coral
import com.fluxoryid.doodlematch.ui.theme.Lavender
import com.fluxoryid.doodlematch.ui.theme.Mint
import com.fluxoryid.doodlematch.ui.theme.Navy
import com.fluxoryid.doodlematch.ui.theme.SkyBlue
import com.fluxoryid.doodlematch.ui.theme.Sunshine
import com.fluxoryid.doodlematch.ui.theme.WarmCream
import kotlinx.coroutines.withTimeoutOrNull

enum class PlayMode { DRAW, MATCH }

data class DoodleCategory(
    val id: String,
    val title: String,
    val symbol: String,
    val color: Color,
    val locked: Boolean = false
)

private val categories = listOf(
    DoodleCategory("animals", "Animals", "🐘", SkyBlue),
    DoodleCategory("dinosaurs", "Dinosaurs", "🦕", Mint),
    DoodleCategory("vehicles", "Vehicles", "🚗", Sunshine),
    DoodleCategory("fruits", "Fruits", "🍎", Coral),
    DoodleCategory("space", "Space", "🚀", Lavender),
    DoodleCategory("abc_numbers", "ABC & Numbers", "ABC\n123", SkyBlue)
)

@Composable
fun HomeScreen(
    onCategorySelected: (categoryId: String, mode: PlayMode) -> Unit,
    onStickers: () -> Unit,
    onParentZone: () -> Unit
) {
    var selectedMode by remember { mutableStateOf<PlayMode?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "DoodleMatch",
                    color = Navy,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Draw it, then find its match.",
                    color = Navy.copy(alpha = 0.72f),
                    fontSize = 13.sp
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StickerShortcut(onClick = onStickers)
                ParentZoneLongPressControl(onLongPress = onParentZone)
            }
        }

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ModeButton(
                label = "✏️  Draw",
                selected = selectedMode == PlayMode.DRAW,
                onClick = { selectedMode = PlayMode.DRAW },
                modifier = Modifier.weight(1f)
            )
            ModeButton(
                label = "🧩  Match",
                selected = selectedMode == PlayMode.MATCH,
                onClick = { selectedMode = PlayMode.MATCH },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = when (selectedMode) {
                PlayMode.DRAW -> "Choose what you want to draw"
                PlayMode.MATCH -> "Choose a set to match"
                null -> "Choose Draw or Match first"
            },
            modifier = Modifier.fillMaxWidth(),
            color = Navy,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            categories.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { category ->
                        CategoryCard(
                            category = category,
                            enabled = selectedMode != null && !category.locked,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            onClick = {
                                selectedMode?.let { mode ->
                                    if (!category.locked) onCategorySelected(category.id, mode)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(46.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selected) SkyBlue.copy(alpha = 0.30f) else MaterialTheme.colorScheme.surface,
            contentColor = Navy
        )
    ) {
        Text(label, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CategoryCard(
    category: DoodleCategory,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) category.color.copy(alpha = 0.34f)
            else category.color.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = category.symbol,
                    fontSize = if (category.id == "abc_numbers") 24.sp else 34.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 25.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = category.title,
                    color = Navy.copy(alpha = if (enabled) 1f else 0.55f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                if (category.locked) {
                    Text("Locked", color = Navy.copy(alpha = 0.55f), fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun StickerShortcut(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(Sunshine.copy(alpha = 0.34f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text("⭐", fontSize = 18.sp)
    }
}

@Composable
private fun ParentZoneLongPressControl(onLongPress: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(SkyBlue.copy(alpha = 0.22f), RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                awaitEachGesture {
                    val firstEvent = awaitPointerEvent()
                    if (firstEvent.changes.none { it.pressed }) return@awaitEachGesture

                    val releasedBeforeThreshold = withTimeoutOrNull(1_000L) {
                        var stillPressed: Boolean
                        do {
                            val event = awaitPointerEvent()
                            stillPressed = event.changes.any { it.pressed }
                        } while (stillPressed)
                        true
                    }

                    if (releasedBeforeThreshold == null) {
                        onLongPress()
                        var stillPressed: Boolean
                        do {
                            val event = awaitPointerEvent()
                            stillPressed = event.changes.any { it.pressed }
                        } while (stillPressed)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text("⌂", color = Navy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
