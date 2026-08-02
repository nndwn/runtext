package com.nndwn.runtext.ui.features.main.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.domain.morse.MorseEngine
import com.nndwn.runtext.ui.theme.toComposeColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MorseFlashPreview(settings: AppSettings) {
    val rawText = settings.lastText.ifEmpty { "SOS" }

    val morseElement = remember (rawText){
        val elements = MorseEngine.textToMorseElements(rawText)
        elements.ifEmpty { MorseEngine.SOS_PATTERN }
    }

    val unitMs = remember(settings.morseConfig.morseWpm){
        MorseEngine.getUnitDurationMs(settings.morseConfig.morseWpm)
    }
    var isSignalActive by remember { mutableStateOf(false) }
    val colorOff = Color(0xFF121212)
    val activeMorseColor = settings.morseConfig.bgColorMorse.toComposeColor()

    LaunchedEffect(morseElement, unitMs) {
        while (isActive){
            for (element in morseElement) {
                if (!isActive) break
                val isSignal = MorseEngine.isSignalElement(element)
                val duration = element.durationMultiplier * unitMs
                isSignalActive = isSignal
                delay(duration.milliseconds)
            }
            isSignalActive = false
            delay((unitMs * 7).milliseconds)
        }
    }
    val animatedBgColor by animateColorAsState(
        targetValue = if (isSignalActive) activeMorseColor else colorOff,
        animationSpec = tween(durationMillis = 40),
        label = "MorseFlashAnimation"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBgColor)
    ){

        Box(
            modifier = Modifier
                .align (Alignment.TopEnd)
                .padding(12.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isSignalActive) activeMorseColor else Color.Black.copy(alpha = 0.4f))
                .border(
                    width = 1.dp,
                    color = if (isSignalActive) Color.White else Color.White.copy(alpha = 0.2f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ){
            Icon(
                painter = painterResource(R.drawable.ic_flash),
                contentDescription = null,
                tint = if (isSignalActive){
                    if (activeMorseColor.luminance() > 0.5f) Color.Black else Color.White
                } else {
                    Color.White.copy(alpha = 0.4f)
                },
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

