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

enum class FontType(
    val displayName: String,
    val googleFontName: String = displayName.substringBefore(" (")
) {
    // ── Modern & Clean ──
    ROBOTO("Roboto"),
    OPEN_SANS("Open Sans"),
    LATO("Lato"),
    MONTSERRAT("Montserrat"),
    POPPINS("Poppins"),
    INTER("Inter"),
    OSWALD("Oswald"),
    RALEWAY("Raleway"),
    QUICKSAND("Quicksand"),
    NUNITO("Nunito"),
    ABRIL_FATFACE("Abril Fatface"),

    // ── Display & Bold ──
    ANTON("Anton"),
    BEBAS_NEUE("Bebas Neue"),
    ARCHIVO_BLACK("Archivo Black"),
    RIGHTEOUS("Righteous"),
    LOBSTER("Lobster"),
    PACIFICO("Pacifico"),
    PERMANENT_MARKER("Permanent Marker"),
    PLAYFAIR_DISPLAY("Playfair Display"),
    SHARE_TECH_MONO("Share Tech Mono"),
    CREEPSTER("Creepster"),

    // ── Retro & Pixel ──
    PRESS_START_2P("Press Start 2P"),
    SILKSCREEN("Silkscreen"),
    DOT_GOTHIC("DotGothic16"),
    VT323("VT323"),
    COURIER_PRIME("Courier Prime"),

    // ── Decorative & Fun ──
    BANGERS("Bangers"),
    ORBITRON("Orbitron"),
    FREDOKA("Fredoka"),
    COMFORTAA("Comfortaa"),
    PATRICK_HAND("Patrick Hand"),
    SATISFY("Satisfy"),
    KAUSHAN_SCRIPT("Kaushan Script"),
    YELLOWTAIL("Yellowtail"),
    COURGETTE("Courgette"),
    DANCING_SCRIPT("Dancing Script"),
    GREAT_VIBES("Great Vibes"),
    SACRAMENTO("Sacramento"),

    // ── International Support ──
    DELA_GOTHIC_ONE("Dela Gothic One (日本語)"),
    BLACK_HAN_SANS("Black Han Sans (한국어)"),
    ZCOOL_KUAILE("ZCOOL KuaiLe (简体中文)"),
    LALEZAR("Lalezar (العربية)"),
    KALAM("Kalam (हिन्दी)"),
    ITIM("Itim (ไทย)"),
    REEM_KUFI("Reem Kufi (العربية)"),
    CAIRO("Cairo (القاهرة)"),
    ALMARAI("Almarai (المراعي)"),
    RAJDHANI("Rajdhani (हिन्दी)"),
    ZCOOL_XIAOWEI("ZCOOL XiaoWei (站酷小薇体)"),
    MA_SHAN_ZHENG("Ma Shan Zheng (马善政毛笔楷书)"),
    KOSUGI_MARU("Kosugi Maru (小杉丸ゴシック)"),
    ANKOR("Ankor (សួស្តី)"),
}



/**
 * Immutable data class holding all app settings.
 * Persisted via Preferences DataStore.
 *
 * Colors are stored as Long (unsigned ARGB value) to avoid Int sign confusion.
 * Convert to Compose Color via Color(argbLong.toInt()).
 */
data class AppSettings(
    val lastText: String = "",
    val mode: AppMode = AppMode.RUNNING_TEXT,

    // ── Running Text config ──
    val speed: Float = 150f,                    // pixels per second
    val textColorArgb: Long = NeonGreen.toArgbLong(),
    val bgColorArgb: Long = Color.Black.toArgbLong(),
    val fontType: FontType = FontType.ROBOTO,
    val googleFontName: String = "",
    val isMirrorMode: Boolean = false,

    // ── Morse Code config ──
    val morseWpm: Int = 15,
    val isFlashScreen: Boolean = true,
    val isTorchEnabled: Boolean = false,
)
