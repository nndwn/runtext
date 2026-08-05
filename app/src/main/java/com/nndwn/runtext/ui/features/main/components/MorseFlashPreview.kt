package com.nndwn.runtext.ui.features.main.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.data.model.MorseConfig
import com.nndwn.runtext.domain.morse.MorseEngine
import com.nndwn.runtext.ui.theme.RuntextTheme
import com.nndwn.runtext.ui.theme.dimens
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
    val colorOff = MaterialTheme.colorScheme.background
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
            .padding(MaterialTheme.dimens.medium)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            IconRound(
                show = settings.morseConfig.isTorchEnabled,
                color = activeMorseColor,
                icon = R.drawable.ic_flash,
                animateSlide = false,
            )
            IconRound(
                show = settings.morseConfig.isSoundEnabled,
                color = activeMorseColor,
                icon = R.drawable.ic_music,
                animateSlide = true,
            )
            IconRound(
                show = settings.morseConfig.isVibrateEnabled,
                color = activeMorseColor,
                icon = R.drawable.ic_wave,
                animateSlide = true,
            )
        }
    }
}
@Composable
private fun IconRound(
    show: Boolean,
    color: Color,
    @DrawableRes icon: Int,
    animateSlide: Boolean = true,

) {
    AnimatedVisibility(
        visible = show,
        enter = if (animateSlide) {
            fadeIn() + expandHorizontally(expandFrom = Alignment.End)
        } else {
            fadeIn(animationSpec = tween(150))
        },
        exit = if (animateSlide) {
            fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.End)
        } else {
            fadeOut(animationSpec = tween(150))
        },
        modifier = Modifier
    ) {

        Box(
            modifier = Modifier.padding(start = if (animateSlide) MaterialTheme.dimens.small else 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(MaterialTheme.dimens.iconLarge)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = MaterialTheme.dimens.borderMedium,
                        color = MaterialTheme.colorScheme.background,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(MaterialTheme.dimens.iconSmall)
                )
            }
        }
    }
}
@Preview
@Composable
private fun Preview() {
    RuntextTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            MorseFlashPreview(
                settings = AppSettings().copy(
                    morseConfig = MorseConfig(
                        isFlashScreen = true,
                        isSoundEnabled = true,
                        isVibrateEnabled = true,
                        isTorchEnabled = true
                    )
                )
            )
        }

    }
}