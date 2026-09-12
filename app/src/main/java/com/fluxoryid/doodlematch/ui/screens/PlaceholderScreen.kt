package com.fluxoryid.doodlematch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fluxoryid.doodlematch.ui.theme.Navy
import com.fluxoryid.doodlematch.ui.theme.SkyBlue
import com.fluxoryid.doodlematch.ui.theme.WarmCream

@Composable
fun PlaceholderScreen(
    title: String,
    subtitle: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = Navy,
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle,
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
            color = Navy.copy(alpha = 0.72f),
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onBack,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue, contentColor = Navy)
        ) {
            Text("Back to Home", fontWeight = FontWeight.Bold)
        }
    }
}
