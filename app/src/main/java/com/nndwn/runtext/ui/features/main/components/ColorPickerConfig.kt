package com.nndwn.runtext.ui.features.main.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.theme.toArgbLong
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
        Column(modifier = Modifier.animateContentSize()) {
            Text(label, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(currentValue.toComposeColor())
                    .clickable { onToggleExpand() }
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    val currentColor = currentValue.toComposeColor()
                    val r = (currentColor.red * 255).toInt()
                    val g = (currentColor.green * 255).toInt()
                    val b = (currentColor.blue * 255).toInt()
                    val a = currentColor.alpha

                    RGBSlider(
                        label = "R",
                        value = r,
                        onValueChange = { newR ->
                            onColorChange(
                                Color(red = newR / 255f, green = g / 255f, blue = b / 255f, alpha = a).toArgbLong()
                            )
                        },
                        color = Color.Red
                    )
                    RGBSlider(
                        label = "G",
                        value = g,
                        onValueChange = { newG ->
                            onColorChange(
                                Color(red = r / 255f, green = newG / 255f, blue = b / 255f, alpha = a).toArgbLong()
                            )
                        },
                        color = Color.Green
                    )
                    RGBSlider(
                        label = "B",
                        value = b,
                        onValueChange = { newB ->
                            onColorChange(
                                Color(red = r / 255f, green = g / 255f, blue = newB / 255f, alpha = a).toArgbLong()
                            )
                        },
                        color = Color.Blue
                    )
                }
            }
        }
    }
}

@Composable
private fun RGBSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..255f,
            colors = SliderDefaults.colors(
                thumbColor = Palette.White,
                activeTrackColor = color,
                inactiveTrackColor = Palette.Grey.copy(alpha = 0.3f)
            )
        )
    }
}
