package com.nndwn.runtext.data.model

import androidx.compose.ui.graphics.Color
import com.nndwn.runtext.ui.theme.NeonGreen
import com.nndwn.runtext.ui.theme.toArgbLong


/**
 * Enum for app display mode selection.
 */
enum class AppMode {
    RUNNING_TEXT,
    MORSE_CODE
}

enum class FontType(val displayName: String) {
    SHARE_TECH_MONO("Share Tech Mono"),
    DOT_GOTHIC("DotGothic16"),
    ANTON("Anton"),
    JAKARTAPLUSBOLD("Plus Jakarta Sans Bold"),
    JAKARTAPLUSLIGHT("Plus Jakarta Sans Light"),
    SHIPPORI("Shippori"),
    GOOGLE_FONT("Google Font \u2193")
}



/**
 * Immutable data class holding all app settings.
 * Persisted via Preferences DataStore.
 *
 * Colors are stored as Long (unsigned ARGB value) to avoid Int sign confusion.
 * Convert to Compose Color via Color(argbLong.toInt()).
 */
data class AppSettings(
    val lastText: String = "HELLO WORLD",
    val mode: AppMode = AppMode.RUNNING_TEXT,

    // ── Running Text config ──
    val speed: Float = 150f,                    // pixels per second
    val textColorArgb: Long = NeonGreen.toArgbLong(),
    val bgColorArgb: Long = Color.Black.toArgbLong(),
    val fontType: FontType = FontType.SHARE_TECH_MONO,
    val googleFontName: String = "",
    val isMirrorMode: Boolean = false,

    // ── Morse Code config ──
    val morseWpm: Int = 15,
    val isFlashScreen: Boolean = true,
    val isTorchEnabled: Boolean = false,
)
