package com.nndwn.runtext.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.ui.features.main.MainScreen
import com.nndwn.runtext.ui.features.main.MainUiEvent

object Routes {
    const val INPUT = "input"
    const val DISPLAY = "display"
}

@Composable
fun AppNavigation(
    settings: AppSettings,
    onEvent: (MainUiEvent) -> Unit,
    onMenuClick: () -> Unit,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.INPUT
    ) {
        composable(Routes.INPUT) {
            MainScreen(
                settings = settings,
                onEvent = onEvent,
                onMenuClick = onMenuClick
            )
        }
        composable(Routes.DISPLAY) {
            // Display Screen
        }
    }
}