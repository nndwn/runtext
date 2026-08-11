package com.nndwn.runtext.ads

import android.app.Activity
import android.content.Context

interface AdHelper {
    fun isAdReady(): Boolean
    fun loadAd(context: Context, onAdLoadedCallback: (() -> Unit)? = null)
    suspend fun loadAdAwait(context: Context): Boolean
    fun showAd(activity: Activity, onAdClosed: (isRewardEarned: Boolean) -> Unit)
}
