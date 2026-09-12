package com.fluxoryid.doodlematch.ui.screens.match

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.fluxoryid.doodlematch.data.drawing.FileDrawingAvailabilityRepository
import com.fluxoryid.doodlematch.data.progress.FileMatchProgressStore
import com.fluxoryid.doodlematch.domain.DoodleCatalog
import com.fluxoryid.doodlematch.domain.match.MatchAvailabilityRules
import com.fluxoryid.doodlematch.domain.match.MatchGameState
import com.fluxoryid.doodlematch.domain.model.CardImageSource
import com.fluxoryid.doodlematch.domain.model.CardType
import com.fluxoryid.doodlematch.domain.model.MatchCard
import com.fluxoryid.doodlematch.domain.model.OfficialArtwork
import com.fluxoryid.doodlematch.ui.theme.Mint
import com.fluxoryid.doodlematch.ui.theme.Navy
import com.fluxoryid.doodlematch.ui.theme.SkyBlue
import com.fluxoryid.doodlematch.ui.theme.Sunshine
import com.fluxoryid.doodlematch.ui.theme.WarmCream

@Composable
fun MatchScreen(
    categoryId: String,
    onBack: () -> Unit,
    onGoToDraw: () -> Unit,
    onOpenStickers: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: MatchViewModel = viewModel(
        key = "match_$categoryId",
        factory = viewModelFactory {
            initializer {
                MatchViewModel(
                    categoryId = categoryId,
                    availabilityRepository = FileDrawingAvailabilityRepository(context.applicationContext),
                    progressStore = FileMatchProgressStore(context.applicationContext),
                )
            }
        },
    )
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is MatchUiState.Loading -> LoadingContent()
        is MatchUiState.NotEnoughDrawings -> NotEnoughDrawingsContent(
            categoryId = categoryId,
            completedCount = state.completedCount,
            onGoToDraw = onGoToDraw,
            onBack = onBack,
        )
        is MatchUiState.Playing -> PlayingContent(
            categoryId = categoryId,
            state = state.state,
            rewardNewlyUnlocked = state.rewardNewlyUnlocked,
            onCardTapped = viewModel::onCardTapped,
            onPlayAgain = viewModel::startNewGame,
            onOpenStickers = onOpenStickers,
            onBack = onBack,
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize().background(WarmCream),
        contentAlignment = Alignment.Center,
    ) {
        Text("Getting your cards ready…", color = Navy, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun NotEnoughDrawingsContent(
    categoryId: String,
    completedCount: Int,
    onGoToDraw: () -> Unit,
    onBack: () -> Unit,
) {
    val minNeeded = MatchAvailabilityRules.MIN_COMPLETED_TO_PLAY
    val message = if (completedCount == 0) {
        "Draw a few pictures first so we can play Match!"
    } else {
        val remaining = minNeeded - completedCount
        "Draw $remaining more picture${if (remaining == 1) "" else "s"} to play Match!"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = DoodleCatalog.categoryTitle(categoryId),
            color = Navy,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = message,
            modifier = Modifier.padding(top = 10.dp, bottom = 24.dp),
            color = Navy.copy(alpha = 0.72f),
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = onGoToDraw,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Mint, contentColor = Navy),
        ) {
            Text("Go to Draw", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = onBack) {
            Text("Back to Home", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PlayingContent(
    categoryId: String,
    state: MatchGameState,
    rewardNewlyUnlocked: Boolean,
    onCardTapped: (String) -> Unit,
    onPlayAgain: () -> Unit,
    onOpenStickers: () -> Unit,
    onBack: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().background(WarmCream)) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(onClick = onBack) {
                    Text("← Home", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Match · ${DoodleCatalog.categoryTitle(categoryId)}",
                        color = Navy,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = "Attempts: ${state.attempts}  ·  ${formatElapsed(state.elapsedMillis)}",
                        color = Navy.copy(alpha = 0.68f),
                        fontSize = 12.sp,
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            MatchGrid(
                categoryId = categoryId,
                cards = state.cards,
                onCardTapped = onCardTapped,
                modifier = Modifier.fillMaxWidth().weight(1f),
            )
        }

        if (state.isComplete) {
            CompletionOverlay(
                categoryId = categoryId,
                state = state,
                rewardNewlyUnlocked = rewardNewlyUnlocked,
                onPlayAgain = onPlayAgain,
                onOpenStickers = onOpenStickers,
                onHome = onBack,
            )
        }
    }
}

/** Fixed 2-column grid, chunked by rows — matches HomeScreen's own category-grid pattern, and
 * keeps gameplay scroll-free on a ~360x640dp phone for up to 10 cards (5 rows). */
@Composable
private fun MatchGrid(
    categoryId: String,
    cards: List<MatchCard>,
    onCardTapped: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        cards.chunked(2).forEach { rowCards ->
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowCards.forEach { card ->
                    FlipCard(
                        card = card,
                        categoryId = categoryId,
                        onClick = { onCardTapped(card.id) },
                        modifier = Modifier.weight(1f).fillMaxSize(),
                    )
                }
                if (rowCards.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun FlipCard(
    card: MatchCard,
    categoryId: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current
    val isRevealed = card.isFaceUp || card.isMatched

    val rotation by animateFloatAsState(
        targetValue = if (isRevealed) 180f else 0f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "cardFlip",
    )
    val matchScale by animateFloatAsState(
        targetValue = if (card.isMatched) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "matchPulse",
    )

    Card(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                scaleX = matchScale
                scaleY = matchScale
                cameraDistance = 12f * density
            }
            .clickable(enabled = !card.isMatched && !isRevealed) {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = if (card.isMatched) 0.dp else 2.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (rotation <= 90f) {
                CardBack()
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f },
                ) {
                    CardFront(card = card, categoryId = categoryId)
                }
            }
        }
    }
}

@Composable
private fun CardBack() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SkyBlue.copy(alpha = 0.30f)),
        contentAlignment = Alignment.Center,
    ) {
        Text("✦", color = Navy.copy(alpha = 0.45f), fontSize = 26.sp)
    }
}

@Composable
private fun CardFront(card: MatchCard, categoryId: String) {
    when (card.cardType) {
        CardType.CHILD_DRAWING -> ChildDrawingThumbnail(
            categoryId = categoryId,
            objectId = card.objectId,
            modifier = Modifier.fillMaxSize().padding(4.dp),
        )
        CardType.OFFICIAL -> {
            val source = card.imageSource
            if (source is CardImageSource.Official) {
                when (val artwork = source.artwork) {
                    is OfficialArtwork.Placeholder -> Text(
                        text = artwork.emoji,
                        fontSize = 34.sp,
                        textAlign = TextAlign.Center,
                    )
                    is OfficialArtwork.Resource -> Image(
                        painter = painterResource(id = artwork.resourceId),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(6.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CompletionOverlay(
    categoryId: String,
    state: MatchGameState,
    rewardNewlyUnlocked: Boolean,
    onPlayAgain: () -> Unit,
    onOpenStickers: () -> Unit,
    onHome: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy.copy(alpha = 0.35f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = WarmCream),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = completionHeadline(state.accuracyPercent),
                    color = Navy,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = DoodleCatalog.categoryTitle(categoryId),
                    color = Navy.copy(alpha = 0.72f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${state.attempts} attempts  ·  ${formatElapsed(state.elapsedMillis)}",
                    color = Navy.copy(alpha = 0.60f),
                    fontSize = 13.sp,
                )

                if (rewardNewlyUnlocked) {
                    Spacer(Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .background(Sunshine.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text("⭐ New sticker unlocked!", color = Navy, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Mint, contentColor = Navy),
                ) {
                    Text("Play Again", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = onOpenStickers, modifier = Modifier.weight(1f)) {
                        Text("Stickers")
                    }
                    OutlinedButton(onClick = onHome, modifier = Modifier.weight(1f)) {
                        Text("Home")
                    }
                }
            }
        }
    }
}

private fun completionHeadline(accuracyPercent: Int): String = when {
    accuracyPercent >= 90 -> "Amazing matching!"
    accuracyPercent >= 70 -> "Great memory!"
    accuracyPercent >= 50 -> "Nice job!"
    else -> "You found them all!"
}

private fun formatElapsed(elapsedMillis: Long): String {
    val totalSeconds = elapsedMillis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
