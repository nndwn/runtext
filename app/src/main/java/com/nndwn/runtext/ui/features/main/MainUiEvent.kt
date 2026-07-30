package com.nndwn.runtext.ui.features.main

import com.nndwn.runtext.data.model.AppMode
import com.nndwn.runtext.data.model.FontType
import com.nndwn.runtext.data.model.TextColorType

sealed interface MainUiEvent {
    // ── General / Text Input ──
    data class UpdateText(val text: String) : MainUiEvent
    object ClearText : MainUiEvent
    data class UpdateMode(val mode: AppMode) : MainUiEvent
    data class UpdateSpeed(val speed: Float) : MainUiEvent
    data class UpdateBgColor(val colorArgb: Long) : MainUiEvent
    object ToggleMirrorMode : MainUiEvent

    // ── Text Style Events ──
    data class UpdateTextColor(val colorArgb: Long) : MainUiEvent
    data class UpdateTextColorType(val type: TextColorType) : MainUiEvent
    data class UpdateGradientColors(val colors: List<Long>) : MainUiEvent
    data class UpdateGradientDistance(val distance: Float) : MainUiEvent
    data class ToggleGradientHorizontal(val isHorizontal: Boolean) : MainUiEvent
    data class UpdateFontType(val fontType: FontType) : MainUiEvent
    data class UpdateGoogleFontName(val fontName: String) : MainUiEvent
    data class UpdateLetterSpacing(val spacingSp: Float) : MainUiEvent
    data class UpdateWordSpacing(val spacingSp: Float) : MainUiEvent

    // ── Stroke Events ──
    data class ToggleStroke(val isEnabled: Boolean) : MainUiEvent
    data class UpdateStrokeWidth(val width: Float) : MainUiEvent
    data class UpdateStrokeColor(val colorArgb: Long) : MainUiEvent

    // ── Shadow Events ──
    data class ToggleShadow(val isEnabled: Boolean) : MainUiEvent
    data class UpdateShadowColor(val colorArgb: Long) : MainUiEvent
    data class UpdateShadowRadius(val radius: Float) : MainUiEvent
    data class UpdateShadowDistance(val distance: Float) : MainUiEvent
    data class UpdateShadowRotation(val rotation: Float) : MainUiEvent
}