package com.nndwn.runtext.ui.sample

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nndwn.runtext.data.model.AppMode
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.data.model.FontType
import com.nndwn.runtext.ui.component.ColorPickerDialog
import com.nndwn.runtext.ui.component.FontSelector
import com.nndwn.runtext.ui.features.main.MainViewModel
import com.nndwn.runtext.ui.features.main.components.ConfigCard
import com.nndwn.runtext.ui.theme.NeonCyan
import com.nndwn.runtext.ui.theme.NeonGreen
import com.nndwn.runtext.ui.theme.NeonOrange
import com.nndwn.runtext.ui.theme.NeonRed
import com.nndwn.runtext.ui.theme.PresetBgColors
import com.nndwn.runtext.ui.theme.PresetTextColors
import com.nndwn.runtext.ui.theme.RuntextTheme
import com.nndwn.runtext.ui.theme.ShareTechMonoFamily
import com.nndwn.runtext.ui.theme.toArgbLong
import com.nndwn.runtext.ui.theme.toComposeColor

@Composable
fun InputScreen(
    viewModel: MainViewModel,
    onNavigateToDisplay: () -> Unit,
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Camera permission launcher (for Torch feature)
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.toggleTorch()
    }

    InputScreenContent(
        settings = settings,
        onNavigateToDisplay = onNavigateToDisplay,
        onUpdateText = viewModel::updateText,
        onClearText = viewModel::clearText,
        onUpdateMode = viewModel::updateMode,
        onUpdateSpeed = viewModel::updateSpeed,
        onUpdateTextColor = viewModel::updateTextColor,
        onUpdateBgColor = viewModel::updateBgColor,
        onUpdateFontType = viewModel::updateFontType,
        onUpdateGoogleFontName = viewModel::updateGoogleFontName,
        onToggleMirrorMode = viewModel::toggleMirrorMode,
        onUpdateMorseWpm = viewModel::updateMorseWpm,
        onToggleFlashScreen = viewModel::toggleFlashScreen,
        onToggleTorch = { on ->
            if (on) {
                val granted = context.checkSelfPermission(Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED
                if (granted) viewModel.toggleTorch() else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            } else {
                viewModel.toggleTorch()
            }
        },
        onPlaySOS = {
            viewModel.updateMode(AppMode.MORSE_CODE)
            viewModel.playSOS()
            onNavigateToDisplay()
        }
    )
}

