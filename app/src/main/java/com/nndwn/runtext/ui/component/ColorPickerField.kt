package com.nndwn.runtext.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.theme.dimens
import com.nndwn.runtext.ui.theme.toArgbLong

@Composable
fun ColorPickerField(
    modifier: Modifier = Modifier,
    label: String? = null,
    alpha: Boolean = false,
    color: Color,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onColorChange: (Long) -> Unit,
) {
    Column(modifier = modifier
        .animateContentSize(),
       verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium)
    ) {
        if (label != null) {
            Text(label, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.small))
        }

        ColorBox(
            color = color,
            onClick = onToggleExpand
        )

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {

            ColorSliders(
                alpha = alpha,
                color = color,
                onColorChange = onColorChange
            )
        }
    }
}
@Composable
private fun ColorBox(
    color: Color,
    height: Dp = 40.dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(MaterialTheme.shapes.small)
            .background(color)
            .border(
                width = MaterialTheme.dimens.borderMedium,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.small
            )
            .clickable { onClick() }
    )
}

@Composable
private fun ColorSliders(
    color: Color,
    onColorChange: (Long) -> Unit,
    alpha: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small)
        ) {
        val r = (color.red * 255).toInt()
        val g = (color.green * 255).toInt()
        val b = (color.blue * 255).toInt()
        val a = (color.alpha * 255).toInt()

        RGBSlider(
            label = "R",
            value = r,
            onValueChange = { newR ->
                onColorChange(
                    Color(
                        red = newR / 255f,
                        green = g / 255f,
                        blue = b / 255f,
                        alpha = a / 255f
                    ).toArgbLong()
                )
            },
            color = Color.Red
        )
        RGBSlider(
            label = "G",
            value = g,
            onValueChange = { newG ->
                onColorChange(
                    Color(
                        red = r / 255f,
                        green = newG / 255f,
                        blue = b / 255f,
                        alpha = a / 255f
                    ).toArgbLong()
                )
            },
            color = Color.Green
        )
        RGBSlider(
            label = "B",
            value = b,
            onValueChange = { newB ->
                onColorChange(
                    Color(
                        red = r / 255f,
                        green = g / 255f,
                        blue = newB / 255f,
                        alpha = a / 255f
                    ).toArgbLong()
                )
            },
            color = Color.Blue
        )

        if (alpha) {
            RGBSlider(
                label = "A",
                value = a,
                onValueChange = { newA ->
                    onColorChange(
                        Color(
                            red = r / 255f,
                            green = g / 255f,
                            blue = b / 255f,
                            alpha = newA / 255f
                        ).toArgbLong()
                    )
                },
                color = Palette.White
            )
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
    LabeledSlider(
        label = label,
        value = value.toFloat(),
        valueRange = 0f..255f,
        displayValueText = value.toString(),
        activeTrackColor = color,
        onValueChange = { onValueChange(it.toInt()) }
    )
}