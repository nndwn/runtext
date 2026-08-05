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
import com.nndwn.runtext.data.model.AppSettings
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
    settings: AppSettings,
    modifier: Modifier = Modifier,
    defaultString: String = "SOS",
) {
    val context = LocalContext.current
    val rawText = settings.lastText.ifEmpty { defaultString }
    val morseConfig = settings.morseConfig
    val torchManager = remember { CameraTorchManager(context) }
    val morseVibrator = remember { MorseVibrator(context) }

    var permissionCamera by remember { mutableStateOf(false) }
    var isSignalOn by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionCamera = isGranted
    }

    LaunchedEffect(morseConfig.isTorchEnabled) {
        if (morseConfig.isTorchEnabled) {
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
        morseConfig.morseWpm,
        morseConfig.isTorchEnabled,
        morseConfig.isSoundEnabled,
        morseConfig.isVibrateEnabled,
        permissionCamera
    ) {
        val elements = if (rawText.equals("SOS", ignoreCase = true)) {
            MorseEngine.SOS_PATTERN
        } else {
            MorseEngine.textToMorseElements(rawText)
        }

        if (elements.isEmpty()) return@LaunchedEffect

        val canUseTorch = morseConfig.isTorchEnabled && permissionCamera
        val unitDurationMs = MorseEngine.getUnitDurationMs(morseConfig.morseWpm)

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
                        if (morseConfig.isSoundEnabled) {
                            MorseAudioPlayer.playBeep(durationMs)
                        }
                        if (morseConfig.isVibrateEnabled) {
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

    val activeColor = remember(morseConfig.bgColorMorse) {
        morseConfig.bgColorMorse.toComposeColor()
    }

    val displayColor = if (morseConfig.isFlashScreen && isSignalOn) {
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