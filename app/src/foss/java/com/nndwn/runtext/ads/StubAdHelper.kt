package com.nndwn.runtext.ads

import android.app.Activity
import android.content.Context

class StubAdHelper : AdHelper {
    override fun isAdReady(): Boolean = false
    override fun loadAd(context: Context, onAdLoadedCallback: (() -> Unit)?) {
        onAdLoadedCallback?.invoke()
    }
    override suspend fun loadAdAwait(context: Context): Boolean = false
    override fun showAd(activity: Activity, onAdClosed: (isRewardEarned: Boolean) -> Unit) {
        onAdClosed(false)
    }
}
