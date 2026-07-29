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
import com.nndwn.runtext.data.model.TextColorType
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
        val TEXT_COLOR = longPreferencesKey("text_color")
        val BG_COLOR = longPreferencesKey("bg_color")
        val TEXT_COLOR_TYPE = stringPreferencesKey("text_color_type")
        val GRADIENT_COLORS = stringPreferencesKey("gradient_colors")
        val GRADIENT_DISTANCE = floatPreferencesKey("gradient_distance")
        val GRADIENT_POSITION = booleanPreferencesKey("gradient_position")
        val IS_STROKE_ENABLED = booleanPreferencesKey("is_stroke_enabled")
        val STROKE_WIDTH = floatPreferencesKey("stroke_width")
        val STROKE_COLOR = longPreferencesKey("stroke_color")
        val FONT_TYPE = stringPreferencesKey("font_type")
        val GOOGLE_FONT_NAME = stringPreferencesKey("google_font_name")
        val MIRROR_MODE = booleanPreferencesKey("mirror_mode")
        val MORSE_WPM = intPreferencesKey("morse_wpm")
        val FLASH_SCREEN = booleanPreferencesKey("flash_screen")
        val TORCH_ENABLED = booleanPreferencesKey("torch_enabled")
    }

    val settingsFlow : Flow<AppSettings> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            val default = AppSettings()
            AppSettings(
                lastText =  prefs[Keys.LAST_TEXT] ?: default.lastText,
                mode = prefs[Keys.MODE]?.let { name ->
                    runCatching { AppMode.valueOf(name) }.getOrDefault(default.mode)
                }?: default.mode,
                speed = prefs[Keys.SPEED] ?: default.speed,
                textColorArgb = prefs[Keys.TEXT_COLOR] ?: default.textColorArgb,
                textColorType = prefs[Keys.TEXT_COLOR_TYPE]?.let { name ->
                    runCatching { TextColorType.valueOf(name) }.getOrDefault(default.textColorType)
                } ?: default.textColorType,
                gradientColorsArgb = prefs[Keys.GRADIENT_COLORS]?.let { str ->
                    str.split(",").mapNotNull { it.toLongOrNull() }.ifEmpty { null }
                } ?: default.gradientColorsArgb,
                gradientDistance = prefs[Keys.GRADIENT_DISTANCE] ?: default.gradientDistance,
                gradientPositionHorizontal = prefs[Keys.GRADIENT_POSITION] ?: default.gradientPositionHorizontal,
                isStrokeEnabled = prefs[Keys.IS_STROKE_ENABLED] ?: default.isStrokeEnabled,
                strokeWidth = prefs[Keys.STROKE_WIDTH] ?: default.strokeWidth,
                strokeColorArgb = prefs[Keys.STROKE_COLOR] ?: default.strokeColorArgb,
                bgColorArgb = prefs[Keys.BG_COLOR] ?: default.bgColorArgb,
                fontType = prefs[Keys.FONT_TYPE] ?.let { name ->
                    runCatching { FontType.valueOf(name) }.getOrDefault(default.fontType)
                } ?: default.fontType,
                googleFontName = prefs[Keys.GOOGLE_FONT_NAME] ?: default.googleFontName,
                isMirrorMode = prefs[Keys.MIRROR_MODE] ?: default.isMirrorMode,
                morseWpm = prefs[Keys.MORSE_WPM] ?: default.morseWpm,
                isFlashScreen = prefs[Keys.FLASH_SCREEN] ?: default.isFlashScreen,
                isTorchEnabled = prefs[Keys.TORCH_ENABLED] ?: default.isTorchEnabled,
            )
        }
    
    suspend fun saveSettings(settings: AppSettings) {
        dataStore.edit { prefs ->
            prefs[Keys.LAST_TEXT] = settings.lastText
            prefs[Keys.MODE] = settings.mode.name
            prefs[Keys.SPEED] = settings.speed
            prefs[Keys.TEXT_COLOR] = settings.textColorArgb
            prefs[Keys.TEXT_COLOR_TYPE] = settings.textColorType.name
            prefs[Keys.GRADIENT_COLORS] = settings.gradientColorsArgb.joinToString(",")
            prefs[Keys.GRADIENT_DISTANCE] = settings.gradientDistance
            prefs[Keys.GRADIENT_POSITION] = settings.gradientPositionHorizontal
            prefs[Keys.IS_STROKE_ENABLED] = settings.isStrokeEnabled
            prefs[Keys.STROKE_WIDTH] = settings.strokeWidth
            prefs[Keys.STROKE_COLOR] = settings.strokeColorArgb
            prefs[Keys.BG_COLOR] = settings.bgColorArgb
            prefs[Keys.FONT_TYPE] = settings.fontType.name
            prefs[Keys.GOOGLE_FONT_NAME] = settings.googleFontName
            prefs[Keys.MIRROR_MODE] = settings.isMirrorMode
            prefs[Keys.MORSE_WPM] = settings.morseWpm
            prefs[Keys.FLASH_SCREEN] = settings.isFlashScreen
            prefs[Keys.TORCH_ENABLED] = settings.isTorchEnabled
        }
    }
}