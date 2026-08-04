package com.nndwn.runtext.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nndwn.runtext.ui.features.display.DisplayScreen
import com.nndwn.runtext.ui.features.main.MainScreen

object Routes {
    const val INPUT = "input"
    const val DISPLAY = "display"
}

@Composable
fun AppNavigation(
    padding: PaddingValues,
    sidebarEnd: () -> Unit,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.INPUT
    ) {
        composable(
            route = Routes.INPUT
        ) {
            MainScreen(
                padding =  padding,
                sideBarEnd = sidebarEnd
            )
        }
        composable(
            route = Routes.DISPLAY,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 350))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(durationMillis = 400, easing = FastOutLinearInEasing)
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 350))
            }
        ) {
            DisplayScreen()
        }
    }
}