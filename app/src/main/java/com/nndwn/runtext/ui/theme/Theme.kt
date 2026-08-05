package com.nndwn.runtext.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp


private val RunTxtDarkColorScheme = darkColorScheme(
    primary = Palette.Yellow,
    onPrimary = Palette.Black2,
    primaryContainer = Palette.Yellow.copy(alpha = 0.15f),
    onPrimaryContainer = Palette.Yellow,
    secondary = Palette.NeonCyan,
    onSecondary = Palette.Black2,
    secondaryContainer = Palette.White,
    onSecondaryContainer = Palette.Black2.copy(alpha = 0.7f),
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

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)
@Composable
fun RuntextTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
    LocalDimens provides Dimens()
    ){
        MaterialTheme(
            colorScheme = RunTxtDarkColorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}
