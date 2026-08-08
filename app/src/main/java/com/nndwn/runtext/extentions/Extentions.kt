package com.nndwn.runtext.extentions

import android.util.Log
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import com.nndwn.runtext.BuildConfig

private fun CornerSize.shrinkRadius(padding: Dp): CornerSize = object : CornerSize {
    override fun toPx(shapeSize: Size, density: Density): Float {
        val originalRadiusPx = this@shrinkRadius.toPx(shapeSize, density)
        val paddingPx = with(density) { padding.toPx() }
        return (originalRadiusPx - paddingPx).coerceAtLeast(0f)
    }
}

fun CornerBasedShape.shrinkRadius(padding: Dp): CornerBasedShape {
    return this.copy(
        topStart = topStart.shrinkRadius(padding),
        topEnd = topEnd.shrinkRadius(padding),
        bottomEnd = bottomEnd.shrinkRadius(padding),
        bottomStart = bottomStart.shrinkRadius(padding)
    )
}

enum class WindowSize {
    PHONE_PORTRAIT,
    PHONE_LANDSCAPE,
    TABLET_PORTRAIT,
    TABLET_LANDSCAPE,
    FOLDABLE,
    EXPAND
}

fun WindowSizeClass.toCustomWindowSize(): WindowSize {
    if (BuildConfig.DEBUG){
        Log.d("test", "$widthSizeClass $heightSizeClass")
    }
    return when (widthSizeClass) {
        WindowWidthSizeClass.Compact if heightSizeClass == WindowHeightSizeClass.Medium -> WindowSize.PHONE_PORTRAIT
        WindowWidthSizeClass.Medium if heightSizeClass == WindowHeightSizeClass.Compact -> WindowSize.PHONE_LANDSCAPE
        WindowWidthSizeClass.Medium if heightSizeClass == WindowHeightSizeClass.Medium -> WindowSize.FOLDABLE
        WindowWidthSizeClass.Medium if heightSizeClass == WindowHeightSizeClass.Expanded -> WindowSize.TABLET_PORTRAIT
        WindowWidthSizeClass.Expanded if heightSizeClass == WindowHeightSizeClass.Medium -> WindowSize.TABLET_LANDSCAPE
        WindowWidthSizeClass.Expanded if widthSizeClass != WindowWidthSizeClass.Compact -> WindowSize.EXPAND
        else -> WindowSize.EXPAND
    }
}