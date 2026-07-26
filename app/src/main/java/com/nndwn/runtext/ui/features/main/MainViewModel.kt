package com.nndwn.runtext.ui.features.main

import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nndwn.runtext.data.model.AppMode
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.data.model.FontType
import com.nndwn.runtext.data.repository.SettingsRepository
import com.nndwn.runtext.domain.morse.MorseElement
import com.nndwn.runtext.domain.morse.MorseEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
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

    // ── Morse playback state ──
    private val _isMorsePlaying = MutableStateFlow(false)
    val isMorsePlaying: StateFlow<Boolean> = _isMorsePlaying.asStateFlow()

    private val _morseSignalOn = MutableStateFlow(false)
    val morseSignalOn: StateFlow<Boolean> = _morseSignalOn.asStateFlow()

    private var morseJob: Job? = null
    private var saveJob: Job? = null

    // ── Camera / Torch ──
    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null

    init {
        // Load saved settings once at startup
        viewModelScope.launch {
            _settings.value = repository.settingsFlow.first()
        }
        initCamera()
    }

    private fun initCamera() {
        try {
            cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            cameraId = cameraManager?.cameraIdList?.firstOrNull()
        } catch (_: Exception) { /* device has no camera */ }
    }

    // ── Debounced save (300 ms) to prevent excessive DataStore writes ──
    private fun save() {
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(300.milliseconds)
            repository.saveSettings(_settings.value)
        }
    }

    /** Force-save immediately (e.g. before navigating away). */
    fun saveNow() {
        saveJob?.cancel()
        viewModelScope.launch {
            repository.saveSettings(_settings.value)
        }
    }

    // ── Setting updaters ──

    fun updateText(text: String) {
        if (text.length <= 250) {
            _settings.update { it.copy(lastText = text) }
            save()
        }
    }

    fun clearText() {
        _settings.update { it.copy(lastText = "") }
        save()
    }

    fun updateMode(mode: AppMode) {
        _settings.update { it.copy(mode = mode) }
        save()
    }

    fun updateSpeed(speed: Float) {
        _settings.update { it.copy(speed = speed) }
        save()
    }

    fun updateTextColor(argb: Long) {
        _settings.update { it.copy(textColorArgb = argb) }
        save()
    }

    fun updateBgColor(argb: Long) {
        _settings.update { it.copy(bgColorArgb = argb) }
        save()
    }

    fun updateFontType(fontType: FontType) {
        _settings.update { it.copy(fontType = fontType) }
        save()
    }

    fun updateGoogleFontName(name: String) {
        _settings.update { it.copy(googleFontName = name) }
        save()
    }

    fun toggleMirrorMode() {
        _settings.update { it.copy(isMirrorMode = !it.isMirrorMode) }
        save()
    }

    fun updateMorseWpm(wpm: Int) {
        _settings.update { it.copy(morseWpm = wpm) }
        save()
    }

    fun toggleFlashScreen() {
        _settings.update { it.copy(isFlashScreen = !it.isFlashScreen) }
        save()
    }

    fun toggleTorch() {
        _settings.update { it.copy(isTorchEnabled = !it.isTorchEnabled) }
        save()
    }

    // ── Morse playback ──

    /** Start playing morse for the current [lastText]. */
    fun playMorse() {
        val elements = MorseEngine.textToMorseElements(_settings.value.lastText)
        if (elements.isEmpty()) return
        playMorseElements(elements)
    }

    /** Instantly play the SOS pattern (···−−−···). */
    fun playSOS() {
        playMorseElements(MorseEngine.SOS_PATTERN)
    }

    private fun playMorseElements(elements: List<MorseElement>) {
        stopMorse()
        val unitMs = MorseEngine.getUnitDurationMs(_settings.value.morseWpm)

        morseJob = viewModelScope.launch {
            _isMorsePlaying.value = true
            try {
                for (element in elements) {
                    if (!isActive) break
                    val isSignal = MorseEngine.isSignalElement(element)
                    _morseSignalOn.value = isSignal

                    if (isSignal && _settings.value.isTorchEnabled) {
                        setTorch(true)
                    }

                    delay((unitMs * element.durationMultiplier).milliseconds)

                    if (isSignal) {
                        if (_settings.value.isTorchEnabled) setTorch(false)
                        _morseSignalOn.value = false
                    }
                }
            } finally {
                _morseSignalOn.value = false
                _isMorsePlaying.value = false
                setTorch(false)
            }
        }
    }

    fun stopMorse() {
        morseJob?.cancel()
        morseJob = null
        _isMorsePlaying.value = false
        _morseSignalOn.value = false
        setTorch(false)
    }

    private fun setTorch(on: Boolean) {
        try {
            cameraId?.let { id -> cameraManager?.setTorchMode(id, on) }
        } catch (_: Exception) { /* ignore if torch unavailable */ }
    }

    override fun onCleared() {
        stopMorse()
    }
}