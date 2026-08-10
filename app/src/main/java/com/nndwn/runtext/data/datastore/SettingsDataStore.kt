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
import com.nndwn.runtext.BuildConfig
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
    private companion object Keys {
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

        val IS_PREMIUM = booleanPreferencesKey("is_premium")
        val LAST_AD_SHOWN_TIMESTAMP_KEY = longPreferencesKey("last_ad_shown_timestamp")
        const val AD_COOLDOWN_MS = 900_000L

    }

    val isPremium : Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            preferences[IS_PREMIUM] ?: false
        }

    val shouldShowAd : Flow<Boolean> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            val isPremium = preferences[IS_PREMIUM] ?: false
            if (isPremium) return@map false

            val lastAdTimestamp = preferences[LAST_AD_SHOWN_TIMESTAMP_KEY] ?: return@map false
            
            val currentTime = System.currentTimeMillis()
            (currentTime - lastAdTimestamp) >= AD_COOLDOWN_MS
        }

    suspend fun setPremiumStatus(isPremium : Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_PREMIUM] = isPremium
        }
    }

    suspend fun recordAdShown() {
        dataStore.edit { preferences ->
            preferences[LAST_AD_SHOWN_TIMESTAMP_KEY] = System.currentTimeMillis()
        }
    }

    suspend fun recordAdShownIfFirstTime() {
        dataStore.edit { preferences ->
            if (preferences[LAST_AD_SHOWN_TIMESTAMP_KEY] == null) {
                preferences[LAST_AD_SHOWN_TIMESTAMP_KEY] = System.currentTimeMillis()
            }
        }
    }

    suspend fun debugPremium() {
        if (BuildConfig.DEBUG){
            dataStore.edit { preferences ->
                preferences[IS_PREMIUM] = true
            }
        }
    }

    suspend fun debugForceShowAd() {
        if (BuildConfig.DEBUG){
            dataStore.edit { preferences ->
                preferences[LAST_AD_SHOWN_TIMESTAMP_KEY] = 0L
            }
        }
    }

    suspend fun reset() {
        if (BuildConfig.DEBUG){
            dataStore.edit { preferences ->
                preferences.clear()
            }
        }
    }


    val settingsFlow: Flow<AppSettings> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            val default = AppSettings()
            AppSettings(
                lastText = prefs[LAST_TEXT] ?: default.lastText,
                mode = prefs[MODE]?.let { name ->
                    runCatching { AppMode.valueOf(name) }.getOrDefault(default.mode)
                } ?: default.mode,

                textConfig = TextConfig(
                    speed = prefs[SPEED] ?: default.textConfig.speed,
                    bgColorArgb = prefs[BG_COLOR] ?: default.textConfig.bgColorArgb,
                    isMirrorMode = prefs[MIRROR_MODE] ?: default.textConfig.isMirrorMode,

                    textStyle = TextStyleConfig(
                        colorArgb = prefs[TEXT_COLOR]
                            ?: default.textConfig.textStyle.colorArgb,
                        colorType = prefs[TEXT_COLOR_TYPE]?.let { name ->
                            runCatching { TextColorType.valueOf(name) }.getOrDefault(default.textConfig.textStyle.colorType)
                        } ?: default.textConfig.textStyle.colorType,
                        gradientColorsArgb = prefs[GRADIENT_COLORS]?.let { str ->
                            str.split(",").mapNotNull { it.toLongOrNull() }.ifEmpty { null }
                        } ?: default.textConfig.textStyle.gradientColorsArgb,
                        gradientDistance = prefs[GRADIENT_DISTANCE]
                            ?: default.textConfig.textStyle.gradientDistance,
                        isGradientHorizontal = prefs[GRADIENT_POSITION]
                            ?: default.textConfig.textStyle.isGradientHorizontal,
                        fontType = prefs[FONT_TYPE]?.let { name ->
                            runCatching { FontType.valueOf(name) }.getOrDefault(default.textConfig.textStyle.fontType)
                        } ?: default.textConfig.textStyle.fontType,
                        googleFontName = prefs[GOOGLE_FONT_NAME]
                            ?: default.textConfig.textStyle.googleFontName,
                        letterSpacingSp = prefs[LETTER_SPACING]
                            ?: default.textConfig.textStyle.letterSpacingSp,
                        wordSpacingSp = prefs[WORD_SPACING]
                            ?: default.textConfig.textStyle.wordSpacingSp
                    ),

                    stroke = StrokeConfig(
                        isEnabled = prefs[IS_STROKE_ENABLED]
                            ?: default.textConfig.stroke.isEnabled,
                        width = prefs[STROKE_WIDTH] ?: default.textConfig.stroke.width,
                        colorArgb = prefs[STROKE_COLOR] ?: default.textConfig.stroke.colorArgb
                    ),

                    shadow = ShadowConfig(
                        isEnabled = prefs[IS_SHADOW_ENABLED]
                            ?: default.textConfig.shadow.isEnabled,
                        colorArgb = prefs[SHADOW_COLOR] ?: default.textConfig.shadow.colorArgb,
                        radius = prefs[SHADOW_RADIUS] ?: default.textConfig.shadow.radius,
                        rotation = prefs[SHADOW_ROTATION] ?: default.textConfig.shadow.rotation
                    )
                ),

                morseConfig = MorseConfig(
                    morseWpm = prefs[MORSE_WPM] ?: default.morseConfig.morseWpm,
                    isFlashScreen = prefs[FLASH_SCREEN] ?: default.morseConfig.isFlashScreen,
                    isTorchEnabled = prefs[TORCH_ENABLED]
                        ?: default.morseConfig.isTorchEnabled,
                    bgColorMorse = prefs[MORSE_BG_COLOR] ?: default.morseConfig.bgColorMorse,
                    isSoundEnabled = prefs[IS_SOUND_ENABLED]
                        ?: default.morseConfig.isSoundEnabled,
                    isVibrateEnabled = prefs[IS_VIBRATE_ENABLED]
                        ?: default.morseConfig.isVibrateEnabled
                )
            )
        }

    suspend fun saveSettings(settings: AppSettings) {
        dataStore.edit { prefs ->
            prefs[LAST_TEXT] = settings.lastText
            prefs[MODE] = settings.mode.name

            // TextConfig
            prefs[SPEED] = settings.textConfig.speed
            prefs[BG_COLOR] = settings.textConfig.bgColorArgb
            prefs[MIRROR_MODE] = settings.textConfig.isMirrorMode

            // Text Style
            prefs[TEXT_COLOR] = settings.textConfig.textStyle.colorArgb
            prefs[TEXT_COLOR_TYPE] = settings.textConfig.textStyle.colorType.name
            prefs[GRADIENT_COLORS] = settings.textConfig.textStyle.gradientColorsArgb.joinToString(",")
            prefs[GRADIENT_DISTANCE] = settings.textConfig.textStyle.gradientDistance
            prefs[GRADIENT_POSITION] = settings.textConfig.textStyle.isGradientHorizontal
            prefs[FONT_TYPE] = settings.textConfig.textStyle.fontType.name
            prefs[GOOGLE_FONT_NAME] = settings.textConfig.textStyle.googleFontName
            prefs[LETTER_SPACING] = settings.textConfig.textStyle.letterSpacingSp
            prefs[WORD_SPACING] = settings.textConfig.textStyle.wordSpacingSp

            // Stroke
            prefs[IS_STROKE_ENABLED] = settings.textConfig.stroke.isEnabled
            prefs[STROKE_WIDTH] = settings.textConfig.stroke.width
            prefs[STROKE_COLOR] = settings.textConfig.stroke.colorArgb

            // Shadow PERBAIKAN: Gunakan settings.textConfig.shadow.isEnabled
            prefs[IS_SHADOW_ENABLED] = settings.textConfig.shadow.isEnabled
            prefs[SHADOW_COLOR] = settings.textConfig.shadow.colorArgb
            prefs[SHADOW_RADIUS] = settings.textConfig.shadow.radius
            prefs[SHADOW_ROTATION] = settings.textConfig.shadow.rotation

            // Morse
            prefs[MORSE_WPM] = settings.morseConfig.morseWpm
            prefs[FLASH_SCREEN] = settings.morseConfig.isFlashScreen
            prefs[TORCH_ENABLED] = settings.morseConfig.isTorchEnabled
            prefs[MORSE_BG_COLOR] = settings.morseConfig.bgColorMorse
            prefs[IS_SOUND_ENABLED] = settings.morseConfig.isSoundEnabled
            prefs[IS_VIBRATE_ENABLED] = settings.morseConfig.isVibrateEnabled
        }
    }
}