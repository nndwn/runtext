package com.nndwn.runtext.ui.features.main.components

import androidx.compose.runtime.Composable
import com.nndwn.runtext.ui.component.ColorPickerField
import com.nndwn.runtext.ui.component.ConfigCard
import com.nndwn.runtext.ui.theme.toComposeColor

@Composable
fun ColorPickerConfig(
    label: String,
    currentValue: Long,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onColorChange: (Long) -> Unit
) {
    ConfigCard {
        ColorPickerField(
            label = label,
            color = currentValue.toComposeColor(),
            isExpanded = isExpanded,
            onToggleExpand = onToggleExpand,
            onColorChange = onColorChange
        )
    }
}
