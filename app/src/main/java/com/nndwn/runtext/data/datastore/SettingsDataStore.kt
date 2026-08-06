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
import com.nndwn.runtext.data.model.TextStyleConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SettingsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
){
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
                speed = prefs[Keys.SPEED] ?: default.speed,
                bgColorArgb = prefs[Keys.BG_COLOR] ?: default.bgColorArgb,
                isMirrorMode = prefs[Keys.MIRROR_MODE] ?: default.isMirrorMode,

                textStyle = TextStyleConfig(
                    colorArgb = prefs[Keys.TEXT_COLOR] ?: default.textStyle.colorArgb,
                    colorType = prefs[Keys.TEXT_COLOR_TYPE]?.let { name ->
                        runCatching { TextColorType.valueOf(name) }.getOrDefault(default.textStyle.colorType)
                    } ?: default.textStyle.colorType,
                    gradientColorsArgb = prefs[Keys.GRADIENT_COLORS]?.let { str ->
                        str.split(",").mapNotNull { it.toLongOrNull() }.ifEmpty { null }
                    } ?: default.textStyle.gradientColorsArgb,
                    gradientDistance = prefs[Keys.GRADIENT_DISTANCE]
                        ?: default.textStyle.gradientDistance,
                    isGradientHorizontal = prefs[Keys.GRADIENT_POSITION]
                        ?: default.textStyle.isGradientHorizontal,
                    fontType = prefs[Keys.FONT_TYPE]?.let { name ->
                        runCatching { FontType.valueOf(name) }.getOrDefault(default.textStyle.fontType)
                    } ?: default.textStyle.fontType,
                    googleFontName = prefs[Keys.GOOGLE_FONT_NAME]
                        ?: default.textStyle.googleFontName,
                    letterSpacingSp = prefs[Keys.LETTER_SPACING] ?: default.textStyle.letterSpacingSp,
                    wordSpacingSp = prefs[Keys.WORD_SPACING] ?: default.textStyle.wordSpacingSp
                ),

                stroke = StrokeConfig(
                    isEnabled = prefs[Keys.IS_STROKE_ENABLED] ?: default.stroke.isEnabled,
                    width = prefs[Keys.STROKE_WIDTH] ?: default.stroke.width,
                    colorArgb = prefs[Keys.STROKE_COLOR] ?: default.stroke.colorArgb
                ),

                shadow = ShadowConfig(
                    isEnabled = prefs[Keys.IS_SHADOW_ENABLED] ?: default.shadow.isEnabled,
                    colorArgb = prefs[Keys.SHADOW_COLOR] ?: default.shadow.colorArgb,
                    radius = prefs[Keys.SHADOW_RADIUS] ?: default.shadow.radius,
                    rotation = prefs[Keys.SHADOW_ROTATION] ?: default.shadow.rotation
                ),
                morseConfig = MorseConfig(
                    morseWpm = prefs[Keys.MORSE_WPM] ?: default.morseConfig.morseWpm,
                    isFlashScreen = prefs[Keys.FLASH_SCREEN] ?: default.morseConfig.isFlashScreen,
                    isTorchEnabled = prefs[Keys.TORCH_ENABLED] ?: default.morseConfig.isTorchEnabled,
                    bgColorMorse = prefs[Keys.MORSE_BG_COLOR] ?: default.morseConfig.bgColorMorse,
                    isSoundEnabled = prefs[Keys.IS_SOUND_ENABLED] ?: default.morseConfig.isSoundEnabled,
                    isVibrateEnabled = prefs[Keys.IS_VIBRATE_ENABLED] ?: default.morseConfig.isVibrateEnabled
                )

            )
        }

    suspend fun saveSettings(settings: AppSettings) {
        dataStore.edit { prefs ->
            prefs[Keys.LAST_TEXT] = settings.lastText
            prefs[Keys.MODE] = settings.mode.name
            prefs[Keys.SPEED] = settings.speed
            prefs[Keys.BG_COLOR] = settings.bgColorArgb
            prefs[Keys.MIRROR_MODE] = settings.isMirrorMode

            // Text Style
            prefs[Keys.TEXT_COLOR] = settings.textStyle.colorArgb
            prefs[Keys.TEXT_COLOR_TYPE] = settings.textStyle.colorType.name
            prefs[Keys.GRADIENT_COLORS] = settings.textStyle.gradientColorsArgb.joinToString(",")
            prefs[Keys.GRADIENT_DISTANCE] = settings.textStyle.gradientDistance
            prefs[Keys.GRADIENT_POSITION] = settings.textStyle.isGradientHorizontal
            prefs[Keys.FONT_TYPE] = settings.textStyle.fontType.name
            prefs[Keys.GOOGLE_FONT_NAME] = settings.textStyle.googleFontName
            prefs[Keys.LETTER_SPACING] = settings.textStyle.letterSpacingSp
            prefs[Keys.WORD_SPACING] = settings.textStyle.wordSpacingSp

            // Stroke
            prefs[Keys.IS_STROKE_ENABLED] = settings.stroke.isEnabled
            prefs[Keys.STROKE_WIDTH] = settings.stroke.width
            prefs[Keys.STROKE_COLOR] = settings.stroke.colorArgb

            // Shadow
            prefs[Keys.IS_SHADOW_ENABLED] = settings.shadow.isEnabled
            prefs[Keys.SHADOW_COLOR] = settings.shadow.colorArgb
            prefs[Keys.SHADOW_RADIUS] = settings.shadow.radius
            prefs[Keys.SHADOW_ROTATION] = settings.shadow.rotation

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