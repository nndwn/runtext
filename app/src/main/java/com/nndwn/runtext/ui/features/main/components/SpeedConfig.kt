package com.nndwn.runtext.ui.features.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.nndwn.runtext.ui.theme.Palette

@Composable
fun SpeedConfig(
    speed: Float,
    onSpeedChange: (Float) -> Unit
) {
    ConfigCard {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Speed", style = MaterialTheme.typography.titleSmall)
            Text(
                "${speed.toInt()} px/s",
                style = MaterialTheme.typography.bodySmall,
                color = Palette.Grey
            )
        }
        Slider(
            value = speed,
            onValueChange = onSpeedChange,
            valueRange = 50f..500f,
            colors = SliderDefaults.colors(
                thumbColor = Palette.White,
                activeTrackColor = Palette.White,
                inactiveTrackColor = Palette.Grey
            ),
        )
    }
}
