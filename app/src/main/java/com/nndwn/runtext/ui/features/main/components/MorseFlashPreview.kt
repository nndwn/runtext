package com.nndwn.runtext.ui.features.main.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.ui.theme.toComposeColor

@Composable
fun MorseFlashPreview(settings: AppSettings) {
    val infiniteTransition = rememberInfiniteTransition(label = "morse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flash"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                settings.bgColorArgb
                    .toComposeColor()
                    .copy(alpha = if (settings.isFlashScreen) alpha else 1f)
            )
    )
}
