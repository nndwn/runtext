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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

  private val startTime = System.currentTimeMillis()

  fun navigateBack() {
    uiEffectController.sendEffect(UiEffect.NavigateBack)
  }

  @OptIn(DelicateCoroutinesApi::class)
  override fun onCleared() {
    val duration = System.currentTimeMillis() - startTime
    if (duration > 0) {
      // Menggunakan GlobalScope agar proses IO ke DataStore tetap diselesaikan 
      // dan tidak ikut ter-cancel ketika viewModelScope dihancurkan (destroyed)
      GlobalScope.launch {
        repository.incrementUsageTime(duration)
      }
    }
  }
}
