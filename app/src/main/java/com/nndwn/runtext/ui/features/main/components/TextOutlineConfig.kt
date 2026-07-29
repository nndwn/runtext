package com.nndwn.runtext.ui.features.main.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.R
import com.nndwn.runtext.ui.component.ColorPickerField
import com.nndwn.runtext.ui.component.ConfigCard
import com.nndwn.runtext.ui.component.SwitchRow
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.theme.toComposeColor

@Composable
fun TextOutlineConfig(
    outlineEnabled : Boolean,
    currentColor : Long,
    onCheckedChange : (Boolean) -> Unit,
    onPickerToggle: (String) -> Unit,
    expandedPickerId : String?,
    width : Float,
    onWidthChange : (Float) -> Unit,
    outlineColor : (Long) -> Unit
){

    ConfigCard {
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            SwitchRow(
                title = stringResource(R.string.set_config_text_outline),
                subtitle = stringResource(R.string.set_config_text_outline_desc),
                checked = outlineEnabled,
                onCheckedChange = { onCheckedChange(it) },
                accentColor = Palette.NeonGreen
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (outlineEnabled) {
                ColorPickerField(
                   color = currentColor.toComposeColor(),
                    isExpanded = expandedPickerId == "text_color_outline",
                    onToggleExpand = { onPickerToggle("text_color_outline") },
                    onColorChange = outlineColor
                )
                Spacer(modifier = Modifier.height(16.dp))
                Slider(
                    value = width,
                    onValueChange = onWidthChange,
                    valueRange = 0f..15f,
                    colors = SliderDefaults.colors(
                        thumbColor = Palette.White,
                        activeTrackColor = currentColor.toComposeColor(),
                        inactiveTrackColor = Palette.Grey
                    )
                )
            }
        }
    }
}

