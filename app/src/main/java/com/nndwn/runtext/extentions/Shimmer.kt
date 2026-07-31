package com.nndwn.runtext.extentions

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp

fun Modifier.shimmer(
    backgroundColor: Color = Color.LightGray.copy(alpha = 0.4f),
    shape: Shape = RectangleShape,
    shimmerColor: Color = Color.White,
    duration: Int = 1300,
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
fun ShimmerCardItem(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(16.dp)

    Column(
        modifier = modifier
            .width(140.dp)
            .shimmer(
                progress = progress,
                backgroundColor = Color(0xFFE0E0E0),
                shape = cardShape
            )
            .padding(12.dp)
    ) {
        // Thumbnail/Image Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .shimmer(
                    progress = progress,
                    backgroundColor = Color(0xFFD0D0D0),
                    shape = RoundedCornerShape(8.dp)
                )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Title Line Placeholder
        Text(
            text = "Sample",
            fontSize = 10.sp,
            color = Color.Transparent,
            modifier = Modifier
                .shimmer(
                    progress = progress,
                    backgroundColor = Color(0xFFD0D0D0),
                    shape = RoundedCornerShape(4.dp)
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(14.dp)
                .shimmer(
                    progress = progress,
                    backgroundColor = Color(0xFFD0D0D0),
                    shape = RoundedCornerShape(4.dp)
                )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle Line Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(10.dp)
                .shimmer(
                    progress = progress,
                    backgroundColor = Color(0xFFD0D0D0),
                    shape = RoundedCornerShape(4.dp)
                )
        )
    }
}

// Implementasi LazyRow Utama
@Composable
fun ShimmerLazyRowSample() {
    // 1. Buat 1 Clock Animasi di level Parent
    val transition = rememberInfiniteTransition(label = "SharedShimmerTransition")
    val shimmerProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SharedShimmerProgress"
    )

    // 2. Lempar nilai `shimmerProgress` ke seluruh item LazyRow
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(count = 10) { index ->
            ShimmerCardItem(progress = shimmerProgress)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShimmerLazyRowPreview() {
    ShimmerLazyRowSample()
}