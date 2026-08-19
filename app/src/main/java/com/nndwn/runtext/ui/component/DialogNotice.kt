package com.nndwn.runtext.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.ui.theme.RuntextTheme
import com.nndwn.runtext.ui.theme.dimens
import com.nndwn.runtext.ui.utils.LocalSizeWidth
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun DialogNotice(
    modifier: Modifier = Modifier,
    visible: Boolean,
    text: String,
    containerColor: Color = Color.Unspecified,
    onDismiss: () -> Unit = {}
) {
    val isExpand = LocalSizeWidth.current != WindowWidthSizeClass.Compact
    val backgroundColor = containerColor.takeOrElse { MaterialTheme.colorScheme.surface }

    LaunchedEffect(visible, text) {
        if (visible) {
            delay(3500.milliseconds)
            onDismiss()
        }
    }

    val (enterAnim, exitAnim) = rememberDialogNoticeTransitions(isExpand)

    AnimatedVisibility(
        visible = visible,
        enter = enterAnim,
        exit = exitAnim,
    ) {
        DialogNoticeContent(
            modifier = modifier,
            isExpand = isExpand,
            backgroundColor = backgroundColor,
            text = text
        )
    }
}

@Composable
private fun rememberDialogNoticeTransitions(isExpand: Boolean): Pair<EnterTransition, ExitTransition> {
    return remember(isExpand) {
        val enter = if (isExpand) {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeIn(animationSpec = tween(200))
        } else {
            scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) + fadeIn(animationSpec = tween(200))
        }

        val exit = if (isExpand) {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(200)
            ) + fadeOut(animationSpec = tween(200))
        } else {
            scaleOut(
                targetScale = 0.8f,
                animationSpec = tween(150)
            ) + fadeOut(animationSpec = tween(150))
        }
        enter to exit
    }
}

@Composable
private fun DialogNoticeContent(
    modifier: Modifier,
    isExpand: Boolean,
    backgroundColor: Color,
    text: String
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = MaterialTheme.dimens.medium,
                vertical = MaterialTheme.dimens.large
            )
            .navigationBarsPadding(),
        contentAlignment = if (isExpand) Alignment.BottomEnd else Alignment.BottomCenter
    ) {
        Surface(
            color = backgroundColor,
            shape = MaterialTheme.shapes.large,
        ) {
            Box(
                modifier = Modifier
                    .padding(
                        vertical = MaterialTheme.dimens.small,
                        horizontal = MaterialTheme.dimens.large
                    )
                    .then(
                        if (isExpand) Modifier.widthIn(max = 560.dp)
                        else Modifier.fillMaxWidth()
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewPhone(){
    var show by remember { mutableStateOf(false) }
    CompositionLocalProvider(
        LocalSizeWidth provides WindowWidthSizeClass.Compact
    ) {
        RuntextTheme {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {show = !show}
                ) {
                    Text("Show")
                }
                DialogNotice(
                    visible = true,
                    text = "Test",
                    onDismiss = { show = false }
                )
            }

        }
    }
}