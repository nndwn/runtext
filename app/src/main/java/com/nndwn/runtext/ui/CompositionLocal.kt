package com.nndwn.runtext.ui

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.nndwn.runtext.ui.component.MenuOptions


val LocalSizeHeight = compositionLocalOf {
    WindowHeightSizeClass.Compact
}

val LocalSizeWidth = compositionLocalOf {
    WindowWidthSizeClass.Compact
}
val LocalIsPremium = compositionLocalOf { false }

val LocalToggleSidebar = staticCompositionLocalOf<() -> Unit> {
    error("No ToggleSidebar provided")
}



val LocalMenuOptionHandler = staticCompositionLocalOf<(MenuOptions) -> Unit> {
    error("No MenuOptionHandler provided")
}