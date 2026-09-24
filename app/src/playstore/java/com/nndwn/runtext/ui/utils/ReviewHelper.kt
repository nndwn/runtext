package com.nndwn.runtext.ui.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import com.nndwn.runtext.BuildConfig

fun launchInAppReview(context: Context) {
  val activity = context as? Activity
  if (activity == null) {
    gotoPlayStore(context)
    return
  }
  val manager = if (BuildConfig.DEBUG) {
    FakeReviewManager(activity)
  } else {
    ReviewManagerFactory.create(activity)
  }
  val request = manager.requestReviewFlow()
  request.addOnCompleteListener { task ->
    if (task.isSuccessful) {
      val reviewInfo = task.result
      val flow = manager.launchReviewFlow(activity, reviewInfo)
      flow.addOnCompleteListener {
        // In-app review flow completed
      }
    } else {
      Log.w("ReviewHelper", "Review flow request failed, fallback to Play Store link", task.exception)
      gotoPlayStore(context)
    }
  }
}
