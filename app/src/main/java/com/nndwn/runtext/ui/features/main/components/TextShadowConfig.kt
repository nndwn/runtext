package com.nndwn.runtext.ui.features.main.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.ShadowConfig
import com.nndwn.runtext.ui.component.ColorPickerField
import com.nndwn.runtext.ui.component.ConfigCard
import com.nndwn.runtext.ui.component.LabeledSlider
import com.nndwn.runtext.ui.component.SwitchRow
import com.nndwn.runtext.ui.features.main.MainUiEvent
import com.nndwn.runtext.ui.theme.dimens
import com.nndwn.runtext.ui.theme.toComposeColor

@Composable
fun TextShadowConfig(
    config : ShadowConfig,
    expandedPickerId: String?,
    onPickerToggle: (String) -> Unit,
    onEvent: (MainUiEvent) -> Unit,
    modifier: Modifier = Modifier
){
    ConfigCard(
        modifier = modifier.animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium)
    ) {
        val keyExpandId = "text_color_shadow"
        SwitchRow(
            title = stringResource(R.string.set_config_text_shadow),
            subtitle = stringResource(R.string.set_config_text_shadow_desc),
            checked = config.isEnabled,
            onCheckedChange = { onEvent(MainUiEvent.ToggleShadow(it))},
        )
        if (config.isEnabled){
            ColorPickerField(
                alpha = true,
                color = config.colorArgb.toComposeColor(),
                isExpanded = expandedPickerId == keyExpandId,
                onToggleExpand = {onPickerToggle(keyExpandId)},
                onColorChange = {onEvent(MainUiEvent.UpdateShadowColor(it))}
            )

            LabeledSlider(
                label = stringResource(R.string.set_config_text_shadow_radius),
                value = config.radius,
                valueRange = 0f..25f,
                displayValueText = "${config.radius.toInt()}",
                onValueChange = { onEvent(MainUiEvent.UpdateShadowRadius(it)) }
            )

            LabeledSlider(
                label = stringResource(R.string.set_config_text_shadow_rotation),
                value = config.rotation,
                valueRange = 0f..360f,
                displayValueText = "${config.rotation.toInt()}",
                onValueChange = { onEvent(MainUiEvent.UpdateShadowRotation(it)) }
            )
        }
    }
}