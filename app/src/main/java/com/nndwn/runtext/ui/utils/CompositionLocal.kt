package com.nndwn.runtext.ui.utils

import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.compositionLocalOf



val LocalIsTablet = compositionLocalOf { false }

val LocalWindowHeightSize = compositionLocalOf { WindowHeightSizeClass.Compact }
val LocalWindowWidthSize = compositionLocalOf { WindowWidthSizeClass.Compact }

