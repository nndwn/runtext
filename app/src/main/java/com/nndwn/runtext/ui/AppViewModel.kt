package com.nndwn.runtext.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nndwn.runtext.ads.AdHelper
import com.nndwn.runtext.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val uiEffectController: UiEffectController,
    val adHelper: AdHelper
): ViewModel() {
    val uiEffect = uiEffectController.uiEffect
    
    val isPremium: StateFlow<Boolean> = repository.isPremium
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val shouldShowAd: StateFlow<Boolean> = repository.shouldShowAd
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _isLoadingAd = MutableStateFlow(false)
    val isLoadingAd = _isLoadingAd.asStateFlow()

    init {
        viewModelScope.launch {
            repository.recordAdShownIfFirstTime()
        }
    }

    fun recordAdShown() {
        viewModelScope.launch {
            repository.recordAdShown()
        }
    }

    fun onRemoveAdsClicked() {
        // Placeholder for billing
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
