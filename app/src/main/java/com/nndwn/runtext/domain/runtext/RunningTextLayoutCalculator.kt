package com.nndwn.runtext.domain.runtext

/**
 * Single source of truth for the pure layout & marquee math shared by the
 * Compose renderer ([com.nndwn.runtext.ui.component.RunningTextRenderer]) and the
 * Canvas-based MP4 exporter ([com.nndwn.runtext.helper.CanvasTextRenderer]).
 *
 * These functions are intentionally framework-independent (no Android/Compose imports)
 * so both renderers produce identical decisions. Text *measurement* is still performed
 * by each framework, so minor pixel differences may remain.
 */
object RunningTextLayoutCalculator {

  /** True when the text has no whitespace and therefore must be kept on a single line. */
  fun isSingleWord(text: String): Boolean =
    !text.contains(" ") && !text.contains("\n")

  /**
   * Returns the font size as a fraction of the container height.
   *
   * Mirrors the original sizing rules:
   * - moving text or text that already fits: 0.55
   * - a single word too wide for the container: 0.26 scaled down (min 0.12)
   * - otherwise: 0.26
   */
  fun calculateFontScale(
    isMove: Boolean,
    fitsInSingleLineAtLarge: Boolean,
    isSingleWord: Boolean,
    availableWidth: Float,
    singleWordWidth: Float,
  ): Float {
    if (isMove || fitsInSingleLineAtLarge) return 0.55f

    if (isSingleWord) {
      if (singleWordWidth > availableWidth) {
        val scaleRatio = (availableWidth / singleWordWidth).coerceIn(0.15f, 1f)
        return (0.26f * scaleRatio).coerceAtLeast(0.12f)
      }
      return 0.26f
    }

    return 0.26f
  }

  /** Start/end X positions (in px) for the marquee sweep. */
  data class MarqueeRange(val startX: Float, val endX: Float)

  /**
   * Computes the marquee sweep range, accounting for RTL and mirror mode.
   *
   * @param containerDim the dimension along which the text travels
   *   (width for horizontal, height for vertical).
   */
  fun calculateMarqueeRange(
    containerDim: Float,
    totalTextWidth: Float,
    isRtl: Boolean,
    isMirrorMode: Boolean,
  ): MarqueeRange {
    val halfText = totalTextWidth / 2f
    val moveRightToLeft = !isRtl
    val effectiveMoveRightToLeft = if (isMirrorMode) !moveRightToLeft else moveRightToLeft

    val startPos = containerDim + halfText
    val endPos = -halfText

    return if (effectiveMoveRightToLeft) {
      MarqueeRange(startPos, endPos)
    } else {
      MarqueeRange(endPos, startPos)
    }
  }

  /**
   * Computes the marquee duration in milliseconds.
   *
   * The absolute travel distance is always `containerDim + totalTextWidth` regardless
   * of direction, so it intentionally does not depend on RTL/mirror.
   */
  fun calculateMarqueeDurationMillis(
    containerDim: Float,
    totalTextWidth: Float,
    speed: Float,
  ): Int {
    val dist = containerDim + totalTextWidth
    val speedFactor = speed.coerceAtLeast(1f)
    val baseDurationSeconds = (dist / containerDim) * (1000f / speedFactor)
    return (baseDurationSeconds * 1000).toInt().coerceAtLeast(200)
  }
}
