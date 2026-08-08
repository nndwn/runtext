package com.nndwn.runtext.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nndwn.runtext.data.model.AppMode
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.data.model.FontType
import com.nndwn.runtext.data.model.MorseConfig
import com.nndwn.runtext.data.model.ShadowConfig
import com.nndwn.runtext.data.model.StrokeConfig
import com.nndwn.runtext.data.model.TextColorType
import com.nndwn.runtext.data.model.TextConfig
import com.nndwn.runtext.data.model.TextStyleConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SettingsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val LAST_TEXT = stringPreferencesKey("last_text")
        val MODE = stringPreferencesKey("mode")
        val SPEED = floatPreferencesKey("speed")
        val BG_COLOR = longPreferencesKey("bg_color")
        val MIRROR_MODE = booleanPreferencesKey("mirror_mode")

        // Text style
        val TEXT_COLOR = longPreferencesKey("text_color")
        val TEXT_COLOR_TYPE = stringPreferencesKey("text_color_type")
        val GRADIENT_COLORS = stringPreferencesKey("gradient_colors")
        val GRADIENT_DISTANCE = floatPreferencesKey("gradient_distance")
        val GRADIENT_POSITION = booleanPreferencesKey("gradient_position")
        val FONT_TYPE = stringPreferencesKey("font_type")
        val GOOGLE_FONT_NAME = stringPreferencesKey("google_font_name")
        val LETTER_SPACING = floatPreferencesKey("letter_spacing")
        val WORD_SPACING = floatPreferencesKey("word_spacing")

        // Stroke
        val IS_STROKE_ENABLED = booleanPreferencesKey("is_stroke_enabled")
        val STROKE_WIDTH = floatPreferencesKey("stroke_width")
        val STROKE_COLOR = longPreferencesKey("stroke_color")

        // Shadow
        val IS_SHADOW_ENABLED = booleanPreferencesKey("is_shadow_enabled")
        val SHADOW_COLOR = longPreferencesKey("shadow_color")
        val SHADOW_RADIUS = floatPreferencesKey("shadow_radius")
        val SHADOW_ROTATION = floatPreferencesKey("shadow_rotation")

        // Morse
        val MORSE_WPM = intPreferencesKey("morse_wpm")
        val FLASH_SCREEN = booleanPreferencesKey("morse_flash_screen")
        val TORCH_ENABLED = booleanPreferencesKey("morse_torch_enabled")
        val MORSE_BG_COLOR = longPreferencesKey("morse_bg_color")
        val IS_SOUND_ENABLED = booleanPreferencesKey("morse_is_sound_enabled")
        val IS_VIBRATE_ENABLED = booleanPreferencesKey("morse_is_vibrate_enabled")
    }

    val settingsFlow: Flow<AppSettings> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            val default = AppSettings()
            AppSettings(
                lastText = prefs[Keys.LAST_TEXT] ?: default.lastText,
                mode = prefs[Keys.MODE]?.let { name ->
                    runCatching { AppMode.valueOf(name) }.getOrDefault(default.mode)
                } ?: default.mode,

                textConfig = TextConfig(
                    speed = prefs[Keys.SPEED] ?: default.textConfig.speed,
                    bgColorArgb = prefs[Keys.BG_COLOR] ?: default.textConfig.bgColorArgb,
                    isMirrorMode = prefs[Keys.MIRROR_MODE] ?: default.textConfig.isMirrorMode,

                    textStyle = TextStyleConfig(
                        colorArgb = prefs[Keys.TEXT_COLOR]
                            ?: default.textConfig.textStyle.colorArgb,
                        colorType = prefs[Keys.TEXT_COLOR_TYPE]?.let { name ->
                            runCatching { TextColorType.valueOf(name) }.getOrDefault(default.textConfig.textStyle.colorType)
                        } ?: default.textConfig.textStyle.colorType,
                        gradientColorsArgb = prefs[Keys.GRADIENT_COLORS]?.let { str ->
                            str.split(",").mapNotNull { it.toLongOrNull() }.ifEmpty { null }
                        } ?: default.textConfig.textStyle.gradientColorsArgb,
                        gradientDistance = prefs[Keys.GRADIENT_DISTANCE]
                            ?: default.textConfig.textStyle.gradientDistance,
                        isGradientHorizontal = prefs[Keys.GRADIENT_POSITION]
                            ?: default.textConfig.textStyle.isGradientHorizontal,
                        fontType = prefs[Keys.FONT_TYPE]?.let { name ->
                            runCatching { FontType.valueOf(name) }.getOrDefault(default.textConfig.textStyle.fontType)
                        } ?: default.textConfig.textStyle.fontType,
                        googleFontName = prefs[Keys.GOOGLE_FONT_NAME]
                            ?: default.textConfig.textStyle.googleFontName,
                        letterSpacingSp = prefs[Keys.LETTER_SPACING]
                            ?: default.textConfig.textStyle.letterSpacingSp,
                        wordSpacingSp = prefs[Keys.WORD_SPACING]
                            ?: default.textConfig.textStyle.wordSpacingSp
                    ),

                    stroke = StrokeConfig(
                        isEnabled = prefs[Keys.IS_STROKE_ENABLED]
                            ?: default.textConfig.stroke.isEnabled,
                        width = prefs[Keys.STROKE_WIDTH] ?: default.textConfig.stroke.width,
                        colorArgb = prefs[Keys.STROKE_COLOR] ?: default.textConfig.stroke.colorArgb
                    ),

                    shadow = ShadowConfig(
                        isEnabled = prefs[Keys.IS_SHADOW_ENABLED]
                            ?: default.textConfig.shadow.isEnabled,
                        colorArgb = prefs[Keys.SHADOW_COLOR] ?: default.textConfig.shadow.colorArgb,
                        radius = prefs[Keys.SHADOW_RADIUS] ?: default.textConfig.shadow.radius,
                        rotation = prefs[Keys.SHADOW_ROTATION] ?: default.textConfig.shadow.rotation
                    )
                ),

                morseConfig = MorseConfig(
                    morseWpm = prefs[Keys.MORSE_WPM] ?: default.morseConfig.morseWpm,
                    isFlashScreen = prefs[Keys.FLASH_SCREEN] ?: default.morseConfig.isFlashScreen,
                    isTorchEnabled = prefs[Keys.TORCH_ENABLED]
                        ?: default.morseConfig.isTorchEnabled,
                    bgColorMorse = prefs[Keys.MORSE_BG_COLOR] ?: default.morseConfig.bgColorMorse,
                    isSoundEnabled = prefs[Keys.IS_SOUND_ENABLED]
                        ?: default.morseConfig.isSoundEnabled,
                    isVibrateEnabled = prefs[Keys.IS_VIBRATE_ENABLED]
                        ?: default.morseConfig.isVibrateEnabled
                )
            )
        }

    suspend fun saveSettings(settings: AppSettings) {
        dataStore.edit { prefs ->
            prefs[Keys.LAST_TEXT] = settings.lastText
            prefs[Keys.MODE] = settings.mode.name

            // TextConfig
            prefs[Keys.SPEED] = settings.textConfig.speed
            prefs[Keys.BG_COLOR] = settings.textConfig.bgColorArgb
            prefs[Keys.MIRROR_MODE] = settings.textConfig.isMirrorMode

            // Text Style
            prefs[Keys.TEXT_COLOR] = settings.textConfig.textStyle.colorArgb
            prefs[Keys.TEXT_COLOR_TYPE] = settings.textConfig.textStyle.colorType.name
            prefs[Keys.GRADIENT_COLORS] =
                settings.textConfig.textStyle.gradientColorsArgb.joinToString(",")
            prefs[Keys.GRADIENT_DISTANCE] = settings.textConfig.textStyle.gradientDistance
            prefs[Keys.GRADIENT_POSITION] = settings.textConfig.textStyle.isGradientHorizontal
            prefs[Keys.FONT_TYPE] = settings.textConfig.textStyle.fontType.name
            prefs[Keys.GOOGLE_FONT_NAME] = settings.textConfig.textStyle.googleFontName
            prefs[Keys.LETTER_SPACING] = settings.textConfig.textStyle.letterSpacingSp
            prefs[Keys.WORD_SPACING] = settings.textConfig.textStyle.wordSpacingSp

            // Stroke
            prefs[Keys.IS_STROKE_ENABLED] = settings.textConfig.stroke.isEnabled
            prefs[Keys.STROKE_WIDTH] = settings.textConfig.stroke.width
            prefs[Keys.STROKE_COLOR] = settings.textConfig.stroke.colorArgb

            // Shadow PERBAIKAN: Gunakan settings.textConfig.shadow.isEnabled
            prefs[Keys.IS_SHADOW_ENABLED] = settings.textConfig.shadow.isEnabled
            prefs[Keys.SHADOW_COLOR] = settings.textConfig.shadow.colorArgb
            prefs[Keys.SHADOW_RADIUS] = settings.textConfig.shadow.radius
            prefs[Keys.SHADOW_ROTATION] = settings.textConfig.shadow.rotation

            // Morse
            prefs[Keys.MORSE_WPM] = settings.morseConfig.morseWpm
            prefs[Keys.FLASH_SCREEN] = settings.morseConfig.isFlashScreen
            prefs[Keys.TORCH_ENABLED] = settings.morseConfig.isTorchEnabled
            prefs[Keys.MORSE_BG_COLOR] = settings.morseConfig.bgColorMorse
            prefs[Keys.IS_SOUND_ENABLED] = settings.morseConfig.isSoundEnabled
            prefs[Keys.IS_VIBRATE_ENABLED] = settings.morseConfig.isVibrateEnabled
        }
    }
}