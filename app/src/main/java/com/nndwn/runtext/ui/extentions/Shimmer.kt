package com.nndwn.runtext.ui.extentions

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.ui.component.Skeleton
import com.nndwn.runtext.ui.theme.RuntextTheme
import com.nndwn.runtext.ui.theme.dimens

fun Modifier.shimmer(
    backgroundColor: Color = Color.LightGray.copy(alpha = 0.4f),
    shape: Shape = RectangleShape,
    shimmerColor: Color = Color.White,
    duration: Int = 800,
    progress: Float? = null
): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }

    val actualProgress = if (progress != null) {
        progress
    } else {
        val transition = rememberInfiniteTransition(label = "LocalShimmerTransition")
        val localAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = duration, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "LocalShimmerProgress"
        )
        localAnim
    }

    val highlightedColor = shimmerColor.copy(alpha = 0.5f).compositeOver(backgroundColor)
    val colors = listOf(backgroundColor, highlightedColor, backgroundColor)

    this
        .onSizeChanged { size = it }
        .drawBehind {
            val width = size.width.toFloat()
            val height = size.height.toFloat()

            if (width == 0f || height == 0f) return@drawBehind
            val shimmerSize = maxOf(width, height) * 0.8f

            val startOffset = -shimmerSize
            val endOffsetWidth = width + shimmerSize
            val endOffsetHeight = height + shimmerSize

            val currentStartX = startOffset + (actualProgress * (endOffsetWidth - startOffset))
            val currentStartY = startOffset + (actualProgress * (endOffsetHeight - startOffset))
            
            val brush = Brush.linearGradient(
                colors = colors,
                start = Offset(x = currentStartX, y = currentStartY),
                end = Offset(x = currentStartX + shimmerSize, y = currentStartY + shimmerSize)
            )

            val outline = shape.createOutline(this.size, layoutDirection, this)
            drawOutline(outline = outline, brush = brush)
        }
}


@Composable
private fun ShimmerLazyRowSample() {
    val transition = rememberInfiniteTransition(label = "TestShimmerTransition")
    val shimmerProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "TestShimmerProgress"
    )

    LazyColumn(
        contentPadding = PaddingValues(
            start = MaterialTheme.dimens.medium,
            end =  MaterialTheme.dimens.medium,

            ),
        verticalArrangement = Arrangement.spacedBy( MaterialTheme.dimens.medium)
    ) {
        stickyHeader {
            Skeleton(
                shimmerProgress = shimmerProgress,
                height = 140.dp
            )
        }
        item {
            Skeleton(
                shimmerProgress = shimmerProgress,
                height = 120.dp
            )
        }
        items(7) {
            Skeleton(
                shimmerProgress = shimmerProgress,
                height = 56.dp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShimmerLazyRowPreview() {
    RuntextTheme {
        ShimmerLazyRowSample()
    }

}