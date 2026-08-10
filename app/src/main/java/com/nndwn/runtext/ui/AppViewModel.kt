package com.nndwn.runtext.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nndwn.runtext.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val uiEffectController: UiEffectController
): ViewModel() {
    val uiEffect = uiEffectController.uiEffect
    
    val isPremium: StateFlow<Boolean> = repository.isPremium
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val shouldShowAd: StateFlow<Boolean> = repository.shouldShowAd
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        // Inisialisasi timer untuk user baru agar tidak langsung muncul iklan
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
}
