package com.nndwn.runtext.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RunTxtColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = DarkBackground,
    primaryContainer = NeonGreen.copy(alpha = 0.15f),
    onPrimaryContainer = NeonGreen,
    secondary = NeonCyan,
    onSecondary = DarkBackground,
    secondaryContainer = NeonCyan.copy(alpha = 0.15f),
    onSecondaryContainer = NeonCyan,
    tertiary = NeonPink,
    onTertiary = DarkBackground,
    tertiaryContainer = NeonPink.copy(alpha = 0.15f),
    onTertiaryContainer = NeonPink,
    error = NeonRed,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOnSurfaceVariant.copy(alpha = 0.5f),
    surfaceContainerLow = DarkSurfaceContainer,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceVariant,
)

@Composable
fun RuntextTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RunTxtColorScheme,
        typography = AppTypography,
        content = content,
    )
}