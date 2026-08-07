package com.nndwn.runtext

import android.content.res.Configuration
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.extentions.WindowSize
import com.nndwn.runtext.ui.component.MainLayout
import com.nndwn.runtext.ui.features.main.MainScreenContent
import com.nndwn.runtext.ui.features.main.MainUiEvent
import com.nndwn.runtext.ui.features.main.MainUiState
import com.nndwn.runtext.ui.navigation.Routes
import com.nndwn.runtext.ui.theme.RuntextTheme
import com.nndwn.runtext.ui.utils.LocalWindowSize


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun InteractivePreviewWrapper(
    windowSize: WindowSize = WindowSize.PHONE_PORTRAIT
) {
    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    var isSidebarOpen by remember { mutableStateOf(false) }
    val sidebarAllowed = isSidebarOpen && currentRoute != Routes.DISPLAY

    var settings by remember {
        mutableStateOf(
            AppSettings(
                lastText = "test preview"
            )
        )
    }

    var noticeMessage by remember { mutableStateOf<Int?>(null) }

    val handleEvent: (MainUiEvent) -> Unit = { event ->
        settings = when (event) {
            // General
            is MainUiEvent.UpdateText -> settings.copy(lastText = event.text)
            is MainUiEvent.ClearText -> settings.copy(lastText = "")
            is MainUiEvent.UpdateMode -> settings.copy(mode = event.mode)
            is MainUiEvent.UpdateSpeed -> settings.copy(speed = event.speed)
            is MainUiEvent.UpdateBgColor -> settings.copy(bgColorArgb = event.colorArgb)
            is MainUiEvent.UpdateMirrorMode -> settings.copy(isMirrorMode = !settings.isMirrorMode)

            // Text Style
            is MainUiEvent.UpdateTextColor -> settings.copy(
                textStyle = settings.textStyle.copy(colorArgb = event.colorArgb)
            )

            is MainUiEvent.UpdateTextColorType -> settings.copy(
                textStyle = settings.textStyle.copy(colorType = event.type)
            )

            is MainUiEvent.UpdateGradientColors -> settings.copy(
                textStyle = settings.textStyle.copy(gradientColorsArgb = event.colors)
            )

            is MainUiEvent.UpdateGradientDistance -> settings.copy(
                textStyle = settings.textStyle.copy(gradientDistance = event.distance)
            )

            is MainUiEvent.ToggleGradientHorizontal -> settings.copy(
                textStyle = settings.textStyle.copy(isGradientHorizontal = event.isHorizontal)
            )

            is MainUiEvent.UpdateFontType -> settings.copy(
                textStyle = settings.textStyle.copy(fontType = event.fontType)
            )

            is MainUiEvent.UpdateGoogleFontName -> settings.copy(
                textStyle = settings.textStyle.copy(googleFontName = event.fontName)
            )

            is MainUiEvent.UpdateLetterSpacing -> settings.copy(
                textStyle = settings.textStyle.copy(letterSpacingSp = event.spacingSp)
            )

            is MainUiEvent.UpdateWordSpacing -> settings.copy(
                textStyle = settings.textStyle.copy(wordSpacingSp = event.spacingSp)
            )

            // Stroke
            is MainUiEvent.ToggleStroke -> settings.copy(
                stroke = settings.stroke.copy(isEnabled = event.isEnabled)
            )

            is MainUiEvent.UpdateStrokeWidth -> settings.copy(
                stroke = settings.stroke.copy(width = event.width)
            )

            is MainUiEvent.UpdateStrokeColor -> settings.copy(
                stroke = settings.stroke.copy(colorArgb = event.colorArgb)
            )

            // Shadow
            is MainUiEvent.ToggleShadow -> settings.copy(
                shadow = settings.shadow.copy(isEnabled = event.isEnabled)
            )

            is MainUiEvent.UpdateShadowColor -> settings.copy(
                shadow = settings.shadow.copy(colorArgb = event.colorArgb)
            )

            is MainUiEvent.UpdateShadowRadius -> settings.copy(
                shadow = settings.shadow.copy(radius = event.radius)
            )


            is MainUiEvent.UpdateShadowRotation -> settings.copy(
                shadow = settings.shadow.copy(rotation = event.rotation)
            )

            is MainUiEvent.ApplyPreset -> event.settings.copy(
                lastText = settings.lastText
            )

            is MainUiEvent.UpdateMorseWpm -> settings.copy(
                morseConfig = settings.morseConfig.copy(morseWpm = event.wpm)
            )

            is MainUiEvent.UpdateBgColorMorse -> settings.copy(
                morseConfig = settings.morseConfig.copy(bgColorMorse = event.colorArgb)
            )

            is MainUiEvent.UpdateFlashScreen -> settings.copy(
                morseConfig = settings.morseConfig.copy(isFlashScreen = event.isFlashScreen)
            )

            is MainUiEvent.UpdateTorchEnabled -> settings.copy(
                morseConfig = settings.morseConfig.copy(isTorchEnabled = event.isTorchEnabled)
            )

            is MainUiEvent.UpdateSoundEnabled -> settings.copy(
                morseConfig = settings.morseConfig.copy(isSoundEnabled = event.isSoundEnabled)
            )

            is MainUiEvent.UpdateVibrateEnabled -> settings.copy(
                morseConfig = settings.morseConfig.copy(isVibrateEnabled = event.isVibrateEnabled)
            )

            is MainUiEvent.NavigateToDisplay -> navController.navigate(Routes.DISPLAY)
            is MainUiEvent.NavigateBack -> navController.popBackStack()
            is MainUiEvent.Toast -> noticeMessage = event.message
        } as AppSettings
    }
    CompositionLocalProvider(
        LocalWindowSize provides windowSize,
    ) {
        RuntextTheme {
            MainLayout(
                isSidebarOpen = sidebarAllowed,
                onCloseSidebar = { isSidebarOpen = false },
                sideBarRight = { }) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Routes.INPUT
                ) {
                    composable(Routes.INPUT) {
                        MainScreenContent(
                            uiState = MainUiState.Success(settings),
                            onEvent = handleEvent,
                            sideBarEnd = { isSidebarOpen = true },
                            padding = innerPadding
                        )
                    }
                    composable(Routes.DISPLAY) {
                        // Display Screen
                    }
                }
            }
        }

    }
}

//@Preview
//@Composable
//private fun PreviewPhone(){
//    InteractivePreviewWrapper(false)
//}

//@Preview(device = "spec:width=1080px,height=2340px,dpi=480")
//@Composable
//private fun PreviewTabletLight(){
//    InteractivePreviewWrapper(false)
//}
@Preview(
    device = "spec:width=1080px,height=2340px,dpi=480,orientation=landscape",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewTabletDark() {
    InteractivePreviewWrapper(
        windowSize = WindowSize.PHONE_LANDSCAPE
    )
}
