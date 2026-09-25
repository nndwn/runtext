package com.nndwn.runtext

import android.app.Application
import com.nndwn.runtext.data.repository.SettingsRepository
import com.nndwn.runtext.helper.BillingHelper
import com.nndwn.runtext.utils.DisplayRatioManager
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

    DisplayRatioManager.init(this)

    billingHelper.startConnection(
      setPurchased = { hasTipped -> applicationScope.launch { settingsRepository.setTippedStatus(hasTipped) } },
      billingDisconnected = {
        // Handle disconnection
      },
    )
  }
}
