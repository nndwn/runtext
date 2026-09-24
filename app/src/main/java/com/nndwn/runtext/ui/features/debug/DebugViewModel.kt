package com.nndwn.runtext.ui.features.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nndwn.runtext.data.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DebugViewModel @Inject constructor(private val dataStore: SettingsDataStore) : ViewModel() {

  val hasTipped: StateFlow<Boolean> =
    dataStore.hasTipped.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  val accumulatedSupportTime: StateFlow<Long> =
    dataStore.accumulatedSupportTime.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

  val accumulatedReviewTime: StateFlow<Long> =
    dataStore.accumulatedReviewTime.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

  val hasRequestedReview: StateFlow<Boolean> =
    dataStore.hasRequestedReview.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  val shouldShowSupportDialog: StateFlow<Boolean> =
    dataStore.shouldShowSupportDialog.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  val shouldShowReviewPrompt: StateFlow<Boolean> =
    dataStore.shouldShowReviewPrompt.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

  fun toggleTipped(current: Boolean) {
    viewModelScope.launch { dataStore.setTippedStatus(!current) }
  }

  fun forceShowSupportDialog() {
    viewModelScope.launch { dataStore.debugForceShowSupportDialog() }
  }

  fun forceShowReviewPrompt() {
    viewModelScope.launch { dataStore.debugForceShowReviewPrompt() }
  }

  fun resetReviewStatus() {
    viewModelScope.launch { dataStore.debugResetReviewStatus() }
  }

  fun resetDataStore() {
    viewModelScope.launch { dataStore.reset() }
  }
}
