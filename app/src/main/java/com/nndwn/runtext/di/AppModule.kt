package com.nndwn.runtext.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.nndwn.runtext.data.datastore.SettingsDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SettingsDataStoreQualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeviceStatsDataStoreQualifier

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Provides fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

  @Provides
  @Singleton
  @SettingsDataStoreQualifier
  fun provideSettingsDataStoreFile(@ApplicationContext context: Context): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create(produceFile = { context.preferencesDataStoreFile("app_settings") })
  }

  @Provides
  @Singleton
  @DeviceStatsDataStoreQualifier
  fun provideDeviceStatsDataStoreFile(@ApplicationContext context: Context): DataStore<Preferences> {
    return PreferenceDataStoreFactory.create(produceFile = { context.preferencesDataStoreFile("device_stats") })
  }

  @Provides
  @Singleton
  fun provideSettingsDataStore(
    @SettingsDataStoreQualifier settingsDataStore: DataStore<Preferences>,
    @DeviceStatsDataStoreQualifier deviceStatsDataStore: DataStore<Preferences>,
  ): SettingsDataStore {
    return SettingsDataStore(settingsDataStore, deviceStatsDataStore)
  }
}
