package com.nndwn.runtext.ui.features.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nndwn.runtext.data.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    private val dataStore: SettingsDataStore
) : ViewModel() {

    fun setPremium() {
        viewModelScope.launch {
            dataStore.debugPremium()
        }
    }

    fun forceShowAd() {
        viewModelScope.launch {
            dataStore.debugForceShowAd()
        }
    }

    fun resetDataStore() {
        viewModelScope.launch {
            dataStore.reset()
        }
    }
}
