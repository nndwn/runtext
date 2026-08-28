package com.nndwn.runtext.data.repository

import com.nndwn.runtext.data.datastore.SettingsDataStore
import com.nndwn.runtext.data.model.AppSettings
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

/**
 * Repository abstraction over [SettingsDataStore]. Keeps the ViewModel decoupled from the persistence implementation.
 */
@Singleton
class SettingsRepository @Inject constructor(private val dataStore: SettingsDataStore) {
  /** Emits the latest [AppSettings]. Always emits at least the default values. */
  val settingsFlow: Flow<AppSettings> = dataStore.settingsFlow

  val isPremium: Flow<Boolean> = dataStore.isPremium

  val shouldShowSupportDialog: Flow<Boolean> = dataStore.shouldShowSupportDialog

  /** Persist the entire [AppSettings] object to DataStore. */
  suspend fun saveSettings(settings: AppSettings) {
    dataStore.saveSettings(settings)
  }

  suspend fun setPremiumStatus(isPremium: Boolean) {
    dataStore.setPremiumStatus(isPremium)
  }

  suspend fun resetCooldownSupportDialog() {
    dataStore.recordSupportDialogShown()
  }

  suspend fun initialCooldownSupportDialog() {
    dataStore.recordSupportDialogShownIfFirstTime()
  }
}
