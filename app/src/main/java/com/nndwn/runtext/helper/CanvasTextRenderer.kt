package com.nndwn.runtext.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.createFontFamilyResolver
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.data.model.FontData
import com.nndwn.runtext.data.model.TextColorType
import com.nndwn.runtext.data.model.TextConfig
import com.nndwn.runtext.data.model.TextStyleConfig
import com.nndwn.runtext.domain.morse.MorseElement
import com.nndwn.runtext.domain.morse.MorseEngine
import com.nndwn.runtext.domain.runtext.RunningTextLayoutCalculator
import com.nndwn.runtext.ui.utils.fontFamilyFor
import java.text.Bidi
import kotlin.math.cos
import kotlin.math.sin
import androidx.core.graphics.withTranslation

object CanvasTextRenderer {

  fun drawRunningText(
    canvas: Canvas,
    width: Int,
    height: Int,
    text: String,
    settings: AppSettings,
    progress: Float,
    fonts: List<FontData>,
    context: Context,
    isVertical: Boolean = false,
    paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
  ) {
    val textConfig = settings.textConfig
    val textStyle = textConfig.textStyle

    val fontData = fonts.find { it.idFont == textStyle.fontId }
    val fontFamily = fontData?.let { fontFamilyFor(context, it) } ?: FontFamily.Default
    val fontFamilyResolver:  FontFamily.Resolver = createFontFamilyResolver(context)
    val typeface = (fontFamilyResolver.resolve(
      fontFamily = fontFamily,
      fontWeight = FontWeight.Normal,
      fontStyle = FontStyle.Normal,
      fontSynthesis = FontSynthesis.All,
    ).value as? Typeface) ?: Typeface.DEFAULT

    paint.reset()
    paint.isAntiAlias = true
    paint.typeface = typeface

    val density = height / 360f
    val strokeWidthPx = textConfig.stroke.width * density
    val shadowRadiusPx = textConfig.shadow.radius * density
    val distanceShadowPx = 4f * density

    val strokePadding = if (textConfig.stroke.isEnabled) strokeWidthPx else 0f
    val shadowPadding = if (textConfig.shadow.isEnabled) (distanceShadowPx + shadowRadiusPx) else 0f
    val extraPaddingPx = maxOf(strokePadding, shadowPadding) + (5f * density)
    val availableWidthPx = (width - extraPaddingPx * 2).coerceAtLeast(1f)

    val largeFontSizePx = height * 0.55f
    paint.textSize = largeFontSizePx
    val largeTextWidth = paint.measureText(text)
    val fitsInSingleLineAtLarge = largeTextWidth <= availableWidthPx
    val isSingleWord = RunningTextLayoutCalculator.isSingleWord(text)

    val fontScale =
      RunningTextLayoutCalculator.calculateFontScale(
        isMove = textConfig.isMove,
        fitsInSingleLineAtLarge = fitsInSingleLineAtLarge,
        isSingleWord = isSingleWord,
        availableWidth = availableWidthPx,
        singleWordWidth =
          if (!textConfig.isMove && !fitsInSingleLineAtLarge && isSingleWord) {
            val testFontSizePx = height * 0.26f
            paint.textSize = testFontSizePx
            paint.measureText(text)
          } else {
            0f
          },
      )

    paint.textSize = height * fontScale
    paint.color = textStyle.colorArgb.toInt()
    paint.textAlign = Paint.Align.CENTER

    val useSingleLine = textConfig.isMove || fitsInSingleLineAtLarge || isSingleWord

    val textWidth = paint.measureText(text)
    val textBounds = Rect()
    paint.getTextBounds(text, 0, text.length, textBounds)
    val textHeight = textBounds.height().toFloat()

    val isRtl = Bidi(text, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT).isRightToLeft

    val containerDim = (if (isVertical) height else width).toFloat()
    val totalTextWidth = textWidth + extraPaddingPx * 2

    val (startX, endX) = RunningTextLayoutCalculator.calculateMarqueeRange(
      containerDim = containerDim,
      totalTextWidth = totalTextWidth,
      isRtl = isRtl,
      isMirrorMode = textConfig.isMirrorMode,
    )

    val currentX = if (textConfig.isMove) {
      startX + progress * (endX - startX)
    } else {
      width / 2f
    }

    val currentY = (height + textHeight) / 2f - textBounds.bottom

    if (isVertical) {
      canvas.save()
      canvas.rotate(90f, width / 2f, height / 2f)
    }

    if (textConfig.isMirrorMode) {
      canvas.save()
      canvas.scale(-1f, 1f, width / 2f, height / 2f)
    }

    val isBlinkOn = if (textConfig.isBlink) {
      (progress * 10).toInt() % 2 == 0
    } else true

    if (isBlinkOn) {
      if (useSingleLine) {
        drawSingleLine(
          canvas = canvas,
          text = text,
          paint = paint,
          textStyle = textStyle,
          textConfig = textConfig,
          currentX = currentX,
          currentY = currentY,
          textWidth = textWidth,
          textHeight = textHeight,
          strokeWidthPx = strokeWidthPx,
          shadowRadiusPx = shadowRadiusPx,
          distanceShadowPx = distanceShadowPx,
        )
      } else {
        drawWrappedTwoLines(
          canvas = canvas,
          text = text,
          basePaint = paint,
          textStyle = textStyle,
          textConfig = textConfig,
          width = width,
          height = height,
          layoutMaxWidthPx = availableWidthPx.toInt().coerceAtLeast(1),
          strokeWidthPx = strokeWidthPx,
          shadowRadiusPx = shadowRadiusPx,
          distanceShadowPx = distanceShadowPx,
        )
      }
    }

    if (textConfig.isMirrorMode) {
      canvas.restore()
    }

    if (isVertical) {
      canvas.restore()
    }
  }

