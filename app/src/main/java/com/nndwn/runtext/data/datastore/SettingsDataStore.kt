package com.nndwn.runtext.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nndwn.runtext.BuildConfig
import com.nndwn.runtext.data.model.AppSettings
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Singleton
class SettingsDataStore @Inject constructor(private val dataStore: DataStore<Preferences>) {
  private companion object Keys {
    val APP_SETTINGS = stringPreferencesKey("app_settings")

    val IS_PREMIUM = booleanPreferencesKey("is_premium")
    val ACCUMULATED_USAGE_TIME_KEY = longPreferencesKey("accumulated_usage_time")
    const val SUPPORT_DIALOG_COOLDOWN_MS = 900_000L
  }

  private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
  }

  val isPremium: Flow<Boolean> =
    dataStore.data.catch { emit(emptyPreferences()) }.map { preferences -> preferences[IS_PREMIUM] ?: false }

  val shouldShowSupportDialog: Flow<Boolean> =
    dataStore.data
      .catch { emit(emptyPreferences()) }
      .map { preferences ->
        val isPremium = preferences[IS_PREMIUM] ?: false
        if (isPremium) return@map false

        val accumulatedTime = preferences[ACCUMULATED_USAGE_TIME_KEY] ?: 0L
        accumulatedTime >= SUPPORT_DIALOG_COOLDOWN_MS
      }

  suspend fun incrementUsageTime(durationMs: Long) {
    dataStore.edit { preferences ->
      val current = preferences[ACCUMULATED_USAGE_TIME_KEY] ?: 0L
      preferences[ACCUMULATED_USAGE_TIME_KEY] = current + durationMs
    }
  }

  suspend fun setPremiumStatus(isPremium: Boolean) {
    dataStore.edit { preferences -> preferences[IS_PREMIUM] = isPremium }
  }

  suspend fun recordSupportDialogShown() {
    dataStore.edit { preferences -> preferences[ACCUMULATED_USAGE_TIME_KEY] = 0L }
  }

  suspend fun recordSupportDialogShownIfFirstTime() {
    dataStore.edit { preferences ->
      if (preferences[ACCUMULATED_USAGE_TIME_KEY] == null) {
        preferences[ACCUMULATED_USAGE_TIME_KEY] = 0L
      }
    }
  }

  suspend fun debugPremium() {
    if (BuildConfig.DEBUG) {
      dataStore.edit { preferences -> preferences[IS_PREMIUM] = true }
    }
  }

  suspend fun debugForceShowSupportDialog() {
    if (BuildConfig.DEBUG) {
      dataStore.edit { preferences -> preferences[ACCUMULATED_USAGE_TIME_KEY] = SUPPORT_DIALOG_COOLDOWN_MS }
    }
  }

  suspend fun reset() {
    if (BuildConfig.DEBUG) {
      dataStore.edit { preferences -> preferences.clear() }
    }
  }

  val settingsFlow: Flow<AppSettings> =
    dataStore.data
      .catch { emit(emptyPreferences()) }
      .map { prefs ->
        val jsonString = prefs[APP_SETTINGS]
        if (jsonString != null) {
          runCatching { json.decodeFromString<AppSettings>(jsonString) }.getOrDefault(AppSettings())
        } else {
          AppSettings()
        }
      }

  suspend fun saveSettings(settings: AppSettings) {
    dataStore.edit { prefs ->
      prefs[APP_SETTINGS] = json.encodeToString(settings)
    }
  }
}
