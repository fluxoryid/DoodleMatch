package com.fluxoryid.doodlematch.ui.screens.stickers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fluxoryid.doodlematch.data.progress.CategoryProgressSnapshot
import com.fluxoryid.doodlematch.data.progress.OverallProgressSnapshot
import com.fluxoryid.doodlematch.data.progress.ParentProgressRepository
import com.fluxoryid.doodlematch.domain.rewards.StickerCatalog
import com.fluxoryid.doodlematch.ui.theme.Lavender
import com.fluxoryid.doodlematch.ui.theme.Mint
import com.fluxoryid.doodlematch.ui.theme.Navy
import com.fluxoryid.doodlematch.ui.theme.Sunshine
import com.fluxoryid.doodlematch.ui.theme.WarmCream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun StickersScreen(onBack: () -> Unit) {
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
                Text("← Back", fontWeight = FontWeight.Bold)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text("Sticker Book", color = Navy, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                Text("Every completed Match set earns a category sticker.", color = Navy.copy(alpha = 0.68f), fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(12.dp))

        val current = snapshot
        if (current == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Opening your sticker book…", color = Navy.copy(alpha = 0.72f))
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Sunshine.copy(alpha = 0.34f)),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text("Your collection", color = Navy, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Keep drawing and matching!", color = Navy.copy(alpha = 0.66f), fontSize = 12.sp)
                    }
                    Text("${current.stickersUnlocked} / ${current.totalCategories}", color = Navy, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(Modifier.height(10.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(current.categories, key = { it.categoryId }) { category ->
                    StickerCard(category)
                }
                item { Spacer(Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun StickerCard(category: CategoryProgressSnapshot) {
    val sticker = StickerCatalog.forCategory(category.categoryId) ?: return
    val unlocked = category.stickerUnlocked
    val container = if (unlocked) Mint.copy(alpha = 0.42f) else Lavender.copy(alpha = 0.18f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        elevation = CardDefaults.cardElevation(defaultElevation = if (unlocked) 2.dp else 0.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.72f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (unlocked) sticker.symbol else "?",
                    fontSize = 32.sp,
                    color = Navy,
                    textAlign = TextAlign.Center,
                )
            }
            Column(modifier = Modifier.weight(1f).padding(start = 14.dp)) {
                Text(
                    text = if (unlocked) sticker.title else category.title,
                    color = Navy,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Text(
                    text = if (unlocked) sticker.encouragement else "Complete this Match set to unlock the sticker.",
                    color = Navy.copy(alpha = 0.66f),
                    fontSize = 12.sp,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (unlocked) "Unlocked ✓" else "${category.drawingsCompleted}/${category.totalDrawings} drawings ready",
                    color = Navy.copy(alpha = 0.78f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
