package com.nndwn.runtext.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nndwn.runtext.R
import com.nndwn.runtext.data.repository.SettingsRepository
import com.nndwn.runtext.helper.BillingHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AppViewModel
@Inject
constructor(
  private val repository: SettingsRepository,
  private val uiEffectController: UiEffectController,
  private val billingHelper: BillingHelper,
) : ViewModel() {
  val uiEffect = uiEffectController.uiEffect

  val hasTipped: StateFlow<Boolean> =
    repository.hasTipped.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  val shouldShowSupportDialog: StateFlow<Boolean> =
    repository.shouldShowSupportDialog.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  val shouldShowReviewPrompt: StateFlow<Boolean> =
    repository.shouldShowReviewPrompt.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  val appPrice: StateFlow<String?> = billingHelper.appPrice

  init {
    viewModelScope.launch { repository.initialCooldownSupportDialog() }

    viewModelScope.launch {
      billingHelper.purchaseSuccessEvent.collectLatest {
        uiEffectController.sendEffect(UiEffect.ShowToast(R.string.msg_premium_activated))
      }
    }
  }

  fun resetCooldownSupportDialog() {
    viewModelScope.launch { repository.resetCooldownSupportDialog() }
  }

  fun resetCooldownReviewPrompt() {
    viewModelScope.launch { repository.resetCooldownReviewPrompt() }
  }

  fun onBuyApp(activity: Activity) {
    billingHelper.launchBillingFlow(activity)
  }
}
