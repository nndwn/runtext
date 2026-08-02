package com.nndwn.runtext.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.nndwn.runtext.R
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.theme.toArgbLong


/**
 * Enum for app display mode selection.
 */
enum class AppMode(@param:StringRes val displayName: Int, @param:DrawableRes val icon : Int) {
    RUNNING_TEXT(R.string.btn_text_running_text, R.drawable.ic_runtext),
    MORSE_CODE(R.string.btn_text_morse_code, R.drawable.ic_flash)
}

enum class TextColorType( @param:StringRes val displayName: Int, @param:DrawableRes val icon : Int){
    SOLID (R.string.set_config_text_color_type_solid, R.drawable.ic_color_solid),
    GRADIENT (R.string.set_config_text_color_type_gradient, R.drawable.ic_color_gradient)
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



data class AppSettings(
    val lastText: String = "",
    val mode: AppMode = AppMode.RUNNING_TEXT,
    val speed: Float = 150f,
    val bgColorArgb: Long = Palette.White.toArgbLong(),
    val isMirrorMode: Boolean = false,

    // Nested Configurations
    val textStyle: TextStyleConfig = TextStyleConfig(),
    val stroke: StrokeConfig = StrokeConfig(),
    val shadow: ShadowConfig = ShadowConfig(),

    // Morse Code config
    val morseWpm: Int = 15,
    val isFlashScreen: Boolean = true,
    val isTorchEnabled: Boolean = false,
)


data class TextStyleConfig(
    val colorArgb: Long = Palette.Black2.toArgbLong(),
    val colorType: TextColorType = TextColorType.SOLID,
    val gradientColorsArgb: List<Long> = listOf(Palette.Yellow.toArgbLong(), Palette.NeonPink.toArgbLong()),
    val gradientDistance: Float = 0.5f,
    val isGradientHorizontal: Boolean = false,
    val fontType: FontType = FontType.ANTON,
    val googleFontName: String = "",
    val letterSpacingSp: Float = 0f,
    val wordSpacingSp: Float = 0f
)

data class StrokeConfig(
    val isEnabled: Boolean = false,
    val width: Float = 0f,
    val colorArgb: Long = Color.Black.toArgbLong()
)

data class ShadowConfig(
    val isEnabled: Boolean = false,
    val colorArgb: Long = Color.Black.copy(alpha = 0.75f).toArgbLong(),
    val radius: Float = 8f,
    val distance: Float = 10f,
    val rotation: Float = 45f
)