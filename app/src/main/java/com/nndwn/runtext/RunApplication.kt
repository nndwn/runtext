package com.nndwn.runtext

import android.app.Application
import com.nndwn.runtext.data.repository.SettingsRepository
import com.nndwn.runtext.helper.BillingHelper
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class RunApplication : Application() {
  @Inject lateinit var billingHelper: BillingHelper

  @Inject lateinit var settingsRepository: SettingsRepository

  private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

  override fun onCreate() {
    super.onCreate()

    billingHelper.startConnection(
      setPurchased = { isPremium -> applicationScope.launch { settingsRepository.setPremiumStatus(isPremium) } },
      billingDisconnected = {
        // Handle disconnection
      },
    )
  }
}
