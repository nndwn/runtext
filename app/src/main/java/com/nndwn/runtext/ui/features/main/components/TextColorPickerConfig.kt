package com.nndwn.runtext.ui.features.main.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.runtext.data.model.TextColorType
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.theme.toArgbLong
import com.nndwn.runtext.ui.theme.toComposeColor
import kotlin.math.roundToInt

@Composable
fun TextColorPickerConfig(
    label: String,
    currentType: TextColorType,
    currentColor: Long,
    horizontalPosition : Boolean,
    gradientColors: List<Long>,
    gradientDistance: Float,
    onTypeChange: (TextColorType) -> Unit,
    onColorChange: (Long) -> Unit,
    onToggleHorizontalPosition : (Boolean) -> Unit,
    onGradientColorsChange: (List<Long>) -> Unit,
    onGradientDistanceChange: (Float) -> Unit
) {
    var expandedColorIndex by remember { mutableIntStateOf(-1) } // -1: closed, 0: solid/first, 1: second

    ConfigCard {
        Column(modifier = Modifier.animateContentSize()) {
            Text(label, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(12.dp))

            // Segmented Control
            TextColorTypeSelector(
                currentType = currentType,
                onTypeChange = {
                    onTypeChange(it)
                    expandedColorIndex = -1
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (currentType == TextColorType.SOLID) {
                // Solid Color Box
                ColorBox(
                    color = currentColor.toComposeColor(),
                    onClick = { expandedColorIndex = if (expandedColorIndex == 0) -1 else 0 }
                )

                AnimatedVisibility(
                    visible = expandedColorIndex == 0,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    ColorSliders(
                        color = currentColor.toComposeColor(),
                        onColorChange = onColorChange
                    )
                }
            } else {
                // Gradient Mode
                val color1 = gradientColors.getOrElse(0) { currentColor }.toComposeColor()
                val color2 = gradientColors.getOrElse(1) { currentColor }.toComposeColor()

                // First Color
                ColorBox(
                    color = color1,
                    onClick = { expandedColorIndex = if (expandedColorIndex == 0) -1 else 0 }
                )
                AnimatedVisibility(
                    visible = expandedColorIndex == 0,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    ColorSliders(
                        color = color1,
                        onColorChange = { newColor ->
                            onGradientColorsChange(listOf(newColor, color2.toArgbLong()))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Second Color
                ColorBox(
                    color = color2,
                    onClick = { expandedColorIndex = if (expandedColorIndex == 1) -1 else 1 }
                )
                AnimatedVisibility(
                    visible = expandedColorIndex == 1,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    ColorSliders(
                        color = color2,
                        onColorChange = { newColor ->
                            onGradientColorsChange(listOf(color1.toArgbLong(), newColor))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Gradient Distance Slider
                GradientDistanceSlider(
                    color1 = color1,
                    color2 = color2,
                    distance = gradientDistance,
                    onDistanceChange = onGradientDistanceChange
                )
                Spacer(modifier = Modifier.height(16.dp))

                SwitchRow(
                    title = "Horizontal",
                    subtitle = "Position Gradient Default Vertical",
                    checked = horizontalPosition,
                    onCheckedChange = onToggleHorizontalPosition,
                    accentColor = Palette.NeonGreen,
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

@Composable
private fun ColorBox(
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .clickable { onClick() }
    )
}

@Composable
private fun GradientDistanceSlider(
    color1: Color,
    color2: Color,
    distance: Float,
    onDistanceChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(12.dp)
//                .clip(RoundedCornerShape(6.dp))
//                .background(
//                    Brush.linearGradient(
//                        colors = listOf(color1, color2)
//                    )
//                )
//        )
//        Slider(
//            value = value.toFloat(),
//            onValueChange = { onValueChange(it.toInt()) },
//            valueRange = 0f..255f,
//            colors = SliderDefaults.colors(
//                thumbColor = Palette.White,
//                activeTrackColor = color,
//                inactiveTrackColor = Palette.Grey.copy(alpha = 0.3f)
//            )
//        )
        Slider(
            value = distance,
            onValueChange = onDistanceChange,
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = Palette.White,
                activeTrackColor = color1,
                inactiveTrackColor = color2
            )
        )
    }
}
