package com.nndwn.runtext.ui

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Singleton
class UiEffectController @Inject constructor() {
  private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 64)
  val uiEffect = _uiEffect.asSharedFlow()

  fun sendEffect(effect: UiEffect) {
    _uiEffect.tryEmit(effect)
  }
}
