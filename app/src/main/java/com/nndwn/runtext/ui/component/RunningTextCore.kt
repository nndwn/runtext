package com.nndwn.runtext.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.data.model.TextColorType
import com.nndwn.runtext.ui.theme.toComposeColor
import com.nndwn.runtext.ui.utils.fontFamilyFor
import kotlinx.coroutines.isActive
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin



@Composable
fun RunningTextCoreOptimized(
    settings: AppSettings,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 174.sp,
    defaultPreviewText: String = "PREVIEW"
) {
    val rawText = settings.lastText.ifEmpty { defaultPreviewText }
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current

    // 1. Hitung Scale Factor berdasarkan font size dasar (40.sp)
    val baseFontSizeSp = 40f
    val scaleFactor = (fontSize.value / baseFontSizeSp).coerceAtLeast(0.1f)

    // 2. Deteksi teks RTL
    val isRtl = remember(rawText) {
        if (rawText.isEmpty()) false
        else {
            val bidi = java.text.Bidi(
                rawText,
                java.text.Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT
            )
            bidi.isRightToLeft
        }
    }

    // 3. TextStyle dasar
    val fontFamily = fontFamilyFor(settings.textStyle.fontType)
    val baseTextStyle = remember(fontFamily, fontSize, settings.textStyle.letterSpacingSp) {
        TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = fontSize,
            letterSpacing = settings.textStyle.letterSpacingSp.sp
        )
    }

    val annotatedText = remember(rawText, settings.textStyle.wordSpacingSp) {
        rawText.toWordSpacedAnnotatedString(settings.textStyle.wordSpacingSp)
    }

    // 4. Measure awal untuk kalkulasi dimensi
    val layoutResult = remember(annotatedText, baseTextStyle) {
        textMeasurer.measure(
            text = annotatedText,
            style = baseTextStyle,
            maxLines = 1,
            softWrap = false
        )
    }

    // 5. Hitung ekstra padding dinamis agar Shadow & Stroke tebal tidak terpotong
    val extraPaddingPx = remember(settings.shadow, settings.stroke, scaleFactor, density) {
        with(density) {
            val strokePadding = if (settings.stroke.isEnabled) settings.stroke.width * scaleFactor else 0f
            val shadowPadding = if (settings.shadow.isEnabled) (settings.shadow.distance + settings.shadow.radius) * scaleFactor else 0f
            (maxOf(strokePadding, shadowPadding) + 20f).dp.toPx()
        }
    }

    val bitmapWidth = (layoutResult.size.width + extraPaddingPx * 2).toInt().coerceAtLeast(1)
    val bitmapHeight = (layoutResult.size.height + extraPaddingPx * 2).toInt().coerceAtLeast(1)

    // 6. CACHE: Generasi ImageBitmap (Dijalankan HANYA jika setting/teks berubah)
    val textBitmap = remember(annotatedText, settings, baseTextStyle, bitmapWidth, bitmapHeight, scaleFactor, density) {
        val bitmap = ImageBitmap(bitmapWidth, bitmapHeight)
        val canvas = Canvas(bitmap)
        val drawScope = CanvasDrawScope()
        val size = Size(bitmapWidth.toFloat(), bitmapHeight.toFloat())

        drawScope.draw(
            density = density,
            layoutDirection = LayoutDirection.Ltr,
            canvas = canvas,
            size = size
        ) {
            val topLeftOffset = Offset(extraPaddingPx, extraPaddingPx)

            // 1. Draw Shadow (Paling Bawah)
            if (settings.shadow.isEnabled) {
                val angleInRadians = Math.toRadians(settings.shadow.rotation.toDouble())
                val scaledDistancePx = with(density) { (settings.shadow.distance * scaleFactor).dp.toPx() }
                val scaledRadiusPx = with(density) { (settings.shadow.radius * scaleFactor).dp.toPx() }

                val offsetX = (scaledDistancePx * cos(angleInRadians)).toFloat()
                val offsetY = (scaledDistancePx * sin(angleInRadians)).toFloat()

                val shadowStyle = baseTextStyle.copy(
                    color = settings.shadow.colorArgb.toComposeColor(),
                    shadow = Shadow(
                        color = settings.shadow.colorArgb.toComposeColor(),
                        offset = Offset(offsetX, offsetY),
                        blurRadius = scaledRadiusPx
                    )
                )
                val shadowLayout = textMeasurer.measure(
                    text = annotatedText,
                    style = shadowStyle,
                    maxLines = 1,
                    softWrap = false
                )
                drawText(textLayoutResult = shadowLayout, topLeft = topLeftOffset)
            }

            // 2. Draw Stroke / Outline (Di Belakang Teks Utama)
            if (settings.stroke.isEnabled && settings.stroke.width > 0) {
                val scaledStrokeWidthPx = with(density) { (settings.stroke.width * scaleFactor).dp.toPx() }

                // PENTING: Paksakan drawStyle menggunakan Stroke secara eksplisit
                val strokeStyle = baseTextStyle.copy(
                    color = settings.stroke.colorArgb.toComposeColor(),
                    drawStyle = Stroke(
                        width = scaledStrokeWidthPx * 2f,
                        join = StrokeJoin.Round
                    )
                )
                val strokeLayout = textMeasurer.measure(
                    text = annotatedText,
                    style = strokeStyle,
                    maxLines = 1,
                    softWrap = false
                )
                drawText(textLayoutResult = strokeLayout, topLeft = topLeftOffset)
            }

            // 3. Draw Main Text / Fill (Di Lapisan Paling Atas)
            // PENTING: Kembalikan drawStyle ke Fill secara eksplisit agar stroke tidak menempel
            val mainTextStyle = if (settings.textStyle.colorType == TextColorType.GRADIENT) {
                val color1 = settings.textStyle.gradientColorsArgb.getOrElse(0) { settings.textStyle.colorArgb }.toComposeColor()
                val color2 = settings.textStyle.gradientColorsArgb.getOrElse(1) { settings.textStyle.colorArgb }.toComposeColor()
                val dist = settings.textStyle.gradientDistance.coerceIn(0f, 1f)

                val textWidth = layoutResult.size.width.toFloat()
                val textHeight = layoutResult.size.height.toFloat()

                val startOffset: Offset
                val endOffset: Offset

                if (settings.textStyle.isGradientHorizontal) {
                    val shift = (dist - 0.5f) * 2f * textWidth
                    startOffset = Offset(shift, 0f)
                    endOffset = Offset(textWidth + shift, 0f)
                } else {
                    val shift = (dist - 0.5f) * 2f * textHeight
                    startOffset = Offset(0f, shift)
                    endOffset = Offset(0f, textHeight + shift)
                }

                baseTextStyle.copy(
                    drawStyle = Fill, // <--- TAMBAHKAN INI
                    brush = Brush.linearGradient(
                        colors = listOf(color1, color2),
                        start = startOffset,
                        end = endOffset
                    )
                )
            } else {
                baseTextStyle.copy(
                    drawStyle = Fill, // <--- TAMBAHKAN INI
                    color = settings.textStyle.colorArgb.toComposeColor()
                )
            }

            val mainLayout = textMeasurer.measure(
                text = annotatedText,
                style = mainTextStyle,
                maxLines = 1,
                softWrap = false
            )
            drawText(textLayoutResult = mainLayout, topLeft = topLeftOffset)
        }

        bitmap
    }

    // 7. ANIMASI: Pindahkan Bitmap dengan graphicsLayer
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        val containerWidthPx = constraints.maxWidth.toFloat()
        val textWidth = textBitmap.width.toFloat()

        val startX = if (isRtl) -textWidth else containerWidthPx
        val endX = if (isRtl) containerWidthPx else -textWidth

        val offsetX = remember(startX) { Animatable(startX) }

        LaunchedEffect(settings.speed, textWidth, containerWidthPx, isRtl) {
            val dist = abs(endX - startX)
            val speedFactor = settings.speed.coerceAtLeast(1f)
            val baseDurationSeconds = (dist / containerWidthPx) * (1000f / speedFactor)
            val dur = (baseDurationSeconds * 1000).toInt().coerceAtLeast(200)

            offsetX.snapTo(startX)
            while (isActive) {
                offsetX.animateTo(
                    targetValue = endX,
                    animationSpec = tween(durationMillis = dur, easing = LinearEasing)
                )
                offsetX.snapTo(startX)
            }
        }

        Image(
            bitmap = textBitmap,
            contentDescription = null,
            modifier = Modifier
                .wrapContentWidth(unbounded = true, align = Alignment.Start)
                .graphicsLayer {
                    translationX = offsetX.value
                    rotationY = if (settings.isMirrorMode) 180f else 0f
                    shadowElevation = 0f
                    clip = false
                }
        )
    }
}
private fun String.toWordSpacedAnnotatedString(wordSpacingSp: Float): AnnotatedString {
    if (wordSpacingSp <= 0f || !this.contains(" ")) {
        return AnnotatedString(this)
    }
    return buildAnnotatedString {
        for (char in this@toWordSpacedAnnotatedString) {
            if (char == ' ') {
                withStyle(style = SpanStyle(letterSpacing = wordSpacingSp.sp)) {
                    append(char)
                }
            } else {
                append(char)
            }
        }
    }
}
