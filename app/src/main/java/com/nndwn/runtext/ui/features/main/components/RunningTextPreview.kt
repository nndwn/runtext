package com.nndwn.runtext.ui.features.main.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.ui.theme.toComposeColor
import com.nndwn.runtext.ui.utils.fontFamilyFor
import kotlinx.coroutines.isActive
import kotlin.math.abs
import kotlin.text.ifEmpty
import kotlin.text.isEmpty

@Composable
fun RunningTextPreview(settings: AppSettings) {
    val text = settings.lastText.ifEmpty { "PREVIEW" }
    val textMeasurer = rememberTextMeasurer()

    val fontFamily = fontFamilyFor(settings.fontType)
    val fontWeight = FontWeight.Normal // Default to normal for the new curated fonts

    // RTL detection for consistency
    val isRtl = remember(text) {
        if (text.isEmpty()) false
        else {
            val bidi = java.text.Bidi(
                text,
                java.text.Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT,
            )
            bidi.isRightToLeft
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        val containerWidthPx = constraints.maxWidth.toFloat()

        // Measure text first to get width for gradient
        val measureStyle = TextStyle(
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontSize = 40.sp
        )
        val initialLayoutResult = remember(text, fontFamily, fontWeight) {
            textMeasurer.measure(
                text = text,
                style = measureStyle,
                maxLines = 1,
                softWrap = false,
            )
        }
        val textWidth = initialLayoutResult.size.width.toFloat()
        val textHeight = initialLayoutResult.size.height.toFloat()

        val textStyle = if (settings.textColorType == com.nndwn.runtext.data.model.TextColorType.GRADIENT) {
            val color1 = settings.gradientColorsArgb.getOrElse(0) { settings.textColorArgb }.toComposeColor()
            val color2 = settings.gradientColorsArgb.getOrElse(1) { settings.textColorArgb }.toComposeColor()

            val endOffset = if (settings.gradientPositionHorizontal) {
                Offset(textWidth, 0f)
            } else {
                Offset(0f, textHeight)
            }

            TextStyle(
                fontFamily = fontFamily,
                fontWeight = fontWeight,
                fontSize = 40.sp,
                brush = Brush.linearGradient(
                    0.0f to color1,
                    settings.gradientDistance to color2,
                    start = Offset(0f, 0f),
                    end = endOffset
                )
            )
        } else {
            TextStyle(
                fontFamily = fontFamily,
                fontWeight = fontWeight,
                fontSize = 40.sp,
                color = settings.textColorArgb.toComposeColor(),
            )
        }

        val startX = if (isRtl) -textWidth else containerWidthPx
        val offsetX = remember { Animatable(startX) }

        LaunchedEffect(settings.speed, textWidth, containerWidthPx, isRtl) {
            val sX = if (isRtl) -textWidth else containerWidthPx
            val eX = if (isRtl) containerWidthPx else -textWidth
            val dist = abs(eX - sX)

            val dur = ((dist / settings.speed.coerceAtLeast(1f)) * 1000).toInt().coerceAtLeast(500)

            offsetX.snapTo(sX)
            while (isActive) {
                offsetX.animateTo(
                    targetValue = eX,
                    animationSpec = tween(durationMillis = dur, easing = LinearEasing),
                )
                offsetX.snapTo(sX)
            }
        }

        Text(
            text = text,
            style = textStyle,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier
                .wrapContentWidth(unbounded = true, align = Alignment.Start)
                .graphicsLayer {
                    translationX = offsetX.value
                    rotationY = if (settings.isMirrorMode) 180f else 0f
                }
        )
    }
}
