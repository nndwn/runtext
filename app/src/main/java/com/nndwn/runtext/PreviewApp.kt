package com.nndwn.runtext

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.ui.component.MainLayout
import com.nndwn.runtext.ui.features.main.MainUiEvent
import com.nndwn.runtext.ui.navigation.AppNavigation
import com.nndwn.runtext.ui.navigation.Routes
import com.nndwn.runtext.ui.theme.RuntextTheme
import com.nndwn.runtext.ui.utils.LocalIsTablet


@Composable
private fun InteractivePreviewWrapper(isTablet: Boolean) {
    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    var isSidebarOpen by remember { mutableStateOf(false) }
    val sidebarAllowed = isSidebarOpen && currentRoute != Routes.DISPLAY

    var settings by remember { mutableStateOf(AppSettings(
        lastText = "test"
    )) }

    val handleEvent: (MainUiEvent) -> Unit = { event ->
        settings = when (event) {
            // General
            is MainUiEvent.UpdateText -> settings.copy(lastText = event.text)
            is MainUiEvent.ClearText -> settings.copy(lastText = "")
            is MainUiEvent.UpdateMode -> settings.copy(mode = event.mode)
            is MainUiEvent.UpdateSpeed -> settings.copy(speed = event.speed)
            is MainUiEvent.UpdateBgColor -> settings.copy(bgColorArgb = event.colorArgb)
            is MainUiEvent.ToggleMirrorMode -> settings.copy(isMirrorMode = !settings.isMirrorMode)

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
            is MainUiEvent.UpdateShadowDistance -> settings.copy(
                shadow = settings.shadow.copy(distance = event.distance)
            )
            is MainUiEvent.UpdateShadowRotation -> settings.copy(
                shadow = settings.shadow.copy(rotation = event.rotation)
            )
        }
    }
    CompositionLocalProvider(LocalIsTablet provides isTablet) {
        RuntextTheme {
            MainLayout(
                isSidebarOpen = sidebarAllowed,
                onCloseSidebar = { isSidebarOpen = false },
                sideBarRight = { }
            ) {
                AppNavigation(
                    settings = settings,
                    onEvent = handleEvent,
                    onMenuClick = { isSidebarOpen = !isSidebarOpen },
                    navController = navController
                )
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
@Preview(device = "spec:width=1080px,height=2340px,dpi=480" , uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewTabletDark(){
    InteractivePreviewWrapper(false)
}
