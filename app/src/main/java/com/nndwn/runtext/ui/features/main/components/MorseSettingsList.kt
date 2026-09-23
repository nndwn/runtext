package com.nndwn.runtext.ui.features.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.ui.component.ConfigCard
import com.nndwn.runtext.ui.component.SwitchRow
import com.nndwn.runtext.ui.features.main.MainUiEvent
import com.nndwn.runtext.ui.theme.dimens
import com.nndwn.runtext.ui.theme.toComposeColor

@Composable
fun MorseCodeSettingsList(
    settings: AppSettings,
    expandedPickerId: String?,
    togglePicker: (String) -> Unit,
    dispatch: (MainUiEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium),
    ) {
        MorseColorConfig(
            currentColor = settings.morseConfig.bgColorMorse.toComposeColor(),
            expandedId = expandedPickerId,
            onToggle = togglePicker,
            onEvent = dispatch,
        )
        MorseSpeedConfig(
            speed = settings.morseConfig.morseWpm,
            event = dispatch,
        )
        ConfigCard {
            SwitchRow(
                title = stringResource(R.string.set_config_morse_flash_screen),
                subtitle = stringResource(R.string.set_config_morse_flash_screen_desc),
                checked = settings.morseConfig.isFlashScreen,
                onCheckedChange = { dispatch(MainUiEvent.UpdateFlashScreen(it)) },
            )
        }
        MorseTorchConfig(
            enable = settings.morseConfig.isTorchEnabled,
            event = dispatch,
        )
        ConfigCard {
            SwitchRow(
                title = stringResource(R.string.set_config_morse_sound),
                subtitle = stringResource(R.string.set_config_morse_sound_desc),
                checked = settings.morseConfig.isSoundEnabled,
                onCheckedChange = { dispatch(MainUiEvent.UpdateSoundEnabled(it)) },
            )
        }
        ConfigCard {
            SwitchRow(
                title = stringResource(R.string.set_config_morse_vibration),
                subtitle = stringResource(R.string.set_config_morse_vibration_desc),
                checked = settings.morseConfig.isVibrateEnabled,
                onCheckedChange = { dispatch(MainUiEvent.UpdateVibrateEnabled(it)) },
            )
        }
    }
}