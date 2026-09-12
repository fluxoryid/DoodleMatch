package com.fluxoryid.doodlematch.ui.screens.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fluxoryid.doodlematch.data.progress.CategoryProgressSnapshot
import com.fluxoryid.doodlematch.data.progress.OverallProgressSnapshot
import com.fluxoryid.doodlematch.data.progress.ParentProgressRepository
import com.fluxoryid.doodlematch.ui.theme.Lavender
import com.fluxoryid.doodlematch.ui.theme.Mint
import com.fluxoryid.doodlematch.ui.theme.Navy
import com.fluxoryid.doodlematch.ui.theme.SkyBlue
import com.fluxoryid.doodlematch.ui.theme.Sunshine
import com.fluxoryid.doodlematch.ui.theme.WarmCream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ParentZoneScreen(onBack: () -> Unit) {
    val context = LocalContext.current.applicationContext
    var snapshot by remember { mutableStateOf<OverallProgressSnapshot?>(null) }

    LaunchedEffect(Unit) {
        snapshot = withContext(Dispatchers.IO) {
            ParentProgressRepository(context).loadSnapshot()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = onBack) {
                Text("← Home", fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text("Parent Zone", color = Navy, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                Text("Private, on-device progress overview", color = Navy.copy(alpha = 0.66f), fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(10.dp))

        val current = snapshot
        if (current == null) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Preparing progress…", color = Navy.copy(alpha = 0.72f))
            }
        } else {
            ParentDashboard(current)
        }
    }
}

@Composable
private fun ParentDashboard(snapshot: OverallProgressSnapshot) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MetricCard("Drawings", "${snapshot.drawingsCompleted}/${snapshot.totalDrawings}", SkyBlue, Modifier.weight(1f))
                MetricCard("Match sets", "${snapshot.completedMatchCategories}/${snapshot.totalCategories}", Mint, Modifier.weight(1f))
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MetricCard("Stickers", "${snapshot.stickersUnlocked}/${snapshot.totalCategories}", Sunshine, Modifier.weight(1f))
                MetricCard("Completed games", snapshot.totalCompletedMatches.toString(), Lavender, Modifier.weight(1f))
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SkyBlue.copy(alpha = 0.14f)),
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("Activity indicators", color = Navy, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = progressNarrative(snapshot),
                        color = Navy.copy(alpha = 0.76f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "These are in-app activity indicators only. They are not developmental, educational, or medical assessments.",
                        color = Navy.copy(alpha = 0.58f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                    )
                }
            }
        }

        item {
            Text(
                text = "Progress by category",
                modifier = Modifier.padding(top = 4.dp),
                color = Navy,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
            )
        }

        items(snapshot.categories, key = { it.categoryId }) { category ->
            CategoryProgressCard(category)
        }

        item { Spacer(Modifier.height(8.dp)) }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    tint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = tint.copy(alpha = 0.30f)),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Text(value, color = Navy, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Text(label, color = Navy.copy(alpha = 0.68f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun CategoryProgressCard(category: CategoryProgressSnapshot) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.78f)),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(category.title, modifier = Modifier.weight(1f), color = Navy, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    text = if (category.stickerUnlocked) "Sticker ✓" else "Sticker locked",
                    color = Navy.copy(alpha = 0.62f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.height(7.dp))
            LinearProgressIndicator(
                progress = { category.drawingProgressFraction },
                modifier = Modifier.fillMaxWidth(),
                color = Mint,
                trackColor = Navy.copy(alpha = 0.08f),
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = "Drawings ${category.drawingsCompleted}/${category.totalDrawings}  ·  Completed games ${category.completedMatches}",
                color = Navy.copy(alpha = 0.68f),
                fontSize = 11.sp,
            )
            if (category.bestAttempts != null || category.bestTimeMillis != null) {
                Spacer(Modifier.height(3.dp))
                val attempts = category.bestAttempts?.let { "Best attempts $it" }
                val time = category.bestTimeMillis?.let { "Best time ${formatDuration(it)}" }
                Text(
                    text = listOfNotNull(attempts, time).joinToString("  ·  "),
                    color = Navy.copy(alpha = 0.68f),
                    fontSize = 11.sp,
                )
            }
        }
    }
}

private fun progressNarrative(snapshot: OverallProgressSnapshot): String {
    val drawingText = if (snapshot.drawingsCompleted == 0) {
        "No saved drawings yet."
    } else {
        "Drawing participation is active in ${snapshot.activeCategories} of ${snapshot.totalCategories} categories, with ${snapshot.drawingsCompleted} saved drawings."
    }
    val matchText = if (snapshot.totalCompletedMatches == 0) {
        " Match play has not been completed yet."
    } else {
        " Match activities have been completed ${snapshot.totalCompletedMatches} time${if (snapshot.totalCompletedMatches == 1) "" else "s"} across ${snapshot.completedMatchCategories} categories."
    }
    val rewardText = " ${snapshot.stickersUnlocked} of ${snapshot.totalCategories} category rewards are unlocked."
    return drawingText + matchText + rewardText
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = millis.coerceAtLeast(0L) / 1000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "%d:%02d".format(minutes, seconds)
}
