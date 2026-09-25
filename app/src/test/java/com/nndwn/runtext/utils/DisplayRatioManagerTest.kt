package com.nndwn.runtext.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DisplayRatioManagerTest {

    @Before
    fun setUp() {
        DisplayRatioManager.resetForTesting()
    }

    @Test
    fun `test initialization in portrait forces landscape ratio`() {
        // Portrait dimensions: width = 1080, height = 2400
        DisplayRatioManager.init(1080f, 2400f)

        assertTrue(DisplayRatioManager.isInitialized())
        // Landscape ratio should be 2400 / 1080 = 2.2222...
        val expectedRatio = 2400f / 1080f
        assertEquals(expectedRatio, DisplayRatioManager.ratio, 0.001f)

        // Landscape dimensions stored
        assertEquals(2400f, DisplayRatioManager.width, 0.001f)
        assertEquals(1080f, DisplayRatioManager.height, 0.001f)
    }

    @Test
    fun `test initialization in landscape keeps landscape ratio`() {
        // Landscape dimensions: width = 1920, height = 1080
        DisplayRatioManager.init(1920f, 1080f)

        val expectedRatio = 1920f / 1080f
        assertEquals(expectedRatio, DisplayRatioManager.ratio, 0.001f)
        assertEquals(1920f, DisplayRatioManager.width, 0.001f)
        assertEquals(1080f, DisplayRatioManager.height, 0.001f)
    }

    @Test
    fun `test value persists once initialized`() {
        DisplayRatioManager.init(1920f, 1080f)
        // Subsequent init call should be ignored
        DisplayRatioManager.init(1080f, 2400f)

        assertEquals(1920f / 1080f, DisplayRatioManager.ratio, 0.001f)
        assertEquals(1920f, DisplayRatioManager.width, 0.001f)
    }

    @Test
    fun `test updateHeight recalculates width based on ratio`() {
        // Init with 16:9 ratio (1600 x 900)
        DisplayRatioManager.init(1600f, 900f)

        // Set new height to 180
        val newWidth = DisplayRatioManager.updateHeight(180f)

        assertEquals(180f, DisplayRatioManager.height, 0.001f)
        // 180 * (16/9) = 320
        assertEquals(320f, DisplayRatioManager.width, 0.001f)
        assertEquals(320f, newWidth, 0.001f)
    }

    @Test
    fun `test updateWidth recalculates height based on ratio`() {
        // Init with 16:9 ratio (1600 x 900)
        DisplayRatioManager.init(1600f, 900f)

        // Set new width to 320
        val newHeight = DisplayRatioManager.updateWidth(320f)

        assertEquals(320f, DisplayRatioManager.width, 0.001f)
        // 320 / (16/9) = 180
        assertEquals(180f, DisplayRatioManager.height, 0.001f)
        assertEquals(180f, newHeight, 0.001f)
    }

    @Test
    fun `test getWidthForHeight and getHeightForWidth calculations`() {
        DisplayRatioManager.init(1920f, 1080f)

        val targetHeight = 540f
        val calculatedWidth = DisplayRatioManager.getWidthForHeight(targetHeight)
        assertEquals(960f, calculatedWidth, 0.001f)

        val targetWidth = 960f
        val calculatedHeight = DisplayRatioManager.getHeightForWidth(targetWidth)
        assertEquals(540f, calculatedHeight, 0.001f)
    }

    @Test
    fun `test square screen dimensions`() {
        DisplayRatioManager.init(1000f, 1000f)
        assertEquals(1.0f, DisplayRatioManager.ratio, 0.001f)
        assertEquals(1000f, DisplayRatioManager.width, 0.001f)
        assertEquals(1000f, DisplayRatioManager.height, 0.001f)
    }

    @Test
    fun `test zero or negative initial dimensions ignored`() {
        DisplayRatioManager.init(0f, -100f)
        assertTrue(!DisplayRatioManager.isInitialized())

        // Default ratio remains 16:9
        assertEquals(16f / 9f, DisplayRatioManager.ratio, 0.001f)
    }

    @Test
    fun `test negative input in updateHeight and updateWidth coerced to zero`() {
        DisplayRatioManager.init(1600f, 900f)

        DisplayRatioManager.updateHeight(-50f)
        assertEquals(0f, DisplayRatioManager.height, 0.001f)
        assertEquals(0f, DisplayRatioManager.width, 0.001f)

        DisplayRatioManager.updateWidth(-100f)
        assertEquals(0f, DisplayRatioManager.width, 0.001f)
        assertEquals(0f, DisplayRatioManager.height, 0.001f)
    }
}
