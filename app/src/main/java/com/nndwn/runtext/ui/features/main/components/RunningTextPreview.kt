package com.nndwn.runtext.ui.features.main.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.data.model.TextColorType
import com.nndwn.runtext.ui.theme.toComposeColor
import com.nndwn.runtext.ui.utils.fontFamilyFor
import kotlinx.coroutines.isActive
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.text.ifEmpty
import kotlin.text.isEmpty

@Composable
fun RunningTextPreview(settings: AppSettings) {
    val rawText = settings.lastText.ifEmpty { "PREVIEW" }
    val textMeasurer = rememberTextMeasurer()

    val fontFamily = fontFamilyFor(settings.textStyle.fontType)
    val fontWeight = FontWeight.Normal

    val annotatedText = remember (rawText, settings.textStyle.wordSpacingSp){
        rawText.toWordSpacedAnnotatedString(settings.textStyle.wordSpacingSp)
    }

    // RTL detection for consistency
    val isRtl = remember(rawText) {
        if (rawText.isEmpty()) false
        else {
            val bidi = java.text.Bidi(
                rawText,
                java.text.Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT,
            )
            bidi.isRightToLeft
        }
    }

    val textShadow = if (settings.shadow.isEnabled){
        val angleInRadians = Math.toRadians(settings.shadow.rotation.toDouble())
        val offsetX = (settings.shadow.distance * cos(angleInRadians)).toFloat()
        val offsetY = (settings.shadow.distance * sin(angleInRadians)).toFloat()

        Shadow(
            color = settings.shadow.colorArgb.toComposeColor(),
            offset = Offset(x = offsetX, y = offsetY),
            blurRadius = settings.shadow.radius
        )
    } else {
        Shadow.None
    }
    val baseTextStyle = remember(
        fontFamily,
        fontWeight,
        settings.textStyle.letterSpacingSp,
        textShadow
    ){
        TextStyle(
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontSize = 40.sp,
            letterSpacing = settings.textStyle.letterSpacingSp.sp,
            shadow = textShadow
        )
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        val containerWidthPx = constraints.maxWidth.toFloat()
        val initialLayoutResult = remember(annotatedText, baseTextStyle) {
            textMeasurer.measure(
                text = annotatedText,
                style = baseTextStyle,
                maxLines = 1,
                softWrap = false,
            )
        }
        val textWidth = initialLayoutResult.size.width.toFloat().coerceAtLeast(1f)
        val textHeight = initialLayoutResult.size.height.toFloat().coerceAtLeast(1f)


        val textStyle = if (settings.textStyle.colorType == TextColorType.GRADIENT) {
            val color1 = settings.textStyle.gradientColorsArgb.getOrElse(0) { settings.textStyle.colorArgb }.toComposeColor()
            val color2 = settings.textStyle.gradientColorsArgb.getOrElse(1) { settings.textStyle.colorArgb }.toComposeColor()

            val dist = settings.textStyle.gradientDistance.coerceIn(0f, 1f)


            val startOffset: Offset
            val endOffset: Offset

            if (settings.textStyle.isGradientHorizontal) {
                val shift = (dist - 0.5f) * 2f * textWidth
                startOffset = Offset(0f + shift, 0f)
                endOffset = Offset(textWidth + shift, 0f)
            } else {
                val shift = (dist - 0.5f) * 2f * textHeight
                startOffset = Offset(0f, 0f + shift)
                endOffset = Offset(0f, textHeight + shift)
            }

            baseTextStyle.copy(
                brush = Brush.linearGradient(
                    colors = listOf(color1, color2),
                    start = startOffset,
                    end = endOffset
                )
            )
        } else {
            baseTextStyle.copy(
                color = settings.textStyle.colorArgb.toComposeColor()
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

        Box(
            modifier = Modifier
                .wrapContentWidth(unbounded = true, align = Alignment.Start)
                .graphicsLayer {
                    translationX = offsetX.value
                    rotationY = if (settings.isMirrorMode) 180f else 0f
                }
        ) {
            if (settings.stroke.isEnabled && settings.stroke.width > 0) {
                Text(
                    text = annotatedText,
                    style = baseTextStyle.copy(
                        color = settings.stroke.colorArgb.toComposeColor(),
                        drawStyle = Stroke(
                            width = settings.stroke.width * 2f,
                            join = StrokeJoin.Round
                        )
                    ),
                    maxLines = 1,
                    softWrap = false
                )
            }
            Text(
                text = annotatedText,
                style = textStyle,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}

private fun String.toWordSpacedAnnotatedString(wordSpacingSp : Float) : AnnotatedString{
    if (wordSpacingSp <= 0f || !this.contains(" ")){
        return AnnotatedString(this)
    }
    return buildAnnotatedString {
        for (char in this@toWordSpacedAnnotatedString){
            if (char == ' '){
                withStyle(style = SpanStyle(letterSpacing = wordSpacingSp.sp)){
                    append(char)
                }
            }else {
                append(char)
            }
        }
    }
}