package com.kate.aijournalcompanion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ---------- Cold modern palette ----------
private val ColdBg = Color(0xFFF2F5FA)
private val ColdSurface = Color(0xFFE7EDF6)

private val ColdPrimary = Color(0xFF5A78FF)     // icy blue
private val ColdSecondary = Color(0xFF7D96FF)   // lighter blue
private val ColdTertiary = Color(0xFF6DD6FF)    // aqua accent

private val TextOnLight = Color(0xFF1E2433)
private val TextOnDark = Color(0xFFEAF0FF)

private val ColdLightColorScheme = lightColorScheme(
    primary = ColdPrimary,
    secondary = ColdSecondary,
    tertiary = ColdTertiary,

    background = ColdBg,
    surface = ColdSurface,

    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFF06212B),

    onBackground = TextOnLight,
    onSurface = TextOnLight
)

private val ColdDarkColorScheme = darkColorScheme(
    primary = Color(0xFF8AA0FF),
    secondary = Color(0xFFA9B8FF),
    tertiary = Color(0xFF7FE2FF),

    background = Color(0xFF0E1320),
    surface = Color(0xFF151C2C),

    onPrimary = Color(0xFF0E1320),
    onSecondary = Color(0xFF0E1320),
    onTertiary = Color(0xFF0E1320),

    onBackground = TextOnDark,
    onSurface = TextOnDark
)

@Composable
fun AIJournalCompanionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) ColdDarkColorScheme else ColdLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}