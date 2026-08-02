package com.nndwn.runtext.ui.features.display.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun SetScreenBrightnessEffect(brightnessValue: Float) {
    val context = LocalContext.current

    DisposableEffect(brightnessValue) {
        val activity = context as? Activity
        val window = activity?.window

        if (window != null) {
            val layoutParams = window.attributes
            val originalBrightness = layoutParams.screenBrightness
            layoutParams.screenBrightness = brightnessValue
            window.attributes = layoutParams

            onDispose {
                layoutParams.screenBrightness = originalBrightness
                window.attributes = layoutParams
            }
        } else {
            onDispose { }
        }
    }
}

//SetScreenBrightnessEffect(brightnessValue = 1.0f)