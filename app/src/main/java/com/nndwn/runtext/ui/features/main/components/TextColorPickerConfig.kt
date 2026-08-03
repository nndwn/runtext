package com.nndwn.runtext.ui.features.main.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.TextColorType
import com.nndwn.runtext.data.model.TextStyleConfig
import com.nndwn.runtext.ui.component.ColorPickerField
import com.nndwn.runtext.ui.component.ConfigCard
import com.nndwn.runtext.ui.component.SwitchRow
import com.nndwn.runtext.ui.features.main.MainUiEvent
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.theme.toArgbLong
import com.nndwn.runtext.ui.theme.toComposeColor
import kotlin.math.roundToInt

@Composable
fun TextColorPickerConfig(
    label: String,
    config: TextStyleConfig,
    expandedPickerId: String?,
    onPickerToggle: (String) -> Unit,
    onEvent: (MainUiEvent) -> Unit
) {
    ConfigCard {
        Column(modifier = Modifier.animateContentSize()) {
            Text(label, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(12.dp))

            // Segmented Control
            TextColorTypeSelector(
                currentType = config.colorType,
                onTypeChange = { onEvent(MainUiEvent.UpdateTextColorType(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (config.colorType == TextColorType.SOLID) {
                ColorPickerField(
                    color = config.colorArgb.toComposeColor(),
                    isExpanded = expandedPickerId == "text_solid",
                    onToggleExpand = { onPickerToggle("text_solid") },
                    onColorChange = { onEvent(MainUiEvent.UpdateTextColor(it)) }
                )
            } else {
                val color1 = config.gradientColorsArgb.getOrElse(0) { config.colorArgb }.toComposeColor()
                val color2 = config.gradientColorsArgb.getOrElse(1) { config.colorArgb }.toComposeColor()

                ColorPickerField(
                    color = color1,
                    isExpanded = expandedPickerId == "text_g1",
                    onToggleExpand = { onPickerToggle("text_g1") },
                    onColorChange = { newColor ->
                        onEvent(MainUiEvent.UpdateGradientColors(listOf(newColor, color2.toArgbLong())))
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ColorPickerField(
                    color = color2,
                    isExpanded = expandedPickerId == "text_g2",
                    onToggleExpand = { onPickerToggle("text_g2") },
                    onColorChange = { newColor ->
                        onEvent(MainUiEvent.UpdateGradientColors(listOf(color1.toArgbLong(), newColor)))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Slider(
                    value = config.gradientDistance,
                    onValueChange = { onEvent(MainUiEvent.UpdateGradientDistance(it)) },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = Palette.White,
                        activeTrackColor = color1,
                        inactiveTrackColor = color2
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                SwitchRow(
                    title = stringResource(R.string.set_config_text_color_type_gradient_direction),
                    subtitle = if (config.isGradientHorizontal)
                        stringResource(R.string.set_config_text_color_type_gradient_horizontal) else
                        stringResource(R.string.set_config_text_color_type_gradient_vertical),
                    checked = config.isGradientHorizontal,
                    onCheckedChange = { onEvent(MainUiEvent.ToggleGradientHorizontal(it)) }
                )
            }
        }
    }
}


@Composable
private fun TextColorTypeSelector(
    currentType: TextColorType,
    onTypeChange: (TextColorType) -> Unit
) {
    val types = TextColorType.entries
    val selectedIndex = types.indexOf(currentType)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(4.dp)
    ) {
        val maxWidth = maxWidth
        val itemWidth = maxWidth / types.size

        val offsetAnim by animateFloatAsState(
            targetValue = selectedIndex.toFloat(),
            animationSpec = spring(stiffness = 700f, dampingRatio = 0.8f),
            label = "indicatorOffset"
        )

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (itemWidth.toPx() * offsetAnim).roundToInt(),
                        y = 0
                    )
                }
                .width(itemWidth)
                .fillMaxHeight()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            types.forEach { type ->
                val isSelected = currentType == type
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    label = "contentColor"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTypeChange(type) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(type.icon),
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(type.displayName),
                            color = contentColor,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}


