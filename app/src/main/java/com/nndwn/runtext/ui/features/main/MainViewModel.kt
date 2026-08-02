package com.nndwn.runtext.ui.features.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(val settings: AppSettings) : SettingsUiState
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository : SettingsRepository,
    @param:ApplicationContext private val context: Context
): ViewModel(){

    private val _settings = MutableStateFlow<AppSettings?>(null)

    val uiState: StateFlow<SettingsUiState> = _settings
        .map { settings ->
            if (settings == null) SettingsUiState.Loading 
            else SettingsUiState.Success(settings)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState.Loading
        )

    private var saveJob: Job? = null

    init {
        viewModelScope.launch {
            repository.settingsFlow.collect { savedSettings ->
                if (_settings.value == null) {
                    _settings.value = savedSettings
                }
            }
        }
    }

    private fun updateSettings(transform: (AppSettings) -> AppSettings) {
        val current = _settings.value ?: return
        val updated = transform(current)
        _settings.value = updated
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(400.milliseconds)
            repository.saveSettings(updated)
        }
    }

    private fun updateText(text: String) {
        if (text.length <= 250) {
            updateSettings { it.copy(lastText = text) }
        }
    }

    fun onEvent(event: MainUiEvent) {
        when (event) {
            // General
            is MainUiEvent.ApplyPreset -> updateSettings { 
                event.settings.copy(lastText = it.lastText) 
            }
            is MainUiEvent.UpdateText -> updateText(event.text)
            is MainUiEvent.ClearText -> updateText("")
            is MainUiEvent.UpdateMode -> updateSettings { it.copy(mode = event.mode) }
            is MainUiEvent.UpdateSpeed -> updateSettings { it.copy(speed = event.speed) }
            is MainUiEvent.UpdateBgColor -> updateSettings { it.copy(bgColorArgb = event.colorArgb) }
            is MainUiEvent.UpdateMirrorMode -> updateSettings { it.copy(isMirrorMode = event.mirror) }

            // Text Style
            is MainUiEvent.UpdateTextColor -> updateSettings { it.copy(textStyle = it.textStyle.copy(colorArgb = event.colorArgb)) }
            is MainUiEvent.UpdateTextColorType -> updateSettings { it.copy(textStyle = it.textStyle.copy(colorType = event.type)) }
            is MainUiEvent.UpdateGradientColors -> updateSettings { it.copy(textStyle = it.textStyle.copy(gradientColorsArgb = event.colors)) }
            is MainUiEvent.UpdateGradientDistance -> updateSettings { it.copy(textStyle = it.textStyle.copy(gradientDistance = event.distance)) }
            is MainUiEvent.ToggleGradientHorizontal -> updateSettings { it.copy(textStyle = it.textStyle.copy(isGradientHorizontal = event.isHorizontal)) }
            is MainUiEvent.UpdateFontType -> updateSettings { it.copy(textStyle = it.textStyle.copy(fontType = event.fontType)) }
            is MainUiEvent.UpdateGoogleFontName -> updateSettings { it.copy(textStyle = it.textStyle.copy(googleFontName = event.fontName)) }
            is MainUiEvent.UpdateLetterSpacing -> updateSettings { it.copy(textStyle = it.textStyle.copy(letterSpacingSp = event.spacingSp.coerceIn(-2f, 20f))) }
            is MainUiEvent.UpdateWordSpacing -> updateSettings { it.copy(textStyle = it.textStyle.copy(wordSpacingSp = event.spacingSp.coerceIn(0f, 30f))) }

            // Stroke
            is MainUiEvent.ToggleStroke -> updateSettings { it.copy(stroke = it.stroke.copy(isEnabled = event.isEnabled)) }
            is MainUiEvent.UpdateStrokeWidth -> updateSettings { it.copy(stroke = it.stroke.copy(width = event.width.coerceIn(0f, 15f))) }
            is MainUiEvent.UpdateStrokeColor -> updateSettings { it.copy(stroke = it.stroke.copy(colorArgb = event.colorArgb)) }

            // Shadow
            is MainUiEvent.ToggleShadow -> updateSettings { it.copy(shadow = it.shadow.copy(isEnabled = event.isEnabled)) }
            is MainUiEvent.UpdateShadowColor -> updateSettings { it.copy(shadow = it.shadow.copy(colorArgb = event.colorArgb)) }
            is MainUiEvent.UpdateShadowRadius -> updateSettings { it.copy(shadow = it.shadow.copy(radius = event.radius.coerceIn(0f, 25f))) }
            is MainUiEvent.UpdateShadowDistance -> updateSettings { it.copy(shadow = it.shadow.copy(distance = event.distance.coerceIn(0f, 50f))) }
            is MainUiEvent.UpdateShadowRotation -> {
                val normalizedRotation = (event.rotation % 360f + 360f) % 360f
                updateSettings { it.copy(shadow = it.shadow.copy(rotation = normalizedRotation)) }
            }
            is MainUiEvent.UpdateMorseWpm -> updateSettings {
                it.copy(morseConfig = it.morseConfig.copy(morseWpm = event.wpm.coerceIn(5, 40)))
            }
            is MainUiEvent.UpdateBgColorMorse -> updateSettings { it.copy(morseConfig = it.morseConfig.copy(bgColorMorse = event.colorArgb)) }
            is MainUiEvent.UpdateFlashScreen -> updateSettings { it.copy(morseConfig = it.morseConfig.copy(isFlashScreen = event.isFlashScreen)) }
            is MainUiEvent.UpdateTorchEnabled -> updateSettings { it.copy(morseConfig = it.morseConfig.copy(isTorchEnabled = event.isTorchEnabled)) }
            is MainUiEvent.UpdateSoundEnabled -> updateSettings { it.copy(morseConfig = it.morseConfig.copy(isSoundEnabled = event.isSoundEnabled)) }

        }
    }
}