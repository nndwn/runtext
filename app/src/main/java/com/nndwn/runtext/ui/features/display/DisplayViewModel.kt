package com.nndwn.runtext.ui.features.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.data.model.FontData
import com.nndwn.runtext.data.repository.FontRepository
import com.nndwn.runtext.data.repository.SettingsRepository
import com.nndwn.runtext.ui.UiEffect
import com.nndwn.runtext.ui.UiEffectController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class DisplayViewModel
@Inject
constructor(
  private val repository: SettingsRepository,
  private val fontRepository: FontRepository,
  private val uiEffectController: UiEffectController,
) : ViewModel() {

  val settings: StateFlow<AppSettings?> =
    repository.settingsFlow.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = AppSettings(),
    )

  val fonts: StateFlow<List<FontData>> = fontRepository.fonts

  fun navigateBack() {
    uiEffectController.sendEffect(UiEffect.NavigateBack)
  }
}
