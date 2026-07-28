package com.nndwn.runtext.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable


private val RunTxtDarkColorScheme = darkColorScheme(
    primary = Palette.NeonGreen,
    onPrimary = Palette.Black2,
    primaryContainer = Palette.NeonGreen.copy(alpha = 0.15f),
    onPrimaryContainer = Palette.NeonGreen,
    secondary = Palette.NeonCyan,
    onSecondary = Palette.Black2,
    secondaryContainer = Palette.NeonCyan.copy(alpha = 0.15f),
    onSecondaryContainer = Palette.NeonCyan,
    tertiary = Palette.NeonPink,
    onTertiary = Palette.Black2,
    tertiaryContainer = Palette.NeonPink.copy(alpha = 0.15f),
    onTertiaryContainer = Palette.NeonPink,
    error = Palette.NeonRed,
    background = Palette.Black2,
    onBackground = Palette.White,
    surface = Palette.Grey,
    onSurface = Palette.White,
    surfaceVariant = Palette.Grey,
    onSurfaceVariant = Palette.Black3,
    outline = Palette.Black3.copy(alpha = 0.5f),
    surfaceContainerLow = Palette.Black4,
    surfaceContainer = Palette.Black4,
    surfaceContainerHigh = Palette.Grey,
)

@Composable
fun RuntextTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RunTxtDarkColorScheme,
        typography = AppTypography,
        content = content,
    )
}