  /** Original single-line drawing path (shadow -> stroke -> fill), unchanged in behavior. */
  private fun drawSingleLine(
    canvas: Canvas,
    text: String,
    paint: Paint,
    textStyle: TextStyleConfig,
    textConfig: TextConfig,
    currentX: Float,
    currentY: Float,
    textWidth: Float,
    textHeight: Float,
    strokeWidthPx: Float,
    shadowRadiusPx: Float,
    distanceShadowPx: Float,
  ) {

    applyGradientShader(
      paint = paint,
      textStyle = textStyle,
      blockWidth = textWidth,
      blockHeight = textHeight,
      centerX = currentX,
      centerY = currentY,
    )

    if (textConfig.shadow.isEnabled) {
      val strokeOffsetPx = if (textConfig.stroke.isEnabled && textConfig.stroke.width > 0) strokeWidthPx else 0f
      val totalShadowDistancePx = distanceShadowPx + strokeOffsetPx
      val rad = Math.toRadians(textConfig.shadow.rotation.toDouble())
      val dx = (totalShadowDistancePx * cos(rad)).toFloat()
      val dy = (totalShadowDistancePx * sin(rad)).toFloat()

      val shadowPaint = Paint(paint).apply {
        shader = null
        style = Paint.Style.FILL
        color = textConfig.shadow.colorArgb.toInt()
        setShadowLayer(shadowRadiusPx, dx, dy, textConfig.shadow.colorArgb.toInt())
      }
      canvas.drawText(text, currentX, currentY, shadowPaint)
    }

    if (textConfig.stroke.isEnabled && textConfig.stroke.width > 0) {
      val strokePaint = Paint(paint).apply {
        shader = null
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthPx * 2f
        color = textConfig.stroke.colorArgb.toInt()
        strokeJoin = Paint.Join.ROUND
        clearShadowLayer()
      }
      canvas.drawText(text, currentX, currentY, strokePaint)
    }

    paint.style = Paint.Style.FILL
    paint.color = textStyle.colorArgb.toInt()
    paint.clearShadowLayer()
    canvas.drawText(text, currentX, currentY, paint)
  }

  /**
   * [FIX] New: wraps text onto up to 2 lines and ellipsizes anything beyond that,
   * mirroring Compose's `maxLines = 2, overflow = TextOverflow.Ellipsis` path.
   * Draws shadow -> stroke -> fill as separate StaticLayout passes so each keeps
   * its own Paint.Style / color / shadow layer, same layering order as before.
   */
  private fun drawWrappedTwoLines(
    canvas: Canvas,
    text: String,
    basePaint: Paint,
    textStyle: TextStyleConfig,
    textConfig: TextConfig,
    width: Int,
    height: Int,
    layoutMaxWidthPx: Int,
    strokeWidthPx: Float,
    shadowRadiusPx: Float,
    distanceShadowPx: Float,
  ) {
    fun buildLayout(textPaint: TextPaint): StaticLayout {
      return StaticLayout.Builder
        .obtain(text, 0, text.length, textPaint, layoutMaxWidthPx)
        .setAlignment(Layout.Alignment.ALIGN_CENTER)
        .setMaxLines(2)
        .setEllipsize(TextUtils.TruncateAt.END)
        .setEllipsizedWidth(layoutMaxWidthPx)
        .setLineSpacing(0f, 1.05f)
        .setIncludePad(false)
        .build()
    }

    val measurePaint = TextPaint(basePaint).apply {
      shader = null
      style = Paint.Style.FILL
      clearShadowLayer()
      textAlign = Paint.Align.LEFT
    }
    val measureLayout = buildLayout(measurePaint)
    val blockHeight = measureLayout.height.toFloat()
    var blockWidth = 0f
    for (i in 0 until measureLayout.lineCount) {
      blockWidth = maxOf(blockWidth, measureLayout.getLineWidth(i))
    }

    val blockLeft = (width - layoutMaxWidthPx) / 2f
    val blockTop = (height - blockHeight) / 2f
    val centerX = width / 2f
    val centerY = blockTop + blockHeight / 2f

    applyGradientShader(
      paint = basePaint,
      textStyle = textStyle,
      blockWidth = blockWidth,
      blockHeight = blockHeight,
      centerX = centerX,
      centerY = centerY,
    )

    fun drawLayer(textPaint: TextPaint) {
      val layout = buildLayout(textPaint)
      canvas.withTranslation(blockLeft, blockTop) {
          layout.draw(this)
      }
    }

    if (textConfig.shadow.isEnabled) {
      val strokeOffsetPx = if (textConfig.stroke.isEnabled && textConfig.stroke.width > 0) strokeWidthPx else 0f
      val totalShadowDistancePx = distanceShadowPx + strokeOffsetPx
      val rad = Math.toRadians(textConfig.shadow.rotation.toDouble())
      val dx = (totalShadowDistancePx * cos(rad)).toFloat()
      val dy = (totalShadowDistancePx * sin(rad)).toFloat()

      val shadowPaint = TextPaint(basePaint).apply {
        shader = null
        style = Paint.Style.FILL
        color = textConfig.shadow.colorArgb.toInt()
        setShadowLayer(shadowRadiusPx, dx, dy, textConfig.shadow.colorArgb.toInt())
        textAlign = Paint.Align.LEFT
      }
      drawLayer(shadowPaint)
    }

    if (textConfig.stroke.isEnabled && textConfig.stroke.width > 0) {
      val strokePaint = TextPaint(basePaint).apply {
        shader = null
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthPx * 2f
        color = textConfig.stroke.colorArgb.toInt()
        strokeJoin = Paint.Join.ROUND
        clearShadowLayer()
        textAlign = Paint.Align.LEFT
      }
      drawLayer(strokePaint)
    }

    val fillPaint = TextPaint(basePaint).apply {
      style = Paint.Style.FILL
      color = textStyle.colorArgb.toInt()
      clearShadowLayer()
      textAlign = Paint.Align.LEFT
    }
    drawLayer(fillPaint)
  }