@Composable
fun InputScreenContent(
    settings: AppSettings,
    onNavigateToDisplay: () -> Unit,
    onUpdateText: (String) -> Unit,
    onClearText: () -> Unit,
    onUpdateMode: (AppMode) -> Unit,
    onUpdateSpeed: (Float) -> Unit,
    onUpdateTextColor: (Long) -> Unit,
    onUpdateBgColor: (Long) -> Unit,
    onUpdateFontType: (FontType) -> Unit,
    onUpdateGoogleFontName: (String) -> Unit,
    onToggleMirrorMode: () -> Unit,
    onUpdateMorseWpm: (Int) -> Unit,
    onToggleFlashScreen: () -> Unit,
    onToggleTorch: (Boolean) -> Unit,
    onPlaySOS: () -> Unit
) {
    var showTextColorPicker by remember { mutableStateOf(false) }
    var showBgColorPicker by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (settings.lastText.isNotEmpty()) onNavigateToDisplay()
                },
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Play") },
                text = { Text("PLAY", fontWeight = FontWeight.Bold) },
                containerColor = NeonGreen,
                contentColor = Color.Black,
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            // ── HEADER ──
            Text(
                text = "runTxt",
                style = TextStyle(
                    fontFamily = ShareTechMonoFamily,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Normal,
                    shadow = Shadow(color = NeonGreen, offset = Offset.Zero, blurRadius = 30f),
                ),
                color = NeonGreen,
                modifier = Modifier.padding(bottom = 4.dp),
            )
            Text(
                text = "LED Banner & Morse Code",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp),
            )



            Spacer(Modifier.height(20.dp))

            // ── MODE SELECTOR ──
            Text(
                "Mode",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = settings.mode == AppMode.RUNNING_TEXT,
                    onClick = { onUpdateMode(AppMode.RUNNING_TEXT) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    icon = { Icon(Icons.Default.TextFields, null, Modifier.size(18.dp)) },
                ) { Text("Running Text") }

                SegmentedButton(
                    selected = settings.mode == AppMode.MORSE_CODE,
                    onClick = { onUpdateMode(AppMode.MORSE_CODE) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    icon = { Icon(Icons.Default.FlashOn, null, Modifier.size(18.dp)) },
                ) { Text("Morse Code") }
            }

            Spacer(Modifier.height(20.dp))

            // ── DYNAMIC CONFIG PANEL ──
            AnimatedContent(
                targetState = settings.mode,
                transitionSpec = {
                    (fadeIn() + slideInVertically { it / 4 }) togetherWith
                            (fadeOut() + slideOutVertically { -it / 4 })
                },
                label = "modePanel",
            ) { mode ->
                when (mode) {
                    AppMode.RUNNING_TEXT -> RunningTextConfigPanel(
                        settings = settings,
                        onUpdateSpeed = onUpdateSpeed,
                        onShowTextColorPicker = { showTextColorPicker = true },
                        onShowBgColorPicker = { showBgColorPicker = true },
                        onUpdateFontType = onUpdateFontType,
                        onUpdateGoogleFontName = onUpdateGoogleFontName,
                        onToggleMirrorMode = onToggleMirrorMode
                    )

                    AppMode.MORSE_CODE -> MorseConfigPanel(
                        settings = settings,
                        onUpdateMorseWpm = onUpdateMorseWpm,
                        onToggleFlashScreen = onToggleFlashScreen,
                        onToggleTorch = onToggleTorch,
                        onPlaySOS = onPlaySOS
                    )
                }
            }

            // Extra space for FAB
            Spacer(Modifier.height(88.dp))
        }
    }

    // ── Colour picker dialogs ──
    if (showTextColorPicker) {
        ColorPickerDialog(
            title = "Text Color",
            currentColor = settings.textColorArgb.toComposeColor(),
            presetColors = PresetTextColors,
            onColorSelected = { onUpdateTextColor(it.toArgbLong()) },
            onDismiss = { showTextColorPicker = false },
        )
    }
    if (showBgColorPicker) {
        ColorPickerDialog(
            title = "Background Color",
            currentColor = settings.bgColorArgb.toComposeColor(),
            presetColors = PresetBgColors,
            onColorSelected = { onUpdateBgColor(it.toArgbLong()) },
            onDismiss = { showBgColorPicker = false },
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Running Text configuration panel
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun RunningTextConfigPanel(
    settings: AppSettings,
    onUpdateSpeed: (Float) -> Unit,
    onShowTextColorPicker: () -> Unit,
    onShowBgColorPicker: () -> Unit,
    onUpdateFontType: (FontType) -> Unit,
    onUpdateGoogleFontName: (String) -> Unit,
    onToggleMirrorMode: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Speed
        ConfigCard {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Speed", style = MaterialTheme.typography.titleSmall)
                Text("${settings.speed.toInt()} px/s", style = MaterialTheme.typography.bodySmall, color = NeonCyan)
            }
            Slider(
                value = settings.speed,
                onValueChange = onUpdateSpeed,
                valueRange = 50f..500f,
                colors = SliderDefaults.colors(thumbColor = NeonGreen, activeTrackColor = NeonGreen),
            )
        }

        // Colours
        ConfigCard {
            Text("Colors", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ColorChip("Text", settings.textColorArgb.toComposeColor(), onShowTextColorPicker, Modifier.weight(1f))
                ColorChip("Background", settings.bgColorArgb.toComposeColor(), onShowBgColorPicker, Modifier.weight(1f))
            }
        }

        // Font
        ConfigCard {
            Text("Font", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            FontSelector(
                selectedFont = settings.fontType,

                onFontSelected = onUpdateFontType,

            )
        }

        // Mirror mode
        ConfigCard {
            SwitchRow(
                title = "Mirror Mode",
                subtitle = "Flip text for mirror reflection",
                checked = settings.isMirrorMode,
                onCheckedChange = { onToggleMirrorMode() },
                accentColor = NeonGreen,
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Morse Code configuration panel
// ═══════════════════════════════════════════════════════════════════════════

@Composable
private fun MorseConfigPanel(
    settings: AppSettings,
    onUpdateMorseWpm: (Int) -> Unit,
    onToggleFlashScreen: () -> Unit,
    onToggleTorch: (Boolean) -> Unit,
    onPlaySOS: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // WPM speed
        ConfigCard {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Speed", style = MaterialTheme.typography.titleSmall)
                Text("${settings.morseWpm} WPM", style = MaterialTheme.typography.bodySmall, color = NeonCyan)
            }
            Slider(
                value = settings.morseWpm.toFloat(),
                onValueChange = { onUpdateMorseWpm(it.toInt()) },
                valueRange = 5f..40f,
                steps = 34,
                colors = SliderDefaults.colors(thumbColor = NeonOrange, activeTrackColor = NeonOrange),
            )
        }

        // Flash screen
        ConfigCard {
            SwitchRow(
                title = "Flash Screen",
                subtitle = "Screen flashes with morse signal",
                checked = settings.isFlashScreen,
                onCheckedChange = { onToggleFlashScreen() },
                accentColor = NeonOrange,
            )
        }

        // Torch
        ConfigCard {
            SwitchRow(
                title = "Flashlight / Torch",
                subtitle = "Use camera flash for morse signal",
                checked = settings.isTorchEnabled,
                onCheckedChange = onToggleTorch,
                accentColor = NeonOrange,
            )
        }

        // SOS button
        OutlinedButton(
            onClick = onPlaySOS,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, NeonRed),
            contentPadding = PaddingValues(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(Icons.Default.Warning, contentDescription = "SOS", tint = NeonRed, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(8.dp))
            Text("SOS", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = NeonRed)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Shared small components
// ═══════════════════════════════════════════════════════════════════════════


@Composable
private fun ColorChip(label: String, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(onClick = onClick, modifier = modifier.height(48.dp), shape = RoundedCornerShape(12.dp)) {
        Box(
            Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
        )
        Spacer(Modifier.width(8.dp))
        Text(label, maxLines = 1)
    }
}

@Composable
private fun SwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color,
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = accentColor,
                checkedTrackColor = accentColor.copy(alpha = 0.3f),
            ),
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// Previews
// ═══════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true, name = "Running Text Mode")
@Composable
private fun InputScreenRunningTextPreview() {
    RuntextTheme {
        InputScreenContent(
            settings = AppSettings(
                lastText = "HELLO WORLD",
                mode = AppMode.RUNNING_TEXT,
                speed = 250f
            ),
            onNavigateToDisplay = {},
            onUpdateText = {},
            onClearText = {},
            onUpdateMode = {},
            onUpdateSpeed = {},
            onUpdateTextColor = {},
            onUpdateBgColor = {},
            onUpdateFontType = {},
            onUpdateGoogleFontName = {},
            onToggleMirrorMode = {},
            onUpdateMorseWpm = {},
            onToggleFlashScreen = {},
            onToggleTorch = {},
            onPlaySOS = {}
        )
    }
}

@Preview(showBackground = true, name = "Morse Code Mode")
@Composable
private fun InputScreenMorsePreview() {
    RuntextTheme {
        InputScreenContent(
            settings = AppSettings(
                lastText = "SOS",
                mode = AppMode.MORSE_CODE,
                morseWpm = 20,
                isFlashScreen = true,
                isTorchEnabled = false
            ),
            onNavigateToDisplay = {},
            onUpdateText = {},
            onClearText = {},
            onUpdateMode = {},
            onUpdateSpeed = {},
            onUpdateTextColor = {},
            onUpdateBgColor = {},
            onUpdateFontType = {},
            onUpdateGoogleFontName = {},
            onToggleMirrorMode = {},
            onUpdateMorseWpm = {},
            onToggleFlashScreen = {},
            onToggleTorch = {},
            onPlaySOS = {}
        )
    }
}
