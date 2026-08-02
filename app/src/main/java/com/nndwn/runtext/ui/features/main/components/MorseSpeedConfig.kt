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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.nndwn.runtext.R
import com.nndwn.runtext.ui.component.ConfigCard
import com.nndwn.runtext.ui.theme.Palette

@Composable
fun MorseSpeedConfig(
    speed: Int,
    onSpeedChange: (Int) -> Unit
){
    ConfigCard{
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.speed),
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "$speed WPM",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Slider(
            value = speed.toFloat(),
            onValueChange = {onSpeedChange(it.toInt())},
            valueRange = 5f..40f,
            steps = 34,
            colors = SliderDefaults.colors(
                thumbColor = Palette.White,
                activeTrackColor = Palette.White,
                inactiveTrackColor = Palette.Grey,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            )
        )
    }
}