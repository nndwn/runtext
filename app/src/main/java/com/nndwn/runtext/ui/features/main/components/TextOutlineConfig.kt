package com.nndwn.runtext.ui.features.main.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.StrokeConfig
import com.nndwn.runtext.ui.component.ColorPickerField
import com.nndwn.runtext.ui.component.ConfigCard
import com.nndwn.runtext.ui.component.LabeledSlider
import com.nndwn.runtext.ui.component.SwitchRow
import com.nndwn.runtext.ui.features.main.MainUiEvent
import com.nndwn.runtext.ui.theme.dimens
import com.nndwn.runtext.ui.theme.toComposeColor
import kotlin.math.roundToInt

@Composable
fun TextOutlineConfig(
    config: StrokeConfig,
    expandedPickerId: String?,
    onPickerToggle: (String) -> Unit,
    onEvent: (MainUiEvent) -> Unit
){
    ConfigCard(
        modifier = Modifier.animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium)
    ) {
        SwitchRow(
            title = stringResource(R.string.set_config_text_outline),
            subtitle = stringResource(R.string.set_config_text_outline_desc),
            checked = config.isEnabled,
            onCheckedChange = { onEvent(MainUiEvent.ToggleStroke(it)) }
        )

        if (config.isEnabled) {
            ColorPickerField(
                alpha = true,
                color = config.colorArgb.toComposeColor(),
                isExpanded = expandedPickerId == "text_color_outline",
                onToggleExpand = { onPickerToggle("text_color_outline") },
                onColorChange = { onEvent(MainUiEvent.UpdateStrokeColor(it)) }
            )

            LabeledSlider(
                label = stringResource(R.string.set_config_text_outline_width),
                value = config.width,
                valueRange = 0f..15f,
                displayValueText = "${config.width.roundToInt()}",
                onValueChange = { onEvent(MainUiEvent.UpdateStrokeWidth(it)) }
            )
        }
    }
}

