package com.nndwn.runtext.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nndwn.runtext.R
import com.nndwn.runtext.ads.AdHelper
import com.nndwn.runtext.ads.BillingHelper
import com.nndwn.runtext.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val uiEffectController: UiEffectController,
    val adHelper: AdHelper,
    private val billingHelper: BillingHelper
): ViewModel() {
    val uiEffect = uiEffectController.uiEffect
    
    val isPremium: StateFlow<Boolean> = repository.isPremium
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val shouldShowAd: StateFlow<Boolean> = repository.shouldShowAd
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val removeAdsPrice: StateFlow<String?> = billingHelper.removeAdsPrice
    
    private val _isLoadingAd = MutableStateFlow(false)
    val isLoadingAd = _isLoadingAd.asStateFlow()

    init {
        viewModelScope.launch {
            repository.recordAdShownIfFirstTime()
        }

        viewModelScope.launch {
            billingHelper.purchaseSuccessEvent.collectLatest {
                uiEffectController.sendEffect(UiEffect.ShowToast(R.string.msg_premium_activated))
            }
        }
    }

    fun recordAdShown() {
        viewModelScope.launch {
            repository.recordAdShown()
        }
    }

    fun onRemoveAdsClicked(activity: Activity) {
        billingHelper.launchBillingFlow(activity)
    }

    fun performAdFlow(activity: Activity, onFinished: () -> Unit) {
        viewModelScope.launch {
            _isLoadingAd.value = true
            val isLoaded = withTimeoutOrNull(7.seconds) {
                adHelper.loadAdAwait(activity.applicationContext)
            } ?: false
            _isLoadingAd.value = false

            if (isLoaded) {
                adHelper.showAd(activity) {
                    recordAdShown()
                    onFinished()
                }
            } else {
                recordAdShown()
                onFinished()
            }
        }
    }
}
