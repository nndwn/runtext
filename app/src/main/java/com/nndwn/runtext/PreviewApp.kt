package com.nndwn.runtext

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import com.nndwn.runtext.ui.component.MainLayout
import com.nndwn.runtext.ui.component.MainLayoutState
import com.nndwn.runtext.ui.features.main.MainScreenContent
import com.nndwn.runtext.ui.features.main.MainUiState
import com.nndwn.runtext.ui.navigation.Routes
import com.nndwn.runtext.ui.theme.RuntextTheme
import com.nndwn.runtext.ui.utils.LocalSizeHeight
import com.nndwn.runtext.ui.utils.LocalSizeWidth


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun InteractivePreviewWrapper(
    widowSizeHeight : WindowHeightSizeClass = WindowHeightSizeClass.Compact,
    windowWidth : WindowWidthSizeClass = WindowWidthSizeClass.Compact
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

    CompositionLocalProvider(
        LocalSizeHeight provides widowSizeHeight,
        LocalSizeWidth provides windowWidth
    ) {
        RuntextTheme {
            MainLayout(
                state = MainLayoutState().copy(
                    isOpen = sidebarAllowed,
                    sidebarBackgroundColor = MaterialTheme.colorScheme.secondary
                ),
                onCloseSidebar = { isSidebarOpen = false },
                sideBarEnd = { }) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Routes.INPUT
                ) {
                    composable(Routes.INPUT) {
                        MainScreenContent(
                            uiState = MainUiState.Success(settings),
                            onEvent = {},
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

@Preview(
    device = "spec:width=673dp,height=841dp",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewTabletDark() {
    InteractivePreviewWrapper(
        widowSizeHeight = WindowHeightSizeClass.Medium,
        windowWidth = WindowWidthSizeClass.Medium
    )
}
