package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = JoseonGold,
    onPrimary = InkBlack,
    primaryContainer = RoyalCrimsonDark,
    onPrimaryContainer = JoseonGold,
    secondary = JoseonGoldLight,
    onSecondary = InkBlack,
    background = DarkHanjiBg,
    onBackground = HanjiPaper,
    surface = DarkHanjiSurface,
    onSurface = HanjiPaper,
    surfaceVariant = DarkHanjiCard,
    onSurfaceVariant = HanjiPaperDark,
    outline = InkLight
)

private val LightColorScheme = lightColorScheme(
    primary = RoyalCrimson,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8DEF8),
    onPrimaryContainer = Color(0xFF1D192B),
    secondary = Color(0xFF625B71),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1D192B),
    background = HanjiPaper,
    onBackground = InkBlack,
    surface = HanjiCard,
    onSurface = InkBlack,
    surfaceVariant = HanjiPaperDark,
    onSurfaceVariant = InkGrey,
    outline = Color(0xFFCAC4D0)
)

@Composable
fun JoseonSillokTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

