package com.nndwn.runtext.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.ui.utils.LocalIsTablet
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SlideUpPanel(
    visible : Boolean,
    modifier : Modifier = Modifier,
    containerColor : Color = Color.White,
    enableDragToDismiss: Boolean = false,
    onDismiss: () -> Unit = {},
    content : @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f) }
    val density = LocalDensity.current
    val isTablet = LocalIsTablet.current
    val dismissThreshold = with(density) { 150.dp.toPx() }


    LaunchedEffect(visible) {
        if (visible) {
            offsetY.snapTo(0f)
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ){
        Scrim(visible) {
            onDismiss()
        }

        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically(initialOffsetY = {it}) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = {it}) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .padding(horizontal = if (isTablet) 24.dp else 0.dp)
                    .padding(bottom = if (isTablet) 24.dp else 0.dp)
                    .offset { IntOffset(0, offsetY.value.roundToInt()) }
                    .clip(if (isTablet) RoundedCornerShape(24.dp)
                    else RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(containerColor)
                    .then(
                        if (enableDragToDismiss) {
                            Modifier.pointerInput(Unit) {
                                detectVerticalDragGestures(
                                    onDragEnd = {
                                        if (offsetY.value > dismissThreshold) {
                                            onDismiss()
                                        } else {
                                            coroutineScope.launch {
                                                offsetY.animateTo(0f)
                                            }
                                        }
                                    },
                                    onDragCancel = {
                                        coroutineScope.launch {
                                            offsetY.animateTo(0f)
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
                        } else Modifier
                    )
            ){
                Column {
                    if (enableDragToDismiss) {
                        Box(
                            modifier = Modifier
                                .padding(top = 12.dp, bottom = 4.dp)
                                .width(40.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.LightGray.copy(alpha = 0.5f))
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                    content()
                }
            }
        }
    }
}