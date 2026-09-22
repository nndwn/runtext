package com.nndwn.runtext.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.nndwn.runtext.data.model.FontData
import com.nndwn.runtext.data.model.TextColorType
import com.nndwn.runtext.data.model.TextConfig
import com.nndwn.runtext.ui.LocalSizeHeight
import com.nndwn.runtext.ui.LocalSizeWidth
import com.nndwn.runtext.ui.theme.toComposeColor
import com.nndwn.runtext.ui.utils.fontFamilyFor
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.isActive

@Composable
fun RunningTextRenderer(
  modifier: Modifier = Modifier,
  text: String = "PREVIEW",
  settings: TextConfig,
  fonts: List<FontData>,
  editor: Boolean = false,
) {
  val textMeasurer = rememberTextMeasurer()
  val density = LocalDensity.current
  val distanceShadow = 4f
  val context = LocalContext.current
  val localSizeWidth = LocalSizeWidth.current
  val localSizeHeight = LocalSizeHeight.current

  val rawText =
    remember(text) {
      text.ifEmpty { "PREVIEW" }
    }

  val isRtl =
    remember(rawText) { java.text.Bidi(rawText, java.text.Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT).isRightToLeft }
  val fontResolver = LocalFontFamilyResolver.current

  val currentFont =
    remember(settings.textStyle.fontId, fonts) {
      fonts.find { it.idFont == settings.textStyle.fontId }
    }

  val fontFamily = currentFont?.let { fontFamilyFor(context, it) } ?: FontFamily.Default

  val fontLoadState by
    produceState(
      initialValue = fontResolver.resolve(fontFamily).value,
      key1 = fontFamily,
    ) {
      snapshotFlow { fontResolver.resolve(fontFamily).value }.collect { value = it }
    }

  BoxWithConstraints(
    modifier = modifier.fillMaxSize().background(if (editor) Color.Transparent else settings.bgColorArgb.toComposeColor()),
    contentAlignment = Alignment.CenterStart,
  ) {
    val containerHeightPx = constraints.maxHeight.toFloat()
    val containerWidthPx = constraints.maxWidth.toFloat()

    val dynamicFontSizeSp =
      remember(containerHeightPx, density) {
        with(density) {
          (containerHeightPx * 0.55f).toSp().value.coerceIn(14f, 120f).sp
        }
      }

    val baseTextStyle =
      remember(fontFamily) {
        TextStyle(
          fontFamily = fontFamily,
          fontWeight = FontWeight.Normal,
          fontSize = dynamicFontSizeSp,
        )
      }

    val textLayoutResult =
      remember(baseTextStyle, baseTextStyle, fontLoadState, rawText) {
        textMeasurer.measure(
          text = rawText,
          style = baseTextStyle,
          maxLines = 1,
          softWrap = false,
        )
      }

    val extraPaddingPx =
      remember(settings.shadow, settings.stroke, density) {
        with(density) {
          val strokePadding = if (settings.stroke.isEnabled) settings.stroke.width else 0f
          val shadowPadding = if (settings.shadow.isEnabled) (distanceShadow + settings.shadow.radius) else 0f
          (maxOf(strokePadding, shadowPadding) + 5f).dp.toPx()
        }
      }

    val mainBrush =
      remember(settings.textStyle, textLayoutResult) {
        if (settings.textStyle.colorType == TextColorType.GRADIENT) {
          val color1 =
            settings.textStyle.gradientColorsArgb.getOrElse(0) { settings.textStyle.colorArgb }.toComposeColor()
          val color2 =
            settings.textStyle.gradientColorsArgb.getOrElse(1) { settings.textStyle.colorArgb }.toComposeColor()
          val dist = settings.textStyle.gradientDistance.coerceIn(0f, 1f)

          val textWidth = textLayoutResult.size.width.toFloat()
          val textHeight = textLayoutResult.size.height.toFloat()

          val (startOffset, endOffset) =
            if (settings.textStyle.isGradientHorizontal) {
              val shift = (dist - 0.5f) * 2f * textWidth
              Offset(shift, 0f) to Offset(textWidth + shift, 0f)
            } else {
              val shift = (dist - 0.5f) * 2f * textHeight
              Offset(0f, shift) to Offset(0f, textHeight + shift)
            }

          Brush.linearGradient(
            colors = listOf(color1, color2),
            start = startOffset,
            end = endOffset,
          )
        } else null
      }

    val isVertical = ( localSizeHeight == WindowHeightSizeClass.Compact || localSizeHeight == WindowHeightSizeClass.Medium )
            && !editor
    val totalTextWidth = textLayoutResult.size.width.toFloat() + (extraPaddingPx * 2)
    val displayContainerDim = if (isVertical) containerHeightPx else containerWidthPx

    val (startX, endX) =
      remember(
        displayContainerDim,
        totalTextWidth,
        isRtl,
        settings.isMirrorMode,
        isVertical
      ) {
        val halfText = totalTextWidth / 2f
        val moveRightToLeft = !isRtl
        val effectiveMoveRightToLeft = if (settings.isMirrorMode) !moveRightToLeft else moveRightToLeft
        
        val startPos = displayContainerDim + halfText
        val endPos = -halfText
        
        if (effectiveMoveRightToLeft) {
          startPos to endPos
        } else {
          endPos to startPos
        }
      }

    val durationMillis =
      remember(settings.speed, totalTextWidth, displayContainerDim) {
        val dist = abs(endX - startX)
        val speedFactor = settings.speed.coerceAtLeast(1f)
        val baseDurationSeconds = (dist / displayContainerDim) * (1000f / speedFactor) 
        (baseDurationSeconds * 1000).toInt().coerceAtLeast(200)
      }

    val animatedOffsetX: Float =
      if (editor) {
        val progress = remember { Animatable(0f) }

        LaunchedEffect(durationMillis) {
          while (isActive) {
            val remainingRatio = (1f - progress.value).coerceIn(0f, 1f)
            val adjustedDuration = (durationMillis * remainingRatio).toInt().coerceAtLeast(1)

            progress.animateTo(
              targetValue = 1f,
              animationSpec =
                tween(
                  durationMillis = adjustedDuration,
                  easing = LinearEasing,
                ),
            )

            if (progress.value >= 1f) {
              progress.snapTo(0f)
            }
          }
        }

        lerp(startX, endX, progress.value)
      } else {
        val transition = rememberInfiniteTransition(label = "marquee")
        val offset by
          transition.animateFloat(
            initialValue = startX,
            targetValue = endX,
            animationSpec =
              infiniteRepeatable(
                animation = tween(durationMillis = durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
              ),
            label = "offsetX",
          )
        offset
      }

    val blinkAlpha by
    if (settings.isBlink) {
      val transition = rememberInfiniteTransition(label = "blink")
      transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec =
          infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = { if (it < 0.5f) 0f else 1f }),
            repeatMode = RepeatMode.Restart,
          ),
        label = "blinkAlpha",
      )
    } else {
      remember { mutableFloatStateOf(1f) }
    }


    Canvas(
      modifier =
        Modifier.fillMaxSize().graphicsLayer {
          alpha = blinkAlpha
          val textWidth = textLayoutResult.size.width.toFloat()
          val textHeight = textLayoutResult.size.height.toFloat()
          
          val currentCenterX = extraPaddingPx + (textWidth / 2f)
          val currentCenterY = (size.height - textHeight) / 2f + (textHeight / 2f)

          if (isVertical) {
            translationY = animatedOffsetX - currentCenterY
            translationX = (containerWidthPx / 2f) - currentCenterX
          } else {
            translationX = animatedOffsetX - currentCenterX
          }
          clip = false
        }
    ) {
      val topOffsetY = (size.height - textLayoutResult.size.height) / 2f
      val baseTopLeft = Offset(extraPaddingPx, topOffsetY)

      val drawContent: DrawScope.() -> Unit = {
        if (settings.shadow.isEnabled) {
          val angleInRadians = Math.toRadians(settings.shadow.rotation.toDouble())
          val baseDistancePx = distanceShadow.dp.toPx()
          val strokeOffsetPx =
            if (settings.stroke.isEnabled && settings.stroke.width > 0) {
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
            shadow =
              Shadow(
                color = settings.shadow.colorArgb.toComposeColor(),
                offset = Offset(shadowOffsetX, shadowOffsetY),
                blurRadius = settings.shadow.radius,
              ),
          )
        }

        if (settings.stroke.isEnabled && settings.stroke.width > 0) {
          val scaledStrokeWidthPx = settings.stroke.width.dp.toPx()

          drawText(
            textLayoutResult = textLayoutResult,
            color = settings.stroke.colorArgb.toComposeColor(),
            topLeft = baseTopLeft,
            drawStyle =
              Stroke(
                width = scaledStrokeWidthPx * 2f,
                join = StrokeJoin.Round,
              ),
          )
        }

        if (mainBrush != null) {
          drawText(
            textLayoutResult = textLayoutResult,
            brush = mainBrush,
            topLeft = baseTopLeft,
            drawStyle = Fill,
            shadow = Shadow.None,
          )
        } else {
          drawText(
            textLayoutResult = textLayoutResult,
            color = settings.textStyle.colorArgb.toComposeColor(),
            topLeft = baseTopLeft,
            drawStyle = Fill,
            shadow = Shadow.None,
          )
        }
      }

      val textCenterX = baseTopLeft.x + (textLayoutResult.size.width / 2f)
      val textCenterY = baseTopLeft.y + (textLayoutResult.size.height / 2f)
      val pivot = Offset(textCenterX, textCenterY)

      val drawMirroredContent: DrawScope.() -> Unit = {
        if (settings.isMirrorMode) {
          scale(scaleX = -1f, scaleY = 1f, pivot = pivot) {
            drawContent()
          }
        } else {
          drawContent()
        }
      }

      if (isVertical) {
        rotate(degrees = 90F, pivot = pivot) {
          drawMirroredContent()
        }
      } else {
        drawMirroredContent()
      }
    }
  }
}
