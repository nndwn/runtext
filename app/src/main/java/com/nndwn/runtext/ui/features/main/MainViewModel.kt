package com.nndwn.runtext.ui.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.AppMode
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.data.model.MorseConfig
import com.nndwn.runtext.data.model.ShadowConfig
import com.nndwn.runtext.data.model.StrokeConfig
import com.nndwn.runtext.data.model.TextConfig
import com.nndwn.runtext.data.model.TextStyleConfig
import com.nndwn.runtext.data.repository.SettingsRepository
import com.nndwn.runtext.ui.UiEffect
import com.nndwn.runtext.ui.UiEffectController
import com.nndwn.runtext.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
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


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val uiEffectController: UiEffectController
) : ViewModel() {

    val limitText = 100

    private val _settings = MutableStateFlow<AppSettings?>(null)

    val uiState: StateFlow<MainUiState> = _settings.map { settings ->
        if (settings == null) MainUiState.Loading
        else MainUiState.Success(settings)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState.Loading
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

    fun onEvent(event: MainUiEvent) {
        when (event) {
            // Morse Config
            is MainUiEvent.UpdateMorseWpm,
            is MainUiEvent.UpdateBgColorMorse,
            is MainUiEvent.UpdateFlashScreen,
            is MainUiEvent.UpdateTorchEnabled,
            is MainUiEvent.UpdateSoundEnabled,
            is MainUiEvent.UpdateVibrateEnabled -> handleMorseEvent(event)

            // Text Style
            is MainUiEvent.UpdateTextColor,
            is MainUiEvent.UpdateTextColorType,
            is MainUiEvent.UpdateGradientColors,
            is MainUiEvent.UpdateGradientDistance,
            is MainUiEvent.ToggleGradientHorizontal,
            is MainUiEvent.UpdateFontType,
            is MainUiEvent.UpdateGoogleFontName,
            is MainUiEvent.UpdateLetterSpacing,
            is MainUiEvent.ToggleStroke,
            is MainUiEvent.UpdateStrokeWidth,
            is MainUiEvent.UpdateStrokeColor,
            is MainUiEvent.ToggleShadow,
            is MainUiEvent.UpdateShadowColor,
            is MainUiEvent.UpdateShadowRadius,
            is MainUiEvent.ApplyPreset,
            is MainUiEvent.UpdateSpeed,
            is MainUiEvent.UpdateBgColor,
            is MainUiEvent.UpdateMirrorMode,
            is MainUiEvent.UpdateShadowRotation,
            is MainUiEvent.UpdateWordSpacing -> handleRunningText(event)

            // Navigation & General
            is MainUiEvent.NavigateToDisplay -> handleNavigateToDisplay()
            is MainUiEvent.NavigateBack -> uiEffectController.sendEffect(UiEffect.NavigateBack)
            is MainUiEvent.Toast -> uiEffectController.sendEffect(UiEffect.ShowToast(event.message))
            is MainUiEvent.UpdateText -> updateText(event.text)
            is MainUiEvent.ClearText -> updateText("")
            is MainUiEvent.UpdateMode -> updateSettings { it.copy(mode = event.mode) }



        }
    }

    private fun handleRunningText(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.ApplyPreset -> updateTextConfig { event.settings }
            is MainUiEvent.UpdateTextColor -> updateTextStyle { copy(colorArgb = event.colorArgb) }
            is MainUiEvent.UpdateTextColorType -> updateTextStyle { copy(colorType = event.type) }
            is MainUiEvent.UpdateGradientColors -> updateTextStyle { copy(gradientColorsArgb = event.colors) }
            is MainUiEvent.UpdateGradientDistance -> updateTextStyle { copy(gradientDistance = event.distance) }
            is MainUiEvent.ToggleGradientHorizontal -> updateTextStyle { copy(isGradientHorizontal = event.isHorizontal) }
            is MainUiEvent.UpdateFontType -> updateTextStyle { copy(fontType = event.fontType) }
            is MainUiEvent.UpdateGoogleFontName -> updateTextStyle { copy(googleFontName = event.fontName) }
            is MainUiEvent.UpdateLetterSpacing -> updateTextStyle { copy(letterSpacingSp = event.spacingSp.coerceIn(-2f, 20f)) }
            is MainUiEvent.UpdateWordSpacing -> updateTextStyle { copy(wordSpacingSp = event.spacingSp.coerceIn(0f, 30f)) }
            is MainUiEvent.UpdateSpeed -> updateTextConfig { copy(speed = event.speed) }
            is MainUiEvent.UpdateBgColor -> updateTextConfig { copy(bgColorArgb = event.colorArgb) }
            is MainUiEvent.UpdateMirrorMode -> updateTextConfig { copy(isMirrorMode = event.mirror) }
            is MainUiEvent.ToggleStroke -> updateStroke { copy(isEnabled = event.isEnabled) }
            is MainUiEvent.UpdateStrokeWidth -> updateStroke { copy(width = event.width.coerceIn(1f, 10f)) }
            is MainUiEvent.UpdateStrokeColor -> updateStroke { copy(colorArgb = event.colorArgb) }
            is MainUiEvent.ToggleShadow -> updateShadow { copy(isEnabled = event.isEnabled) }
            is MainUiEvent.UpdateShadowColor -> updateShadow { copy(colorArgb = event.colorArgb) }
            is MainUiEvent.UpdateShadowRadius -> updateShadow { copy(radius = event.radius.coerceIn(0f, 25f)) }
            is MainUiEvent.UpdateShadowRotation -> updateShadow {
                val normalizedRotation = (event.rotation % 360f + 360f) % 360f
                copy(rotation = normalizedRotation)
            }
            else -> {}
        }
    }


    private fun handleMorseEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.UpdateMorseWpm -> updateMorse { copy(morseWpm = event.wpm.coerceIn(5, 40)) }
            is MainUiEvent.UpdateBgColorMorse -> updateMorse { copy(bgColorMorse = event.colorArgb) }
            is MainUiEvent.UpdateFlashScreen -> handleMorseOutputToggle(isFlashScreen = event.isFlashScreen)
            is MainUiEvent.UpdateTorchEnabled -> handleMorseOutputToggle(isTorchEnabled = event.isTorchEnabled)
            is MainUiEvent.UpdateSoundEnabled -> updateMorse { copy(isSoundEnabled = event.isSoundEnabled) }
            is MainUiEvent.UpdateVibrateEnabled -> updateMorse { copy(isVibrateEnabled = event.isVibrateEnabled) }
            else -> {}
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

    private fun updateTextConfig(transform: TextConfig.() -> TextConfig) {
        updateSettings { it.copy(textConfig = it.textConfig.transform()) }
    }

    private fun updateTextStyle(transform: TextStyleConfig.() -> TextStyleConfig) {
        updateTextConfig { copy(textStyle = textStyle.transform()) }
    }

    private fun updateStroke(transform: StrokeConfig.() -> StrokeConfig) {
        updateTextConfig { copy(stroke = stroke.transform()) }
    }

    private fun updateShadow(transform: ShadowConfig.() -> ShadowConfig) {
        updateTextConfig { copy(shadow = shadow.transform()) }
    }

    private fun updateMorse(transform: MorseConfig.() -> MorseConfig) {
        updateSettings { it.copy(morseConfig = it.morseConfig.transform()) }
    }

    private fun updateText(text: String) {
        if (text.length <= limitText) {
            updateSettings { it.copy(lastText = text) }
        }
    }

    private fun handleNavigateToDisplay() {
        val currentSettings = _settings.value ?: return

        if (currentSettings.lastText.length >= limitText) return

        if (currentSettings.mode == AppMode.MORSE_CODE) {
            val morse = currentSettings.morseConfig
            if (!morse.isFlashScreen && !morse.isTorchEnabled) {
                uiEffectController.sendEffect(UiEffect.ShowToast(R.string.notice_morse_output_required))
                return
            }
        }
        uiEffectController.sendEffect(UiEffect.RequestNavigationWithAdCheck(Routes.DISPLAY))
    }

    private fun handleMorseOutputToggle(
        isFlashScreen: Boolean? = null, isTorchEnabled: Boolean? = null
    ) {
        val currentMorse = _settings.value?.morseConfig ?: return

        val newFlash = isFlashScreen ?: currentMorse.isFlashScreen
        val newTorch = isTorchEnabled ?: currentMorse.isTorchEnabled

        if (!newFlash && !newTorch) {
            uiEffectController.sendEffect(UiEffect.ShowToast(R.string.notice_morse_output_required))
            return
        }

        updateMorse { copy(isFlashScreen = newFlash, isTorchEnabled = newTorch) }
    }
}