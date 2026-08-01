package com.nndwn.runtext.ui.utils

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.compositionLocalOf

val LocalIsTablet = compositionLocalOf { false }

val LocalWindowWidthSize = compositionLocalOf { WindowWidthSizeClass.Compact }