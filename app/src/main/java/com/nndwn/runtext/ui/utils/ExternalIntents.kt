package com.nndwn.runtext.ui.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.net.toUri
import com.nndwn.runtext.BuildConfig
import com.nndwn.runtext.R

fun gotoPlayStore(context: Context) {
    val packageName = context.packageName
    val marketIntent = Intent(
        Intent.ACTION_VIEW, "market://details?id=$packageName".toUri()
    )
    marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        context.startActivity(marketIntent)
    } catch (_: ActivityNotFoundException) {
        val webIntent = Intent(
            Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$packageName".toUri()
        )
        context.startActivity(webIntent)
    }
}

fun gotoMail(context: Context) {
    val deviceModel = Build.MODEL
    val androidVersion = Build.VERSION.RELEASE
    val appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.EMAIL))
        putExtra(Intent.EXTRA_SUBJECT, "Report Issue - ${context.getString(R.string.app_name)}")
        putExtra(
            Intent.EXTRA_TEXT,
            "\n\n\n---\nDevice: $deviceModel\nAndroid: $androidVersion\nApp Version: $appVersion"
        )
    }
    context.startActivity(Intent.createChooser(intent, "Send Email"))
}
