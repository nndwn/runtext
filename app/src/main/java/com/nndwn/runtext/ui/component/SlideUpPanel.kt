package com.nndwn.runtext.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.ui.theme.RuntextTheme
import com.nndwn.runtext.ui.theme.dimens
import com.nndwn.runtext.ui.LocalSizeWidth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


data class SlideUpPanelState(
    val visible: Boolean,
    val enabledDragToDismiss : Boolean = false,
    val containerColor: Color = Color.Unspecified
)


@Composable
fun SlideUpPanel(
    modifier: Modifier = Modifier,
    state: SlideUpPanelState,
    onDismiss: () -> Unit = {},
    content: @Composable (ColumnScope.() -> Unit)
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f) }
    val density = LocalDensity.current

    val isExpand = LocalSizeWidth.current != WindowWidthSizeClass.Compact
    val dismissThreshold = with(density) { 150.dp.toPx() }

    val containerColor = if (state.containerColor != Color.Unspecified) {
        state.containerColor
    } else MaterialTheme.colorScheme.surface

    BackHandler(state.visible) {
        onDismiss()
    }

    LaunchedEffect(state.visible) {
        if (state.visible) {
            offsetY.snapTo(0f)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Scrim(state.visible) {
            onDismiss()
        }

        AnimatedVisibility(
            visible = state.visible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Surface(
                color = containerColor,
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .heightIn(max = 560.dp)
                    .fillMaxWidth()
                    .offset { IntOffset(0, offsetY.value.roundToInt()) }
                    .panelAppearance(isExpand)
                    .dragToDismiss(
                        enabled = state.enabledDragToDismiss,
                        offsetY = offsetY,
                        dismissThreshold = dismissThreshold,
                        coroutineScope = coroutineScope,
                        onDismiss = onDismiss
                    )
            ) {
                SlideUpPanelContent(
                    drag = state.enabledDragToDismiss,
                    containerColor = containerColor,
                    content = content,
                    modifier = if (isExpand) Modifier else Modifier.navigationBarsPadding()
                )
            }
        }
    }
}

@Composable
private fun Modifier.panelAppearance(isExpand: Boolean): Modifier {
    val dimens = MaterialTheme.dimens
    val shapes = MaterialTheme.shapes
    return if (isExpand) {
        this.padding(
            bottom = dimens.large,
            start = dimens.large,
            end = dimens.large
        )
            .clip(shapes.large)
            .navigationBarsPadding()
    } else {
        this.clip(
            shapes.large.copy(
                bottomEnd = CornerSize(0.dp),
                bottomStart = CornerSize(0.dp)
            )
        )
    }
}

private fun Modifier.dragToDismiss(
    enabled: Boolean,
    offsetY: Animatable<Float, *>,
    dismissThreshold: Float,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit
): Modifier = if (enabled) {
    this.pointerInput(Unit) {
        detectVerticalDragGestures(
            onDragEnd = {
                if (offsetY.value > dismissThreshold) {
                    onDismiss()
                } else {
                    coroutineScope.launch {
                        offsetY.animateTo(0f, animationSpec = spring())
                    }
                }
            },
            onDragCancel = {
                coroutineScope.launch {
                    offsetY.animateTo(0f, animationSpec = spring())
                }
            },
            onVerticalDrag = { change, dragAmount ->
                change.consume()
                coroutineScope.launch {
                    offsetY.snapTo((offsetY.value + dragAmount).coerceAtLeast(0f))
                }
            }
        )
    }
} else this

@Composable
private fun SlideUpPanelContent(
    modifier: Modifier = Modifier,
    drag : Boolean,
    containerColor : Color,
    content: @Composable (ColumnScope.() -> Unit)
){
    val colorDragIcon =  if (containerColor.luminance() > 0.5f ) {
        Color.Black.copy(alpha = 0.3f)
    } else {
        Color.White.copy(alpha = 0.3f)
    }
    Column(
        modifier = modifier.fillMaxWidth()) {
        if (drag) {
            Box(
                modifier = Modifier
                    .padding(
                        top = MaterialTheme.dimens.small,
                        bottom = MaterialTheme.dimens.medium
                    )
                    .width(MaterialTheme.dimens.extraLarge)
                    .height(MaterialTheme.dimens.extraSmall)
                    .clip(CircleShape)
                    .background(colorDragIcon)
                    .align(Alignment.CenterHorizontally)
            )
        }

        content()
    }
}


@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    var show by remember { mutableStateOf(false) }

    CompositionLocalProvider(
        LocalSizeWidth provides WindowWidthSizeClass.Compact
    ) {
        RuntextTheme {
            Button(
                onClick = { show = !show },
                modifier = Modifier.padding(MaterialTheme.dimens.medium)
            ) { }
            SlideUpPanel(
              state = SlideUpPanelState(
                  visible = true,
                  enabledDragToDismiss = true,
                  containerColor = MaterialTheme.colorScheme.secondaryContainer
              )
            ) {
                Spacer(modifier = Modifier.height(MaterialTheme.dimens.extraLarge))
            }
        }
    }

}