package com.nndwn.runtext.ads

import android.app.Activity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class StubBillingHelper : BillingHelper {
    override val removeAdsPrice: StateFlow<String?> = MutableStateFlow<String?>(null).asStateFlow()
    override val purchaseSuccessEvent: SharedFlow<Unit> = MutableSharedFlow<Unit>().asSharedFlow()

    override fun startConnection(setPurchased: (Boolean) -> Unit, billingDisconnected: () -> Unit) {
        setPurchased(false)
    }

    override fun launchBillingFlow(activity: Activity) {
        // No-op in FOSS
    }
}
