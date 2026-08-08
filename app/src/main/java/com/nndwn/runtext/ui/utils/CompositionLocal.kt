package com.nndwn.runtext.ui.utils

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.nndwn.runtext.extentions.WindowSize
import com.nndwn.runtext.ui.component.MenuOptions


val LocalWindowSize = compositionLocalOf { WindowSize.PHONE_PORTRAIT }
val LocalIsPremium = compositionLocalOf { false }

val LocalToggleSidebar = staticCompositionLocalOf<() -> Unit> {
    error("No ToggleSidebar provided")
}

val LocalMenuOptionHandler = staticCompositionLocalOf<(MenuOptions) -> Unit> {
    error("No MenuOptionHandler provided")
}