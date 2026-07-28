package com.nndwn.runtext.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb


object Palette {
    val Black2 = Color(0xFF0A0A0F)
    val Black4 = Color(0xFF16161F)
    val Black3 = Color(0xFF5E5E60)
    val Grey = Color(0xFF3E3E57)
    val White = Color(0xFFF5F5FF)
    val NeonRed = Color(0xFFFF0040)
    val NeonPink = Color(0xFFFF006E)
    val NeonGreen = Color(0xFF00FF41)
    val NeonCyan = Color(0xFF00D4FF)
}

/** Convert an unsigned ARGB Long to Compose [Color]. */
fun Long.toComposeColor(): Color = Color(this.toInt())

/** Convert a Compose [Color] to unsigned ARGB Long for storage. */
fun Color.toArgbLong(): Long = this.toArgb().toUInt().toLong()