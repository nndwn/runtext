package com.nndwn.runtext.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.PathParser

@Composable
fun LogoAnimation(
    modifier: Modifier = Modifier,
    tint : Color
) {
    val pathBase = remember {
        PathParser.createPathFromPathData(
            "M185.36,107.97l-9.09,-35.47l-131.06,0l8.74,35.47l39.08,0l30.77,124.85l43.33,0l-30.77,-124.85l49,0z"
        ).asComposePath()
    }
    val pathDot = remember {
        PathParser.createPathFromPathData(
            "M244.76,158.65m-15.35,0a15.35,15.35 0,1 1,30.7 0a15.35,15.35 0,1 1,-30.7 0"
        ).asComposePath()
    }

    val pathDash1 = remember {
        PathParser.createPathFromPathData(
            "M174.54,143.38L207.28,143.38A13.18,13.18 0,0 1,220.46 156.56L220.46,160.74A13.18,13.18 0,0 1,207.28 173.92L174.54,173.92A13.18,13.18 0,0 1,161.36 160.74L161.36,156.56A13.18,13.18 0,0 1,174.54 143.38z"
        ).asComposePath()
    }

    val pathDash2 = remember {
        PathParser.createPathFromPathData(
            "M190.03,185.95L222.77,185.95A13.18,13.18 0,0 1,235.95 199.13L235.95,203.31A13.18,13.18 0,0 1,222.77 216.49L190.03,216.49A13.18,13.18 0,0 1,176.85 203.31L176.85,199.13A13.18,13.18 0,0 1,190.03 185.95z"
        ).asComposePath()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "TransmitterTransition")

    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DotAlpha"
    )

    val dash1Alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 600,
                delayMillis = 200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Dash1Alpha"
    )

    val dash2Alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 600,
                delayMillis = 400,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Dash2Alpha"
    )

    Canvas(modifier = modifier.size(200.dp)) {
        val viewportSize = 305.32f
        val scaleX = size.width / viewportSize
        val scaleY = size.height / viewportSize

        scale(scaleX, scaleY, pivot = Offset.Zero) {
            drawPath(
                path = pathBase,
                color = tint
            )

            drawPath(
                path = pathDot,
                color = tint.copy(alpha = dotAlpha)
            )
            drawPath(
                path = pathDash1,
                color = tint.copy(alpha = dash1Alpha)
            )
            drawPath(
                path = pathDash2,
                color = tint.copy(alpha = dash2Alpha)
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    LogoAnimation(
        modifier = Modifier,
        tint = Color.Red
    )
}