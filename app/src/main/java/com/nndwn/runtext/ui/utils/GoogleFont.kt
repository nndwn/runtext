package com.nndwn.runtext.ui.utils

import androidx.compose.ui.text.font.FontFamily
import com.nndwn.runtext.data.model.FontType
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
