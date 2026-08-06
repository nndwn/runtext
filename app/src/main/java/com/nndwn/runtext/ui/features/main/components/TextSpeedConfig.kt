package com.nndwn.runtext.ui.features.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nndwn.runtext.R
import com.nndwn.runtext.ui.component.ConfigCard
import com.nndwn.runtext.ui.component.SliderTheme

@Composable
fun TextSpeedConfig(
    speed: Float,
    onSpeedChange: (Float) -> Unit
) {
    ConfigCard {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.speed), style = MaterialTheme.typography.titleSmall)
            Text(
                "${speed.toInt()} px/s",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        SliderTheme(
            value = speed,
            onValueChange = onSpeedChange,
            valueRange = 50f..500f
        )
    }
}