  /** Shared gradient shader setup, parameterized by the drawn block's size/center. */
  private fun applyGradientShader(
    paint: Paint,
    textStyle: TextStyleConfig,
    blockWidth: Float,
    blockHeight: Float,
    centerX: Float,
    centerY: Float,
  ) {
    if (textStyle.colorType == TextColorType.GRADIENT && textStyle.gradientColorsArgb.isNotEmpty()) {
      val color1 = textStyle.gradientColorsArgb.getOrElse(0) { textStyle.colorArgb }.toInt()
      val color2 = textStyle.gradientColorsArgb.getOrElse(1) { textStyle.colorArgb }.toInt()
      val dist = textStyle.gradientDistance.coerceIn(0f, 1f)

      val (x0, y0, x1, y1) = if (textStyle.isGradientHorizontal) {
        val shift = (dist - 0.5f) * 2f * blockWidth
        listOf(
          centerX - blockWidth / 2f + shift, centerY,
          centerX + blockWidth / 2f + shift, centerY,
        )
      } else {
        val shift = (dist - 0.5f) * 2f * blockHeight
        listOf(
          centerX, centerY - blockHeight / 2f + shift,
          centerX, centerY + blockHeight / 2f + shift,
        )
      }
      paint.shader = LinearGradient(x0, y0, x1, y1, intArrayOf(color1, color2), null, Shader.TileMode.CLAMP)
    } else {
      paint.shader = null
    }
  }


  fun drawMorseSignal(
    canvas: Canvas,
    elements: List<MorseElement>,
    unitMs: Long,
    timeMs: Long,
    totalMorseTimeMs: Long,
    settings: AppSettings,
  ) {
    if (elements.isEmpty()) return

    val currentCycleTimeMs = timeMs % totalMorseTimeMs
    var elapsed = 0L
    var isSignalOn = false

    for (element in elements) {
      val dur = element.durationMultiplier * unitMs
      if (currentCycleTimeMs >= elapsed && currentCycleTimeMs < elapsed + dur) {
        isSignalOn = MorseEngine.isSignalElement(element)
        break
      }
      elapsed += dur
    }

    val bg = if (isSignalOn && settings.morseConfig.isFlashScreen) {
      settings.morseConfig.bgColorMorse.toInt()
    } else {
      0xFF000000.toInt()
    }
    canvas.drawColor(bg)
  }

  fun calculateMarqueeDurationMillis(
    width: Int,
    height: Int,
    text: String,
    settings: AppSettings,
    paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
  ): Int {
    val textConfig = settings.textConfig
    val density = height / 360f
    paint.textSize = height * 0.55f
    val textWidth = paint.measureText(text)
    val strokeWidthPx = textConfig.stroke.width * density
    val shadowRadiusPx = textConfig.shadow.radius * density
    val distanceShadowPx = 4f * density

    val strokePadding = if (textConfig.stroke.isEnabled) strokeWidthPx else 0f
    val shadowPadding = if (textConfig.shadow.isEnabled) (distanceShadowPx + shadowRadiusPx) else 0f
    val extraPaddingPx = maxOf(strokePadding, shadowPadding) + (5f * density)

    val totalTextWidth = textWidth + extraPaddingPx * 2
    return RunningTextLayoutCalculator.calculateMarqueeDurationMillis(
      containerDim = width.toFloat(),
      totalTextWidth = totalTextWidth,
      speed = settings.textConfig.speed,
    )
  }
}
