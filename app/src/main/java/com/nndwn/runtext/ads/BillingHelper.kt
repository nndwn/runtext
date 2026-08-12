package com.nndwn.runtext.ads

import android.app.Activity
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BillingHelper {
    val removeAdsPrice: StateFlow<String?>
    val purchaseSuccessEvent: SharedFlow<Unit>
    fun startConnection(setPurchased: (Boolean) -> Unit, billingDisconnected: () -> Unit)
    fun launchBillingFlow(activity: Activity)
}
