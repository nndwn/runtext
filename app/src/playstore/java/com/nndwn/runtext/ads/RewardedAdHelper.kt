package com.nndwn.runtext.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.nndwn.runtext.BuildConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class RewardedAdHelper : AdHelper {
    private var rewardedAd : RewardedAd? = null
    private var isAdLoading = false
    private val adUnit = if (BuildConfig.DEBUG) {
        "ca-app-pub-3940256099942544/5224354917"
    } else {
        BuildConfig.ADS_API_ALT
    }
    override fun isAdReady(): Boolean = rewardedAd != null

    override suspend fun loadAdAwait(context : Context) : Boolean = suspendCancellableCoroutine { continuation ->
        if (rewardedAd != null) {
            continuation.resume(true)
            return@suspendCancellableCoroutine
        }
        if (isAdLoading) {
            continuation.resume(false)
            return@suspendCancellableCoroutine
        }
        
        isAdLoading = true
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context.applicationContext,
            adUnit,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isAdLoading = false
                    if (continuation.isActive) continuation.resume(true)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    isAdLoading = false
                    if (continuation.isActive) continuation.resume(false)
                }
            }
        )
    }

    override fun loadAd(context : Context, onAdLoadedCallback : (() -> Unit)?) {
        if (rewardedAd != null) {
            onAdLoadedCallback?.invoke()
            return
        }
        if (isAdLoading) return
        isAdLoading = true
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context.applicationContext,
            adUnit,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isAdLoading = false
                    onAdLoadedCallback?.invoke()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    isAdLoading = false
                }
            }
        )
    }

    override fun showAd(
        activity : Activity,
        onAdClosed : (isRewardEarned : Boolean) -> Unit
    ) {
        if (rewardedAd != null) {
            checkAdClosedSuddenly(activity, onAdClosed)
        } else {
            loadAd(activity) {
                if (rewardedAd != null) {
                    checkAdClosedSuddenly(activity, onAdClosed)
                } else {
                    onAdClosed(false)
                }
            }
        }
    }

    private fun checkAdClosedSuddenly(
        activity: Activity,
        onAdClosed : (isRewardEarned : Boolean) -> Unit
    ){
        val appContext = activity.applicationContext
        var isRewardEarned = false
        rewardedAd?.fullScreenContentCallback = object  : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                onAdClosed(isRewardEarned)
                loadAd(appContext)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                rewardedAd = null
                onAdClosed(false)
                loadAd(appContext)
            }
        }
        rewardedAd?.show(activity) { _ ->
            isRewardEarned = true
        }
    }
}
