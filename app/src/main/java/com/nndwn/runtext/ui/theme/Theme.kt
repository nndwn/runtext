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
    onPrimary = Palette.PitchBlack,
    primaryContainer = Palette.Yellow.copy(alpha = 0.15f),
    onPrimaryContainer = Palette.Yellow,
    secondary = Palette.NeonCyan,
    onSecondary = Palette.PitchBlack,
    secondaryContainer = Palette.White,
    onSecondaryContainer = Palette.PitchBlack.copy(alpha = 0.7f),
    tertiary = Palette.NeonPink,
    onTertiary = Palette.PitchBlack,
    tertiaryContainer = Palette.PitchBlack,
    onTertiaryContainer = Palette.White,
    error = Palette.NeonRed,
    background = Palette.PitchBlack,
    onBackground = Palette.White,
    surface = Palette.CoolGrey,
    onSurface = Palette.White,
    surfaceVariant = Palette.CoolGrey,
    onSurfaceVariant = Palette.DimGray,
    outline = Palette.DimGray,
    surfaceContainerLow = Palette.DarkBlueGray,
    surfaceContainer = Palette.DarkBlueGray,
    surfaceContainerHigh = Palette.CoolGrey,
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
