package com.nndwn.runtext.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.data.model.TextColorType
import com.nndwn.runtext.ui.theme.toComposeColor
import com.nndwn.runtext.ui.utils.fontFamilyFor
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun RunningTextCoreOptimized(
    settings: AppSettings,
    modifier: Modifier = Modifier,
    defaultPreviewText: String = "PREVIEW"
) {
    val rawText = settings.lastText.ifEmpty { defaultPreviewText }
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val distanceShadow = 4f

    val isRtl = remember(rawText) {
        if (rawText.isEmpty()) false
        else java.text.Bidi(rawText, java.text.Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT).isRightToLeft
    }

    val fontFamily = fontFamilyFor(settings.textStyle.fontType)

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        val containerHeightPx = constraints.maxHeight.toFloat()
        val containerWidthPx = constraints.maxWidth.toFloat()

        val dynamicFontSizeSp = remember(containerHeightPx, density) {
            with(density) {
                (containerHeightPx * 0.55f).toSp().value.coerceIn(14f, 120f).sp
            }
        }

        val baseTextStyle = remember(fontFamily, settings.textStyle.letterSpacingSp) {
            TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = dynamicFontSizeSp,
                letterSpacing = settings.textStyle.letterSpacingSp.sp
            )
        }

        val annotatedText = remember(rawText, settings.textStyle.wordSpacingSp) {
            rawText.toWordSpacedAnnotatedString(settings.textStyle.wordSpacingSp)
        }

        val textLayoutResult = remember(annotatedText, baseTextStyle) {
            textMeasurer.measure(
                text = annotatedText,
                style = baseTextStyle,
                maxLines = 1,
                softWrap = false
            )
        }

        val extraPaddingPx = remember(settings.shadow, settings.stroke, density) {
            with(density) {
                val strokePadding = if (settings.stroke.isEnabled) settings.stroke.width else 0f
                val shadowPadding =
                    if (settings.shadow.isEnabled) (distanceShadow + settings.shadow.radius) else 0f
                (maxOf(strokePadding, shadowPadding) + 5f).dp.toPx()
            }
        }


        val mainBrush = remember(settings.textStyle, textLayoutResult) {
            if (settings.textStyle.colorType == TextColorType.GRADIENT) {
                val color1 =
                    settings.textStyle.gradientColorsArgb.getOrElse(0) { settings.textStyle.colorArgb }
                        .toComposeColor()
                val color2 =
                    settings.textStyle.gradientColorsArgb.getOrElse(1) { settings.textStyle.colorArgb }
                        .toComposeColor()
                val dist = settings.textStyle.gradientDistance.coerceIn(0f, 1f)

                val textWidth = textLayoutResult.size.width.toFloat()
                val textHeight = textLayoutResult.size.height.toFloat()

                val (startOffset, endOffset) = if (settings.textStyle.isGradientHorizontal) {
                    val shift = (dist - 0.5f) * 2f * textWidth
                    Offset(shift, 0f) to Offset(textWidth + shift, 0f)
                } else {
                    val shift = (dist - 0.5f) * 2f * textHeight
                    Offset(0f, shift) to Offset(0f, textHeight + shift)
                }

                Brush.linearGradient(
                    colors = listOf(color1, color2),
                    start = startOffset,
                    end = endOffset
                )
            } else null
        }

        val totalTextWidth = textLayoutResult.size.width.toFloat() + (extraPaddingPx * 2)

        val baseStartX = if (isRtl) -totalTextWidth else containerWidthPx
        val baseEndX = if (isRtl) containerWidthPx else -totalTextWidth

        val startX = if (settings.isMirrorMode) baseEndX else baseStartX
        val endX = if (settings.isMirrorMode) baseStartX else baseEndX

        val durationMillis = remember(settings.speed, totalTextWidth, containerWidthPx, settings.isMirrorMode) {
            val dist = abs(endX - startX)
            val speedFactor = settings.speed.coerceAtLeast(1f)
            val baseDurationSeconds = (dist / containerWidthPx) * (1000f / speedFactor)
            (baseDurationSeconds * 1000).toInt().coerceAtLeast(200)
        }

        val transition = rememberInfiniteTransition(label = "marquee")
        val animatedOffsetX by transition.animateFloat(
            initialValue = startX,
            targetValue = endX,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "offsetX"
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = animatedOffsetX
                    rotationY = if (settings.isMirrorMode) 180f else 0f
                    clip = false
                }
        ) {
            val topOffsetY = (size.height - textLayoutResult.size.height) / 2f
            val baseTopLeft = Offset(extraPaddingPx, topOffsetY)

            if (settings.shadow.isEnabled) {
                val angleInRadians = Math.toRadians(settings.shadow.rotation.toDouble())


                val baseDistancePx = distanceShadow.dp.toPx()

                val strokeOffsetPx = if (settings.stroke.isEnabled && settings.stroke.width > 0) {
                    settings.stroke.width.dp.toPx()
                } else {
                    0f
                }

                val totalShadowDistancePx = baseDistancePx + strokeOffsetPx

                val shadowOffsetX = (totalShadowDistancePx * cos(angleInRadians)).toFloat()
                val shadowOffsetY = (totalShadowDistancePx * sin(angleInRadians)).toFloat()

                drawText(
                    textLayoutResult = textLayoutResult,
                    color = settings.shadow.colorArgb.toComposeColor(),
                    topLeft = baseTopLeft,
                    shadow = Shadow(
                        color = settings.shadow.colorArgb.toComposeColor(),
                        offset = Offset(shadowOffsetX, shadowOffsetY),
                        blurRadius = settings.shadow.radius
                    )
                )
            }

            if (settings.stroke.isEnabled && settings.stroke.width > 0) {
                val scaledStrokeWidthPx = settings.stroke.width.dp.toPx()

                drawIntoCanvas { canvas ->
                    canvas.save()
                    canvas.translate(baseTopLeft.x, baseTopLeft.y)

                    textLayoutResult.multiParagraph.paint(
                        canvas = canvas,
                        color = settings.stroke.colorArgb.toComposeColor(),
                        drawStyle = Stroke(
                            width = scaledStrokeWidthPx * 2f,
                            join = StrokeJoin.Round
                        )
                    )
                    canvas.restore()
                }
            }

            if (mainBrush != null) {
                drawText(
                    textLayoutResult = textLayoutResult,
                    brush = mainBrush,
                    topLeft = baseTopLeft,
                    drawStyle = Fill,
                    shadow = Shadow.None
                )
            } else {
                drawText(
                    textLayoutResult = textLayoutResult,
                    color = settings.textStyle.colorArgb.toComposeColor(),
                    topLeft = baseTopLeft,
                    drawStyle = Fill,
                    shadow = Shadow.None
                )
            }
        }
    }
}

private fun String.toWordSpacedAnnotatedString(wordSpacingSp: Float): AnnotatedString {
    if (wordSpacingSp <= 0f || !this.contains(' ')) {
        return AnnotatedString(this)
    }

    val spaceSpanStyle = SpanStyle(letterSpacing = wordSpacingSp.sp)

    return buildAnnotatedString {
        for (i in indices) {
            val char = this@toWordSpacedAnnotatedString[i]
            if (char == ' ') {
                val start = length
                append(char)
                addStyle(style = spaceSpanStyle, start = start, end = length)
            } else {
                append(char)
            }
        }
    }
}