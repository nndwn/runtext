package com.nndwn.runtext.ui.features.main.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.R
import com.nndwn.runtext.extentions.shimmer
import com.nndwn.runtext.ui.component.ConfigCard
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.utils.Dimens

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

@Composable
fun SpeedConfigSkeleton(
    shimmerProgress: Float,
    modifier: Modifier = Modifier
) {
    ConfigCard(modifier = modifier
        .shimmer(
            progress = shimmerProgress,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
            shimmerColor = Palette.Grey,
            shape = RoundedCornerShape(Dimens.RoundedCorner)
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )
        }
    }
}
