package com.nndwn.runtext.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nndwn.runtext.ui.features.main.MainScreen

object Routes {
    const val INPUT = "input"
    const val DISPLAY = "display"
}

@Composable
fun AppNavigation(
    sidebarEnd: () -> Unit,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Routes.INPUT
    ) {
        composable(Routes.INPUT) {
            MainScreen(
                sideBarEnd = sidebarEnd,
                onNavigateToDisplay = { navController.navigate(Routes.DISPLAY) }
            )
        }
        composable(Routes.DISPLAY) {
            // Display Screen
        }
    }
}