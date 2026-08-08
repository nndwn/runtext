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
            is MainUiEvent.UpdateSpeed -> settings.textConfig.copy(speed = event.speed)
            is MainUiEvent.UpdateBgColor -> settings.textConfig.copy(bgColorArgb = event.colorArgb)
            is MainUiEvent.UpdateMirrorMode -> settings.textConfig.copy(isMirrorMode = !settings.textConfig.isMirrorMode)

            // Text Style
            is MainUiEvent.UpdateTextColor -> settings.textConfig.copy(
                textStyle = settings.textConfig.textStyle.copy(colorArgb = event.colorArgb)
            )

            is MainUiEvent.UpdateTextColorType -> settings.textConfig.copy(
                textStyle = settings.textConfig.textStyle.copy(colorType = event.type)
            )

            is MainUiEvent.UpdateGradientColors -> settings.textConfig.copy(
                textStyle = settings.textConfig.textStyle.copy(gradientColorsArgb = event.colors)
            )

            is MainUiEvent.UpdateGradientDistance -> settings.textConfig.copy(
                textStyle = settings.textConfig.textStyle.copy(gradientDistance = event.distance)
            )

            is MainUiEvent.ToggleGradientHorizontal -> settings.textConfig.copy(
                textStyle = settings.textConfig.textStyle.copy(isGradientHorizontal = event.isHorizontal)
            )

            is MainUiEvent.UpdateFontType -> settings.textConfig.copy(
                textStyle = settings.textConfig.textStyle.copy(fontType = event.fontType)
            )

            is MainUiEvent.UpdateGoogleFontName -> settings.textConfig.copy(
                textStyle = settings.textConfig.textStyle.copy(googleFontName = event.fontName)
            )

            is MainUiEvent.UpdateLetterSpacing -> settings.textConfig.copy(
                textStyle = settings.textConfig.textStyle.copy(letterSpacingSp = event.spacingSp)
            )

            is MainUiEvent.UpdateWordSpacing -> settings.textConfig.copy(
                textStyle = settings.textConfig.textStyle.copy(wordSpacingSp = event.spacingSp)
            )

            // Stroke
            is MainUiEvent.ToggleStroke -> settings.textConfig.copy(
                stroke = settings.textConfig.stroke.copy(isEnabled = event.isEnabled)
            )

            is MainUiEvent.UpdateStrokeWidth -> settings.textConfig.copy(
                stroke = settings.textConfig.stroke.copy(width = event.width)
            )

            is MainUiEvent.UpdateStrokeColor -> settings.textConfig.copy(
                stroke = settings.textConfig.stroke.copy(colorArgb = event.colorArgb)
            )

            // Shadow
            is MainUiEvent.ToggleShadow -> settings.textConfig.copy(
                shadow = settings.textConfig.shadow.copy(isEnabled = event.isEnabled)
            )

            is MainUiEvent.UpdateShadowColor -> settings.textConfig.copy(
                shadow = settings.textConfig.shadow.copy(colorArgb = event.colorArgb)
            )

            is MainUiEvent.UpdateShadowRadius -> settings.textConfig.copy(
                shadow = settings.textConfig.shadow.copy(radius = event.radius)
            )


            is MainUiEvent.UpdateShadowRotation -> settings.textConfig.copy(
                shadow = settings.textConfig.shadow.copy(rotation = event.rotation)
            )

            is MainUiEvent.ApplyPreset -> settings.copy(
                textConfig = settings.textConfig
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
                            padding = innerPadding,
                            menus = {}
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
    device = "spec:width=1280dp,height=800dp,dpi=240",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewTabletDark() {
    InteractivePreviewWrapper(
        windowSize = WindowSize.EXPAND
    )
}
