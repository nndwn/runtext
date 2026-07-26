package com.nndwn.runtext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.ui.component.MainLayout
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

    var settings by remember { mutableStateOf(AppSettings()) }


    CompositionLocalProvider(LocalIsTablet provides isTablet) {
        RuntextTheme {
            MainLayout(
                isSidebarOpen = sidebarAllowed,
                onCloseSidebar = { isSidebarOpen = false },
                sideBarRight = { }
            ) {
                AppNavigation(
                    settings = settings,
                    onUpdateText = { text ->
                        settings = settings.copy(
                            lastText = text
                        )
                    },
                    onClearText = {
                        if (!settings.lastText.isEmpty()){
                            settings = settings.copy(
                                lastText = ""
                            )
                        }
                    },
                    onUpdateMode = {
                        settings = settings.copy(
                            mode = it
                        )
                    },
                    onMenuClick = { isSidebarOpen = !isSidebarOpen },
                    navController = navController,
                    onUpdateSpeed = {
                        settings  = settings.copy(
                            speed = it
                        )
                    }
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

@Preview
@Composable
private fun PreviewTablet(){
    InteractivePreviewWrapper(false)
}
