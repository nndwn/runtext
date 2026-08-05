package com.nndwn.runtext.ui.features.main.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.data.model.AppMode
import com.nndwn.runtext.extentions.shrinkRadius
import com.nndwn.runtext.ui.theme.RuntextTheme
import com.nndwn.runtext.ui.theme.dimens
import kotlin.math.roundToInt

@Composable
fun AppModeSettings(
    currentMode: AppMode,
    onModeChange: (AppMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = AppMode.entries
    val selectedIndex = modes.indexOf(currentMode)

    val paddingValue = MaterialTheme.dimens.extraSmall
    val outerShape = MaterialTheme.shapes.medium

    val innerShape = outerShape.shrinkRadius(paddingValue)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(
                width = MaterialTheme.dimens.borderMedium,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = outerShape
            )
            .clip(MaterialTheme.shapes.medium)
            .padding(paddingValue)
    ) {
        val maxWidth = maxWidth
        val itemWidth = maxWidth / modes.size

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
                .clip(innerShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            modes.forEach { mode ->
                AppModeItem(
                    mode = mode,
                    isSelected = currentMode == mode,
                    onClick = { onModeChange(mode) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AppModeItem(
    mode: AppMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onSurface
                      else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 200),
        label = "contentColor"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = mode.icon),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
            )
            Spacer(modifier = Modifier.width(MaterialTheme.dimens.extraSmall))
            Text(
                text = stringResource(id = mode.displayName),
                color = contentColor,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            )
        }
    }
}

@Preview
@Composable
private fun Preview(){
    RuntextTheme {
        AppModeSettings(
            currentMode = AppMode.RUNNING_TEXT,
            onModeChange = {}
        )
    }
}

