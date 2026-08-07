package com.nndwn.runtext

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nndwn.runtext.extentions.toCustomWindowSize
import com.nndwn.runtext.ui.RunTextApp
import com.nndwn.runtext.ui.theme.RuntextTheme
import com.nndwn.runtext.ui.utils.DeviceSpecsLogger
import com.nndwn.runtext.ui.utils.LocalWindowSize
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 35) {
            enableEdgeToEdge()
        } else {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
                navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
            )
        }

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val context = LocalContext.current
            val customWindowSize = windowSizeClass.toCustomWindowSize()
            if (BuildConfig.DEBUG) {
                LaunchedEffect(Unit) {
                    DeviceSpecsLogger.logSpecs(
                        context = context,
                        windowSizeClass = windowSizeClass
                    )
                }
            }

            CompositionLocalProvider(
                LocalWindowSize provides customWindowSize,
            ) {
                RuntextTheme {
                    RunTextApp()
                }
            }
        }
    }
}