package com.nndwn.runtext.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.nndwn.runtext.R
import androidx.compose.ui.text.googlefonts.Font as GoogleFontFont
import androidx.compose.material3.Typography

// ── Google Fonts provider (requires Internet + GMS) ──
val GoogleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

// ── Bundled font families (offline, embedded in APK) ──

val ShareTechMonoFamily = FontFamily(
    Font(R.font.sharetechmono_regular),
)

val DotGothic16Family = FontFamily(
    Font(R.font.dotgothic16_regular),
)

val AntonFamily = FontFamily(
    Font(R.font.anton_regular),
)

val LatoFamity = FontFamily(
    Font(R.font.lato_bold)
)

val OswaldFamily = FontFamily(
    Font(R.font.oswald_regular)
)

val ralewayFamily = FontFamily(
    Font(R.font.raleway_medium)
)

val abrilFatFaceFamily = FontFamily(
    Font(R.font.abrilfatface_regular)
)

val bebasNeueFamily = FontFamily(
    Font(R.font.bebasneue_regular)
)

val archivoBlackFamily = FontFamily(
    Font(R.font.archivoblack_regular)
)

val lobsterFamily = FontFamily(
    Font(R.font.lobster_regular)
)

val pacificoFamily = FontFamily(
    Font(R.font.pacifico_regular)
)

val permanentMarkerFamily = FontFamily(
    Font(R.font.permanentmarker_regular)
)

val creepsterFamily = FontFamily(
    Font(R.font.creepster_regular)
)

val silkScreenFamily = FontFamily(
    Font(R.font.silkscreen_regular)
)

val courierPrimeFamily = FontFamily(
    Font(R.font.courierprime_regular)
)

val bangersFamily = FontFamily(
    Font(R.font.bangers_regular)
)

val orbitronFamily = FontFamily(
    Font(R.font.orbitron_medium)
)
val fredokaFamily = FontFamily(
    Font(R.font.fredoka)
)

val patrickHandFamily = FontFamily(
    Font(R.font.patrickhand_regular)
)

val courgetteFamily = FontFamily(
    Font(R.font.courgette_regular)
)

val greatVibesFamily = FontFamily(
    Font(R.font.greatvibes_regular)
)

val sacremntoFamily = FontFamily(
    Font(R.font.sacramento_regular)
)

val JakartaPlusFamily = FontFamily(
    Font(R.font.plus_jakarta_sans_light, FontWeight.Light),
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.Black),
)

/**
 * Create a [FontFamily] from a Google Font name.
 * Falls back to device default if the font cannot be downloaded.
 */
fun googleFontFamily(fontName: String): FontFamily {
    val gf = GoogleFont(fontName)
    return FontFamily(
        GoogleFontFont(googleFont = gf, fontProvider = GoogleFontProvider),
        GoogleFontFont(
            googleFont = gf,
            fontProvider = GoogleFontProvider,
            weight = FontWeight.Bold,
        ),
    )
}


// ── App-wide Material 3 typography ──
val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = ShareTechMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = JakartaPlusFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = JakartaPlusFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = JakartaPlusFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = JakartaPlusFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = JakartaPlusFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = JakartaPlusFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = JakartaPlusFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = JakartaPlusFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = JakartaPlusFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = JakartaPlusFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = JakartaPlusFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
)