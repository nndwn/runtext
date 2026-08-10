package com.nndwn.runtext.ui.utils

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.FontType
import com.nndwn.runtext.data.model.ScriptCategory
import com.nndwn.runtext.ui.theme.*
import java.util.concurrent.ConcurrentHashMap


private val googleFontCache = ConcurrentHashMap<String, FontFamily>()

val GoogleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)
fun googleFontFamily(fontName: String): FontFamily {
    if (fontName.isEmpty()) return FontFamily.Default

    return googleFontCache.getOrPut(fontName) {
        val gf = GoogleFont(fontName)
        FontFamily(
            Font(googleFont = gf, fontProvider = GoogleFontProvider),
            Font(
                googleFont = gf,
                fontProvider = GoogleFontProvider,
                weight = FontWeight.Bold
            )
        )
    }
}
fun fontFamilyFor(fontType: FontType): FontFamily {
    return when (fontType) {
        FontType.LATO -> LatoFamity
        FontType.OSWALD -> OswaldFamily
        FontType.RALEWAY -> ralewayFamily
        FontType.ABRIL_FATFACE -> abrilFatFaceFamily
        FontType.ANTON -> AntonFamily
        FontType.BEBAS_NEUE -> bebasNeueFamily
        FontType.ARCHIVO_BLACK -> archivoBlackFamily
        FontType.LOBSTER -> lobsterFamily
        FontType.PACIFICO -> pacificoFamily
        FontType.PERMANENT_MARKER -> permanentMarkerFamily
        FontType.SHARE_TECH_MONO -> ShareTechMonoFamily
        FontType.CREEPSTER -> creepsterFamily
        FontType.SILKSCREEN -> silkScreenFamily
        FontType.DOT_GOTHIC -> DotGothic16Family
        FontType.COURIER_PRIME -> courierPrimeFamily
        FontType.BANGERS -> bangersFamily
        FontType.ORBITRON -> orbitronFamily
        FontType.FREDOKA -> fredokaFamily
        FontType.PATRICK_HAND -> patrickHandFamily
        FontType.COURGETTE -> courgetteFamily
        FontType.GREAT_VIBES -> greatVibesFamily
        FontType.SACRAMENTO -> sacremntoFamily
        
        // Fallback to Google Fonts for others (International support, etc.)
        else -> googleFontFamily(fontType.googleFontName)
    }
}
fun String.detectPrimaryScript(): ScriptCategory {
    if (isEmpty()) return ScriptCategory.LATIN

    for (i in indices) {
        val char = this[i]
        if (char.code in 0x0000..0x007F) continue

        val block = Character.UnicodeBlock.of(char) ?: continue

        val category = when (block) {
            Character.UnicodeBlock.ARABIC,
            Character.UnicodeBlock.ARABIC_SUPPLEMENT,
            Character.UnicodeBlock.ARABIC_EXTENDED_A,
            Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A,
            Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B -> ScriptCategory.ARABIC

            Character.UnicodeBlock.HIRAGANA,
            Character.UnicodeBlock.KATAKANA,
            Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS -> ScriptCategory.JAPANESE

            Character.UnicodeBlock.HANGUL_SYLLABLES,
            Character.UnicodeBlock.HANGUL_JAMO,
            Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO -> ScriptCategory.KOREAN

            Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS,
            Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A -> ScriptCategory.CHINESE

            Character.UnicodeBlock.THAI -> ScriptCategory.THAI
            Character.UnicodeBlock.DEVANAGARI -> ScriptCategory.DEVANAGARI
            Character.UnicodeBlock.KHMER -> ScriptCategory.KHMER
            Character.UnicodeBlock.HEBREW -> ScriptCategory.HEBREW
            else -> null
        }

        if (category != null) return category
    }
    return ScriptCategory.LATIN
}