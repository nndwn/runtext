package com.nndwn.runtext.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nndwn.runtext.data.model.AppMode
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.ui.features.main.MainScreen

object Routes {
    const val INPUT = "input"
    const val DISPLAY = "display"
}

@Composable
fun AppNavigation(
    settings: AppSettings,
    onUpdateText: (String) -> Unit,
    onClearText: () -> Unit,
    onUpdateMode: (AppMode) -> Unit,
    onMenuClick: () -> Unit,
    onUpdateSpeed : (Float) -> Unit,
    navController: NavHostController
){
    NavHost (
        navController = navController,
        startDestination = Routes.INPUT,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300),
            )
        },
        exitTransition = { fadeOut(animationSpec = tween(300)) },
        popEnterTransition = { fadeIn(animationSpec = tween(300)) },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300),
            )
        },
    ){
        composable(Routes.INPUT) {
            MainScreen(
                settings = settings,
                onUpdateText = onUpdateText,
                onClearText = onClearText,
                onUpdateMode = onUpdateMode,
                onMenuClick = onMenuClick,
                onUpdateSpeed =  onUpdateSpeed
            )
        }
        composable(Routes.DISPLAY) {

        }
    }
}