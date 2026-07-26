package com.nndwn.runtext.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.ui.theme.toComposeColor
import kotlinx.coroutines.isActive
import kotlin.math.abs

@Composable
private fun RunningTextDisplay(settings: AppSettings) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    val fontFamily = fontFamilyFor(settings.fontType, settings.googleFontName)
    val fontWeight = fontWeightFor(settings.fontType)
    val textColor = settings.textColorArgb.toComposeColor()
    val bgColor = settings.bgColorArgb.toComposeColor()

    // RTL detection via java.text.Bidi
    val isRtl = remember(settings.lastText) {
        if (settings.lastText.isEmpty()) false
        else {
            val bidi = java.text.Bidi(
                settings.lastText,
                java.text.Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT,
            )
            bidi.isRightToLeft
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .then(if (settings.isMirrorMode) Modifier.graphicsLayer(scaleX = -1f) else Modifier),
    ) {
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }
        val fontSizeSp = with(density) { (screenHeightPx * 0.55f).toSp() }

        val textStyle = TextStyle(
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontSize = fontSizeSp,
            color = textColor,
        )

        val textLayoutResult = remember(settings.lastText, fontFamily, fontWeight, fontSizeSp) {
            textMeasurer.measure(
                text = settings.lastText,
                style = textStyle,
                maxLines = 1,
                softWrap = false,
            )
        }

        val textWidth = textLayoutResult.size.width.toFloat()
        val textHeight = textLayoutResult.size.height.toFloat()
        val yOffset = (screenHeightPx - textHeight) / 2f

        val startX = if (isRtl) -textWidth else screenWidthPx
        val offsetX = remember { Animatable(startX) }

        LaunchedEffect(settings.speed, textWidth, screenWidthPx, isRtl) {
            val sX = if (isRtl) -textWidth else screenWidthPx
            val eX = if (isRtl) screenWidthPx else -textWidth
            val dist = abs(eX - sX)
            
            // Recalculate duration whenever speed or distance changes
            val dur = ((dist / settings.speed.coerceAtLeast(1f)) * 1000).toInt().coerceAtLeast(500)

            // Start animation from the current position to avoid jumps if possible,
            // or just restart to keep it simple. Restarting is safer for speed changes.
            offsetX.snapTo(sX)
            while (isActive) {
                offsetX.animateTo(
                    targetValue = eX,
                    animationSpec = tween(durationMillis = dur, easing = LinearEasing),
                )
                offsetX.snapTo(sX)
            }
        }

        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            // Glow layers (outermost → innermost)
            val glowRadii = listOf(40f, 25f, 12f)
            for (radius in glowRadii) {
                drawText(
                    textMeasurer = textMeasurer,
                    text = settings.lastText,
                    topLeft = Offset(offsetX.value, yOffset),
                    style = textStyle.copy(
                        color = textColor.copy(alpha = 0.12f),
                        shadow = Shadow(color = textColor.copy(alpha = 0.35f), blurRadius = radius),
                    ),
                    maxLines = 1,
                    softWrap = false,
                )
            }

            // Main text with subtle glow
            drawText(
                textMeasurer = textMeasurer,
                text = settings.lastText,
                topLeft = Offset(offsetX.value, yOffset),
                style = textStyle.copy(
                    shadow = Shadow(
                        color = textColor,
                        offset = Offset.Zero,
                        blurRadius = 8f,
                    ),
                ),
                maxLines = 1,
                softWrap = false,
            )
        }
    }
}
