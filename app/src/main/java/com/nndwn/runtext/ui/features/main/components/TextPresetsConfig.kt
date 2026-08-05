package com.nndwn.runtext.ui.features.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.data.model.FontType
import com.nndwn.runtext.data.model.ShadowConfig
import com.nndwn.runtext.data.model.StrokeConfig
import com.nndwn.runtext.data.model.TextColorType
import com.nndwn.runtext.data.model.TextStyleConfig
import com.nndwn.runtext.ui.component.CardExpanded
import com.nndwn.runtext.ui.features.main.MainUiEvent
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.theme.toArgbLong
import com.nndwn.runtext.ui.theme.toComposeColor
import com.nndwn.runtext.ui.utils.fontFamilyFor

data class TextPreset(
    val name: String,
    val settings: AppSettings
)

@Composable
fun TextPresetConfig(
    modifier: Modifier = Modifier,
    expandedId : String?,
    onToggle: (String) -> Unit,
    onEvent : (MainUiEvent) -> Unit,
){
    CardExpanded(
        title = stringResource(R.string.set_config_text_presets),
        modifier = modifier,
        idString = "text_presets",
        expandedId = expandedId,
        onToggle = onToggle
    ) {
        LazyRow(

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            items(Presets) { preset ->
                PresetItem(
                    preset = preset,
                    onClick = {onEvent(MainUiEvent.ApplyPreset(preset.settings))}
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }

}

@Composable
private fun PresetItem(
    preset: TextPreset,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier

            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(preset.settings.bgColorArgb.toComposeColor())
                .border(
                    width = 1.dp,
                    color = Palette.DimGray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            // Menggunakan Text preview biasa / RunningTextPreview mini
            Text(
                text = "Aa",
                fontFamily = fontFamilyFor(preset.settings.textStyle.fontType),
                color = if (preset.settings.textStyle.colorType == TextColorType.SOLID) {
                    preset.settings.textStyle.colorArgb.toComposeColor()
                } else {
                    preset.settings.textStyle.gradientColorsArgb.firstOrNull()?.toComposeColor() ?: Palette.White
                },
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = preset.name,
            style = MaterialTheme.typography.labelSmall,
            color = Palette.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
val Presets = listOf(
    // 7. Caution Alert - Bold Warning Sign
    TextPreset(
        name = "Caution Alert",
        settings = AppSettings(
            bgColorArgb = Color(0xFFFFCC00).toArgbLong(), // Caution Yellow
            textStyle = TextStyleConfig(
                colorType = TextColorType.SOLID,
                colorArgb = Color(0xFF111111).toArgbLong(),
                fontType = FontType.ARCHIVO_BLACK,
                letterSpacingSp = 2f
            ),
            stroke = StrokeConfig(
                isEnabled = true,
                width = 3f,
                colorArgb = Color(0xFF111111).toArgbLong()
            )
        )
    ),
    // 9. Comic Pop - Fun Animated Vibe
    TextPreset(
        name = "Comic Pop",
        settings = AppSettings(
            bgColorArgb = Color(0xFF2979FF).toArgbLong(), // Bright Blue
            textStyle = TextStyleConfig(
                colorType = TextColorType.SOLID,
                colorArgb = Color(0xFFFFEA00).toArgbLong(), // Yellow
                fontType = FontType.BANGERS,
                letterSpacingSp = 2f
            ),
            stroke = StrokeConfig(
                isEnabled = true,
                width = 4f,
                colorArgb = Color(0xFF000000).toArgbLong()
            ),
            shadow = ShadowConfig(
                isEnabled = true,
                colorArgb = Color(0xFF000000).toArgbLong(),
                radius = 2f,
                distance = 6f,
                rotation = 45f
            )
        )
    ),
    // 10. Cute Pastel - Soft Handwriting
    TextPreset(
        name = "Cute Pastel",
        settings = AppSettings(
            bgColorArgb = Color(0xFF3A2E39).toArgbLong(),
            textStyle = TextStyleConfig(
                colorType = TextColorType.GRADIENT,
                gradientColorsArgb = listOf(
                    Color(0xFFF8BBD0).toArgbLong(), // Soft Pink
                    Color(0xFFE1BEE7).toArgbLong()  // Soft Purple
                ),
                fontType = FontType.PACIFICO,
                letterSpacingSp = 1f
            ),
            shadow = ShadowConfig(
                isEnabled = true,
                colorArgb = Color(0xFFFFFFFF).toArgbLong(),
                radius = 6f,
                distance = 3f,
                rotation = 45f
            )
        )
    ),
    // 5. Electric EDM - High Contrast Party
    TextPreset(
        name = "Electric EDM",
        settings = AppSettings(
            bgColorArgb = Color(0xFF4A00E0).toArgbLong(), // Electric Purple
            textStyle = TextStyleConfig(
                colorType = TextColorType.SOLID,
                colorArgb = Color(0xFF8E2DE2).toArgbLong(), // Bright Yellow
                fontType = FontType.ANTON,
                letterSpacingSp = 2f
            ),
            shadow = ShadowConfig(
                isEnabled = true,
                colorArgb = Color(0xFF000000).toArgbLong(),
                radius = 4f,
                distance = 8f,
                rotation = 90f
            )
        )
    ),
    // 1. Cyber Neon - Modern Cyberpunk
    TextPreset(
        name = "Cyber Neon",
        settings = AppSettings(
            bgColorArgb = Color(0xFF0D0E15).toArgbLong(),
            textStyle = TextStyleConfig(
                colorType = TextColorType.GRADIENT,
                gradientColorsArgb = listOf(
                    Color(0xFFFF007F).toArgbLong(), // Neon Pink
                    Color(0xFF00F0FF).toArgbLong()  // Neon Cyan
                ),
                fontType = FontType.ORBITRON,
                letterSpacingSp = 2f
            ),
            shadow = ShadowConfig(
                isEnabled = true,
                colorArgb = Color(0xFFFF007F).toArgbLong(),
                radius = 16f,
                distance = 0f
            )
        )
    ),

    // 2. Retro Arcade - Pixel 8-Bit
    TextPreset(
        name = "Retro Arcade",
        settings = AppSettings(
            bgColorArgb = Color(0xFF120824).toArgbLong(),
            textStyle = TextStyleConfig(
                colorType = TextColorType.SOLID,
                colorArgb = Color(0xFFFFE600).toArgbLong(), // Yellow
                fontType = FontType.PRESS_START_2P,
                letterSpacingSp = 1f
            ),
            stroke = StrokeConfig(
                isEnabled = true,
                width = 3f,
                colorArgb = Color(0xFFFF0055).toArgbLong() // Magenta
            ),
            shadow = ShadowConfig(
                isEnabled = true,
                colorArgb = Color(0xFF00FFFF).toArgbLong(), // Cyan
                radius = 8f,
                distance = 4f,
                rotation = 135f
            )
        )
    ),

    // 3. Matrix Code - Terminal Green
    TextPreset(
        name = "Matrix Code",
        settings = AppSettings(
            bgColorArgb = Color(0xFF050B05).toArgbLong(),
            textStyle = TextStyleConfig(
                colorType = TextColorType.SOLID,
                colorArgb = Color(0xFF00FF66).toArgbLong(), // Matrix Green
                fontType = FontType.VT323,
                letterSpacingSp = 3f
            ),
            shadow = ShadowConfig(
                isEnabled = true,
                colorArgb = Color(0xFF00FF66).toArgbLong(),
                radius = 12f,
                distance = 0f
            )
        )
    ),

    // 4. Sunset Vibes - Bold Gradient
    TextPreset(
        name = "Sunset Vibes",
        settings = AppSettings(
            bgColorArgb = Color(0xFF181124).toArgbLong(),
            textStyle = TextStyleConfig(
                colorType = TextColorType.GRADIENT,
                gradientColorsArgb = listOf(
                    Color(0xFFFF512F).toArgbLong(), // Orange
                    Color(0xFFDD2476).toArgbLong()  // Sunset Pink
                ),
                fontType = FontType.BEBAS_NEUE,
                letterSpacingSp = 1.5f,
                isGradientHorizontal = true
            ),
            shadow = ShadowConfig(
                isEnabled = true,
                colorArgb = Color(0xFFFF512F).toArgbLong(),
                radius = 10f,
                distance = 4f,
                rotation = 45f
            )
        )
    ),



    // 6. Gold Luxury - Premium Elegant
    TextPreset(
        name = "Gold Luxury",
        settings = AppSettings(
            bgColorArgb = Color(0xFF0F0F0F).toArgbLong(),
            textStyle = TextStyleConfig(
                colorType = TextColorType.GRADIENT,
                gradientColorsArgb = listOf(
                    Color(0xFFFFE082).toArgbLong(), // Light Gold
                    Color(0xFFC5A059).toArgbLong()  // Deep Gold
                ),
                fontType = FontType.ABRIL_FATFACE,
                letterSpacingSp = 3f
            ),
            shadow = ShadowConfig(
                isEnabled = true,
                colorArgb = Color(0xFFFFD54F).toArgbLong(),
                radius = 8f,
                distance = 0f
            )
        )
    ),



    // 8. Frozen Ice - Cool Frosty Vibe
    TextPreset(
        name = "Frozen Ice",
        settings = AppSettings(
            bgColorArgb = Color(0xFF0A192F).toArgbLong(), // Deep Navy
            textStyle = TextStyleConfig(
                colorType = TextColorType.GRADIENT,
                gradientColorsArgb = listOf(
                    Color(0xFFE0F7FA).toArgbLong(), // Ice Blue Light
                    Color(0xFF80DEEA).toArgbLong()  // Cyan Ice
                ),
                fontType = FontType.RIGHTEOUS,
                letterSpacingSp = 1f
            ),
            stroke = StrokeConfig(
                isEnabled = true,
                width = 2f,
                colorArgb = Color(0xFF00BCD4).toArgbLong()
            ),
            shadow = ShadowConfig(
                isEnabled = true,
                colorArgb = Color(0xFF80DEEA).toArgbLong(),
                radius = 12f,
                distance = 0f
            )
        )
    ),





    // 11. Minimal Dark - Modern Clean
    TextPreset(
        name = "Minimal Dark",
        settings = AppSettings(
            bgColorArgb = Color(0xFF121212).toArgbLong(),
            textStyle = TextStyleConfig(
                colorType = TextColorType.SOLID,
                colorArgb = Color(0xFFFFFFFF).toArgbLong(),
                fontType = FontType.INTER,
                letterSpacingSp = 1f
            )
        )
    ),

    // 12. Minimal Light - High Visibility White
    TextPreset(
        name = "Minimal Light",
        settings = AppSettings(
            bgColorArgb = Color(0xFFFFFFFF).toArgbLong(),
            textStyle = TextStyleConfig(
                colorType = TextColorType.SOLID,
                colorArgb = Color(0xFF121212).toArgbLong(),
                fontType = FontType.MONTSERRAT,
                letterSpacingSp = 1f
            )
        )
    )
)

