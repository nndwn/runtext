package com.nndwn.runtext.ui.utils

import androidx.compose.ui.text.font.FontFamily
import com.nndwn.runtext.data.model.FontType
import com.nndwn.runtext.data.model.ScriptCategory
import com.nndwn.runtext.ui.theme.*

/**
 * Returns the [FontFamily] for the given [FontType].
 * Uses bundled (offline) fonts when available, otherwise downloads from Google Fonts.
 */
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
    for (char in this) {
        val block = Character.UnicodeBlock.of(char) ?: continue
        when (block) {
            // Arab
            Character.UnicodeBlock.ARABIC,
            Character.UnicodeBlock.ARABIC_SUPPLEMENT,
            Character.UnicodeBlock.ARABIC_EXTENDED_A,
            Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A,
            Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B -> return ScriptCategory.ARABIC

            // Jepang (Hiragana, Katakana, Kanji)
            Character.UnicodeBlock.HIRAGANA,
            Character.UnicodeBlock.KATAKANA,
            Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS -> return ScriptCategory.JAPANESE

            // Korea (Hangul)
            Character.UnicodeBlock.HANGUL_SYLLABLES,
            Character.UnicodeBlock.HANGUL_JAMO,
            Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO -> return ScriptCategory.KOREAN

            // Cina (Hanzi)
            Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS,
            Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A -> return ScriptCategory.CHINESE

            // Thai
            Character.UnicodeBlock.THAI -> return ScriptCategory.THAI

            // India / Hindi (Devanagari)
            Character.UnicodeBlock.DEVANAGARI -> return ScriptCategory.DEVANAGARI
            Character.UnicodeBlock.KHMER -> return  ScriptCategory.KHMER
            else -> continue
        }
    }
    return ScriptCategory.LATIN
}