package com.nndwn.runtext.ui.component

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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.ui.theme.RuntextTheme
import com.nndwn.runtext.ui.theme.dimens
import com.nndwn.runtext.ui.utils.LocalSizeWidth
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SlideUpPanel(
    visible: Boolean,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    enableDragToDismiss: Boolean = false,
    onDismiss: () -> Unit = {},
    content: @Composable (ColumnScope.() -> Unit)
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f) }
    val density = LocalDensity.current

    val isExpand = LocalSizeWidth.current != WindowWidthSizeClass.Compact
    val dismissThreshold = with(density) { 150.dp.toPx() }

    LaunchedEffect(visible) {
        if (visible) {
            offsetY.snapTo(0f)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Scrim(visible) {
            onDismiss()
        }

        AnimatedVisibility(
            visible = visible,
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
                    .then(
                        if (isExpand) {
                            Modifier
                                .padding(
                                    bottom = MaterialTheme.dimens.large,
                                    start = MaterialTheme.dimens.large,
                                    end = MaterialTheme.dimens.large
                                )
                                .clip(MaterialTheme.shapes.large)
                                .navigationBarsPadding()
                        } else {
                            Modifier
                                .clip(
                                    MaterialTheme.shapes.large.copy(
                                        bottomEnd = CornerSize(0.dp),
                                        bottomStart = CornerSize(0.dp)
                                    )
                                )
                        }
                    )

                    .then(
                        if (enableDragToDismiss) {
                            Modifier.pointerInput(Unit) {
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
                                            offsetY.snapTo(
                                                (offsetY.value + dragAmount).coerceAtLeast(
                                                    0f
                                                )
                                            )
                                        }
                                    }
                                )
                            }
                        } else Modifier
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isExpand) {
                                Modifier
                            } else Modifier.navigationBarsPadding()
                        )
                ) {
                    if (enableDragToDismiss) {
                        Box(
                            modifier = Modifier
                                .padding(
                                    top = MaterialTheme.dimens.small,
                                    bottom = MaterialTheme.dimens.medium
                                )
                                .width(MaterialTheme.dimens.extraLarge)
                                .height(MaterialTheme.dimens.extraSmall)
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                        alpha = 0.3f
                                    )
                                )
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                    content()
                }
            }
        }
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
                visible = show,
                enableDragToDismiss = true,
                onDismiss = {
                    show = false
                }
            ) {
                Spacer(modifier = Modifier.height(MaterialTheme.dimens.extraLarge))
            }
        }
    }

}