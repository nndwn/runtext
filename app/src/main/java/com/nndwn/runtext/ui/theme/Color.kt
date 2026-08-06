package com.nndwn.runtext.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb


object Palette {
    val PitchBlack = Color(0xFF0A0A0F)
    val DarkBlueGray = Color(0xFF1A1A23)
    val DimGray = Color(0xFF5E5E60)
    val CoolGrey = Color(0xFF3E3E57)
    val White = Color(0xFFF5F5FF)
    val NeonRed = Color(0xFFFF0040)
    val NeonPink = Color(0xFFFF006E)
    val Yellow = Color(0xFFFFCC00)
    val NeonCyan = Color(0xFF00D4FF)
}

/** Convert an unsigned ARGB Long to Compose [Color]. */
fun Long.toComposeColor(): Color = Color(this.toInt())

/** Convert a Compose [Color] to unsigned ARGB Long for storage. */
fun Color.toArgbLong(): Long = this.toArgb().toUInt().toLong()