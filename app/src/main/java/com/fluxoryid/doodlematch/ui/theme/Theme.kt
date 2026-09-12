package com.fluxoryid.doodlematch.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val SkyBlue = Color(0xFF64C7F5)
val Sunshine = Color(0xFFFFD86B)
val Coral = Color(0xFFFF9B8B)
val Mint = Color(0xFF8DDEC5)
val Lavender = Color(0xFFC9B6F4)
val WarmCream = Color(0xFFFFF9EF)
val Navy = Color(0xFF213354)
val SoftWhite = Color(0xFFFFFDF8)

private val DoodleMatchColors = lightColorScheme(
    primary = SkyBlue,
    onPrimary = Navy,
    secondary = Sunshine,
    onSecondary = Navy,
    tertiary = Coral,
    onTertiary = Navy,
    background = WarmCream,
    onBackground = Navy,
    surface = SoftWhite,
    onSurface = Navy,
    outline = Color(0xFFD8D7D3)
)

@Composable
fun DoodleMatchTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DoodleMatchColors,
        content = content
    )
}
