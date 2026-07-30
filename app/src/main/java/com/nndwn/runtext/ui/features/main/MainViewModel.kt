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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository : SettingsRepository,
    @param:ApplicationContext private val context: Context
): ViewModel(){

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private var saveJob: Job? = null

    init {
        viewModelScope.launch {
            _settings.value = repository.settingsFlow.first()
        }
    }

    private fun save() {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(300.milliseconds)
            repository.saveSettings(_settings.value)
        }
    }

    // ── Main Event Processor ──
    fun onEvent(event: MainUiEvent) {
        when (event) {
            // General
            is MainUiEvent.UpdateText -> updateText(event.text)
            is MainUiEvent.ClearText -> updateText("")
            is MainUiEvent.UpdateMode -> _settings.update { it.copy(mode = event.mode) }.also { save() }
            is MainUiEvent.UpdateSpeed -> _settings.update { it.copy(speed = event.speed) }.also { save() }
            is MainUiEvent.UpdateBgColor -> _settings.update { it.copy(bgColorArgb = event.colorArgb) }.also { save() }
            is MainUiEvent.ToggleMirrorMode -> _settings.update { it.copy(isMirrorMode = !it.isMirrorMode) }.also { save() }

            // Text Style
            is MainUiEvent.UpdateTextColor -> _settings.update { it.copy(textStyle = it.textStyle.copy(colorArgb = event.colorArgb)) }.also { save() }
            is MainUiEvent.UpdateTextColorType -> _settings.update { it.copy(textStyle = it.textStyle.copy(colorType = event.type)) }.also { save() }
            is MainUiEvent.UpdateGradientColors -> _settings.update { it.copy(textStyle = it.textStyle.copy(gradientColorsArgb = event.colors)) }.also { save() }
            is MainUiEvent.UpdateGradientDistance -> _settings.update { it.copy(textStyle = it.textStyle.copy(gradientDistance = event.distance)) }.also { save() }
            is MainUiEvent.ToggleGradientHorizontal -> _settings.update { it.copy(textStyle = it.textStyle.copy(isGradientHorizontal = event.isHorizontal)) }.also { save() }
            is MainUiEvent.UpdateFontType -> _settings.update { it.copy(textStyle = it.textStyle.copy(fontType = event.fontType)) }.also { save() }
            is MainUiEvent.UpdateGoogleFontName -> _settings.update { it.copy(textStyle = it.textStyle.copy(googleFontName = event.fontName)) }.also { save() }

            // Stroke
            is MainUiEvent.ToggleStroke -> _settings.update { it.copy(stroke = it.stroke.copy(isEnabled = event.isEnabled)) }.also { save() }
            is MainUiEvent.UpdateStrokeWidth -> _settings.update { it.copy(stroke = it.stroke.copy(width = event.width.coerceIn(0f, 15f))) }.also { save() }
            is MainUiEvent.UpdateStrokeColor -> _settings.update { it.copy(stroke = it.stroke.copy(colorArgb = event.colorArgb)) }.also { save() }

            // Shadow
            is MainUiEvent.ToggleShadow -> _settings.update { it.copy(shadow = it.shadow.copy(isEnabled = event.isEnabled)) }.also { save() }
            is MainUiEvent.UpdateShadowColor -> _settings.update { it.copy(shadow = it.shadow.copy(colorArgb = event.colorArgb)) }.also { save() }
            is MainUiEvent.UpdateShadowRadius -> _settings.update { it.copy(shadow = it.shadow.copy(radius = event.radius.coerceIn(0f, 25f))) }.also { save() }
            is MainUiEvent.UpdateShadowDistance -> _settings.update { it.copy(shadow = it.shadow.copy(distance = event.distance.coerceIn(0f, 50f))) }.also { save() }
            is MainUiEvent.UpdateShadowRotation -> {
                val normalizedRotation = (event.rotation % 360f + 360f) % 360f
                _settings.update { it.copy(shadow = it.shadow.copy(rotation = normalizedRotation)) }.also { save() }
            }
        }
    }

    private fun updateText(text: String) {
        if (text.length <= 250) {
            _settings.update { it.copy(lastText = text) }
            save()
        }
    }
}