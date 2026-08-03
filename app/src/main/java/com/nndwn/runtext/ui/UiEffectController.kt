package com.nndwn.runtext.ui

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UiEffectController @Inject constructor() {
    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 64)
    val uiEffect = _uiEffect.asSharedFlow()

    fun sendEffect(effect: UiEffect) {
        _uiEffect.tryEmit(effect)
    }
}
