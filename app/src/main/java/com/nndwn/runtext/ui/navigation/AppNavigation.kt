package com.nndwn.runtext.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
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
                scaleIn(
                    initialScale = 0.8f, // Mulai dari 80% ukuran asli
                    animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(durationMillis = 400))
            },
            exitTransition = {
                scaleOut(
                    targetScale = 1.1f, // Membesar ke 110% saat menghilang
                    animationSpec = tween(durationMillis = 350)
                ) + fadeOut(animationSpec = tween(durationMillis = 350))
            },
            popEnterTransition = {
                scaleIn(
                    initialScale = 1.1f, // Mulai dari 110% lalu mengecil ke normal
                    animationSpec = tween(durationMillis = 350)
                ) + fadeIn(animationSpec = tween(durationMillis = 350))
            },
            popExitTransition = {
                scaleOut(
                    targetScale = 0.8f, // Mengecil ke 80% saat menutup
                    animationSpec = tween(durationMillis = 400, easing = FastOutLinearInEasing)
                ) + fadeOut(animationSpec = tween(durationMillis = 400))
            }
        ) {
            DisplayScreen()
        }
    }
}