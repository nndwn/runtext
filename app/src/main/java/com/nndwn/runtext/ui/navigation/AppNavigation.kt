package com.nndwn.runtext.ui.navigation

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nndwn.runtext.ui.features.debug.DebugScreen
import com.nndwn.runtext.ui.features.display.DisplayScreen
import com.nndwn.runtext.ui.features.main.MainScreen

object Routes {
    const val INPUT = "input"
    const val DISPLAY = "display"
    const val DEBUG = "debug"
}

@Composable
fun AppNavigation(
    padding: PaddingValues,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.INPUT,
    ) {
        composable(
            route = Routes.INPUT
        ) {
            MainScreen(
                padding = padding
            )
        }
        composable(
            route = Routes.DISPLAY,
            enterTransition = {
                scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(durationMillis = 400))
            },
            exitTransition = {
                scaleOut(
                    targetScale = 1.1f,
                    animationSpec = tween(durationMillis = 350)
                ) + fadeOut(animationSpec = tween(durationMillis = 350))
            },
            popEnterTransition = {
                scaleIn(
                    initialScale = 1.1f,
                    animationSpec = tween(durationMillis = 350)
                ) + fadeIn(animationSpec = tween(durationMillis = 350))
            },
            popExitTransition = {
                scaleOut(
                    targetScale = 0.8f,
                    animationSpec = tween(durationMillis = 400, easing = FastOutLinearInEasing)
                ) + fadeOut(animationSpec = tween(durationMillis = 400))
            }
        ) {
            DisplayScreen()
        }
        if (com.nndwn.runtext.BuildConfig.DEBUG) {
            composable(route = Routes.DEBUG) {
                DebugScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
