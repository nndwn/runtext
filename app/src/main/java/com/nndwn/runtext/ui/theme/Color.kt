package com.nndwn.runtext.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

object Palette {
  val PitchBlack = Color(0xFF0A0A0F)
  val DarkBlueGray = Color(0xFF1A1A23)
  val DimGray = Color(0xFF5E5E60)
  val CoolGrey = Color(0xFF3E3E57)
  val White = Color(0xFFF5F5FF)
  val NeonRed = Color(0xFFE91E63) // Adjusted for better visibility
  val NeonPink = Color(0xFFFF006E)
  val Yellow = Color(0xFFFFCC00)
  val NeonCyan = Color(0xFF00D4FF)
}

object ColorPresets {
  /** Basic essential colors for general use */
  val Basic =
    listOf(
      Palette.White,
      Palette.PitchBlack,
      Color.Red,
      Color(0xFF4CAF50), // Green
      Color(0xFF2196F3), // Blue
      Palette.Yellow,
      Color(0xFFFF9800), // Orange
      Color(0xFF795548), // Brown
    )

  /** Vibrant colors optimized for LED/Morse displays */
  val Vibrant =
    listOf(
      Color(0xFFFFFFFF), // White
      Palette.Yellow,
      Color(0xFFFFD700), // Gold
      Color(0xFFFFC107), // Amber
      Color(0xFF00E676), // Spring Green
      Color(0xFF76FF03), // Light Green
      Color(0xFF00FFCC), // Bright Teal
      Palette.NeonCyan,
      Color(0xFF1DE9B6), // Turquoise
      Color(0xFF80D8FF), // Sky Blue
      Color(0xFF40C4FF), // Light Blue
      Palette.NeonPink,
      Color(0xFFEA80FC), // Light Purple
      Color(0xFFE040FB), // Purple
      Color(0xFFFF9100), // Bright Orange
      Color(0xFFFF5252), // Coral/Red
    )

  val All = (Basic + Vibrant).distinct()
}

/** Convert an unsigned ARGB Long to Compose [Color]. */
fun Long.toComposeColor(): Color = Color(this.toInt())

/** Convert a Compose [Color] to unsigned ARGB Long for storage. */
fun Color.toArgbLong(): Long = this.toArgb().toUInt().toLong()
