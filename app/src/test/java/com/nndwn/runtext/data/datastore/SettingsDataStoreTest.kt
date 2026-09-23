package com.nndwn.runtext.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.data.model.TextConfig
import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsDataStoreTest {

  @get:Rule val tmpFolder = TemporaryFolder()

  private val testDispatcher = UnconfinedTestDispatcher()
  private val testScope = TestScope(testDispatcher)

  private lateinit var testDataStore: DataStore<Preferences>
  private lateinit var settingsDataStore: SettingsDataStore

  @Before
  fun setup() {
    // Create a temporary DataStore in the temp folder
    testDataStore =
      PreferenceDataStoreFactory.create(
        scope = testScope,
        produceFile = { File(tmpFolder.root, "test_settings.preferences_pb") },
      )
    settingsDataStore = SettingsDataStore(testDataStore)
  }

  @Test
  fun `settingsFlow returns default AppSettings when empty`() =
    runTest(testDispatcher) {
      val settings = settingsDataStore.settingsFlow.first()
      assertEquals(AppSettings(), settings)
    }

  @Test
  fun `saveSettings persists data correctly using JSON`() =
    runTest(testDispatcher) {
      val customSettings =
        AppSettings(
          lastText = "Hello Test",
          textConfig = TextConfig(speed = 200f),
        )

      settingsDataStore.saveSettings(customSettings)

      val savedSettings = settingsDataStore.settingsFlow.first()
      assertEquals("Hello Test", savedSettings.lastText)
      assertEquals(200f, savedSettings.textConfig.speed)
    }

  @Test
  fun `settingsFlow handles corrupted JSON by returning default`() =
    runTest(testDispatcher) {
      val corruptedJson = "{ invalid_json }"
      testDataStore.edit { prefs ->
        prefs[stringPreferencesKey("app_settings")] = corruptedJson
      }

      val settings = settingsDataStore.settingsFlow.first()
      // Should return default AppSettings if JSON decoding fails
      assertEquals(AppSettings(), settings)
    }

  @Test
  fun `setPremiumStatus persists premium flag independently`() =
    runTest(testDispatcher) {
      settingsDataStore.setPremiumStatus(true)

      val isPremium = settingsDataStore.isPremium.first()
      assertEquals(true, isPremium)
    }

  @Test
  fun `incrementUsageTime accumulates time and shouldShowSupportDialog becomes true when threshold met`() =
    runTest(testDispatcher) {
      // Pastikan awalnya false karena akumulasi = 0
      var shouldShow = settingsDataStore.shouldShowSupportDialog.first()
      assertEquals(false, shouldShow)

      // Tambah waktu di bawah threshold (misal 5 menit = 300_000ms)
      settingsDataStore.incrementUsageTime(300_000L)
      shouldShow = settingsDataStore.shouldShowSupportDialog.first()
      assertEquals(false, shouldShow)

      // Tambah waktu lagi hingga melewati threshold (900_000ms)
      settingsDataStore.incrementUsageTime(600_000L)
      shouldShow = settingsDataStore.shouldShowSupportDialog.first()
      assertEquals(true, shouldShow)

      // Setelah dialog direset (muncul), waktu akumulasi kembali ke 0 dan flag harus kembali false
      settingsDataStore.recordSupportDialogShown()
      shouldShow = settingsDataStore.shouldShowSupportDialog.first()
      assertEquals(false, shouldShow)
    }
}
