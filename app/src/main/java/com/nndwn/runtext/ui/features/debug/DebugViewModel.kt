package com.nndwn.runtext.ui.features.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nndwn.runtext.data.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class DebugViewModel @Inject constructor(private val dataStore: SettingsDataStore) : ViewModel() {

  fun setPremium() {
    viewModelScope.launch { dataStore.debugPremium() }
  }

  fun forceShowAd() {
    viewModelScope.launch { dataStore.debugForceShowSupportDialog() }
  }

  fun resetDataStore() {
    viewModelScope.launch { dataStore.reset() }
  }
}
