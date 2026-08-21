package com.nndwn.runtext

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.nndwn.runtext.ui.RunTextApp
import com.nndwn.runtext.ui.theme.RuntextTheme
import com.nndwn.runtext.utils.DeviceSpecsLogger
import com.nndwn.runtext.ui.LocalSizeHeight
import com.nndwn.runtext.ui.LocalSizeWidth

abstract class BaseMainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        onInitialize()

        enableEdgeToEdge()

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val context = LocalContext.current
            if (BuildConfig.DEBUG) {
                LaunchedEffect(Unit) {
                    DeviceSpecsLogger.logSpecs(
                        context = context,
                        windowSizeClass = windowSizeClass
                    )
                }
            }

            CompositionLocalProvider(
                LocalSizeWidth provides windowSizeClass.widthSizeClass,
                LocalSizeHeight provides windowSizeClass.heightSizeClass
            ) {
                RuntextTheme {
                    RunTextApp()
                }
            }
        }
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
    }


    open fun onInitialize() {
        //     * Hook for subclasses to perform additional initialization before the UI is set.
        //     * This is intentionally empty in the base class to allow flavor-specific
        //     * implementations (e.g., initializing ads or app updates in the Play Store flavor)
        //     * without cluttering the base logic or FOSS flavor.
    }
}
