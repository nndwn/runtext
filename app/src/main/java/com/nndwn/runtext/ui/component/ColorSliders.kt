package com.nndwn.runtext.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.ui.theme.toArgbLong

/**
 * A reusable component that provides RGB sliders to modify a [Color].
 *
 * @param color The current color to be modified.
 * @param onColorChange Callback triggered when any of the RGB values change.
 */
@Composable
fun ColorSliders(
    color: Color,
    onColorChange: (Long) -> Unit
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        val r = (color.red * 255).toInt()
        val g = (color.green * 255).toInt()
        val b = (color.blue * 255).toInt()
        val a = color.alpha

        RGBSlider(
            label = "R",
            value = r,
            onValueChange = { newR ->
                onColorChange(Color(red = newR / 255f, green = g / 255f, blue = b / 255f, alpha = a).toArgbLong())
            },
            color = Color.Red
        )
        RGBSlider(
            label = "G",
            value = g,
            onValueChange = { newG ->
                onColorChange(Color(red = r / 255f, green = newG / 255f, blue = b / 255f, alpha = a).toArgbLong())
            },
            color = Color.Green
        )
        RGBSlider(
            label = "B",
            value = b,
            onValueChange = { newB ->
                onColorChange(Color(red = r / 255f, green = g / 255f, blue = newB / 255f, alpha = a).toArgbLong())
            },
            color = Color.Blue
        )
    }
}

/**
 * A single RGB slider with a label and value display.
 */
@Composable
private fun RGBSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    color: Color
) {
    LabeledSlider(
        label = label,
        value = value.toFloat(),
        valueRange = 0f..255f,
        displayValueText = value.toString(),
        activeTrackColor = color,
        onValueChange = { onValueChange(it.toInt()) }
    )
}