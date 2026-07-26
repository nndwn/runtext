package com.nndwn.runtext.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb


object Palette {
    val Black2 = Color(0xFF121219)
    val Black3 = Color(0xFF262626)
    val Grey = Color(0xFF414141)
    val White = Color(0xFFF5F5FF)
}
// ── Neon accent colors ──

val NeonGreen = Color(0xFF00FF41)
val NeonPink = Color(0xFFFF006E)
val NeonCyan = Color(0xFF00D4FF)
val NeonYellow = Color(0xFFFFE600)
val NeonOrange = Color(0xFFFF6B00)
val NeonPurple = Color(0xFFBF00FF)
val NeonRed = Color(0xFFFF0040)
val NeonBlue = Color(0xFF0080FF)

// ── Surface / background colors ──
val DarkBackground = Color(0xFF0A0A0F)
val DarkSurface = Color(0xFF12121A)
val DarkSurfaceVariant = Color(0xFF1A1A2E)
val DarkSurfaceContainer = Color(0xFF16161F)
val DarkOnSurface = Color(0xFFE0E0E0)
val DarkOnSurfaceVariant = Color(0xFF9E9E9E)

// ── Preset color palettes for the picker ──
val PresetTextColors = listOf(
    Color(0xFF00FF41), // Neon Green
    Color(0xFFFF0040), // Neon Red
    Color(0xFF0080FF), // Neon Blue
    Color(0xFFFFE600), // Neon Yellow
    Color(0xFF00D4FF), // Neon Cyan
    Color(0xFFFF006E), // Neon Pink
    Color(0xFFBF00FF), // Neon Purple
    Color(0xFFFF6B00), // Neon Orange
    Color.White,
    Color(0xFFFF4081), // Hot Pink
    Color(0xFF76FF03), // Lime
    Color(0xFFFFAB00), // Amber
)

val PresetBgColors = listOf(
    Color.Black,
    Color(0xFF0A0A0F), // Near black
    Color(0xFF1A0000), // Dark red
    Color(0xFF001A00), // Dark green
    Color(0xFF00001A), // Dark blue
    Color(0xFF1A1A00), // Dark olive
    Color(0xFF0D0D1A), // Dark navy
    Color(0xFF1A000D), // Dark maroon
    Color(0xFF0D1A1A), // Dark teal
    Color(0xFF1A0D1A), // Dark purple
    Color(0xFF1A1A1A), // Dark gray
    Color(0xFF0D0D0D), // Very dark gray
)

// ── Helper extensions for Long↔Color conversion ──
/** Convert an unsigned ARGB Long to Compose [Color]. */
fun Long.toComposeColor(): Color = Color(this.toInt())

/** Convert a Compose [Color] to unsigned ARGB Long for storage. */
fun Color.toArgbLong(): Long = this.toArgb().toUInt().toLong()