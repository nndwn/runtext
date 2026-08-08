package com.nndwn.runtext.ui.features.display.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.nndwn.runtext.data.model.MorseConfig
import com.nndwn.runtext.domain.morse.MorseElement
import com.nndwn.runtext.domain.morse.MorseEngine
import com.nndwn.runtext.ui.features.display.utils.CameraTorchManager
import com.nndwn.runtext.ui.features.display.utils.MorseAudioPlayer
import com.nndwn.runtext.ui.features.display.utils.MorseVibrator
import com.nndwn.runtext.ui.theme.toComposeColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MorseCodeCore(
    modifier: Modifier = Modifier,
    text: String = "SOS",
    settings: MorseConfig,
) {
    val context = LocalContext.current
    val rawText = remember(text) {
        text.ifEmpty { "PREVIEW" }
    }

    val torchManager = remember { CameraTorchManager(context) }
    val morseVibrator = remember { MorseVibrator(context) }

    var permissionCamera by remember { mutableStateOf(false) }
    var isSignalOn by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionCamera = isGranted
    }

    LaunchedEffect(settings.isTorchEnabled) {
        if (settings.isTorchEnabled) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                permissionCamera = true
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        } else {
            permissionCamera = false
        }
    }
    LaunchedEffect(
        rawText,
        settings.morseWpm,
        settings.isTorchEnabled,
        settings.isSoundEnabled,
        settings.isVibrateEnabled,
        permissionCamera
    ) {
        val elements = if (rawText.equals("SOS", ignoreCase = true)) {
            MorseEngine.SOS_PATTERN
        } else {
            MorseEngine.textToMorseElements(rawText)
        }

        if (elements.isEmpty()) return@LaunchedEffect

        val canUseTorch = settings.isTorchEnabled && permissionCamera
        val unitDurationMs = MorseEngine.getUnitDurationMs(settings.morseWpm)

        try {
            while (isActive) {
                for (element in elements) {
                    val isSignal = MorseEngine.isSignalElement(element)
                    val durationMs = element.durationMultiplier * unitDurationMs

                    if (isSignal) {
                        isSignalOn = true

                        if (canUseTorch) {
                            torchManager.setTorchEnabled(true)
                        }
                        if (settings.isSoundEnabled) {
                            MorseAudioPlayer.playBeep(durationMs)
                        }
                        if (settings.isVibrateEnabled) {
                            morseVibrator.vibrate(durationMs)
                        }

                        delay(durationMs.milliseconds)

                        isSignalOn = false
                        if (canUseTorch) {
                            torchManager.setTorchEnabled(false)
                        }
                        morseVibrator.cancel()
                    } else {
                        isSignalOn = false
                        if (canUseTorch) {
                            torchManager.setTorchEnabled(false)
                        }

                        delay(durationMs.milliseconds)
                    }
                }

                val loopDelay = MorseElement.WordGap.durationMultiplier * unitDurationMs
                delay(loopDelay.milliseconds)
            }
        } finally {
            isSignalOn = false
            if (canUseTorch) torchManager.setTorchEnabled(false)
            morseVibrator.cancel()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            torchManager.setTorchEnabled(false)
            morseVibrator.cancel()
            MorseAudioPlayer.stop()
        }
    }

    val activeColor = remember(settings.bgColorMorse) {
        settings.bgColorMorse.toComposeColor()
    }

    val displayColor = if (settings.isFlashScreen && isSignalOn) {
        activeColor
    } else {
        Color.Black
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(displayColor)
    )
}