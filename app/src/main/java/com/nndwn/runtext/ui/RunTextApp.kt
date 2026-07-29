package com.nndwn.runtext.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nndwn.runtext.ui.component.MainLayout
import com.nndwn.runtext.ui.features.main.MainViewModel
import com.nndwn.runtext.ui.navigation.AppNavigation
import com.nndwn.runtext.ui.navigation.Routes

@Composable
fun RunTextApp(
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    var isSidebarOpen by remember { mutableStateOf(false) }
    val sidebarAllowed = isSidebarOpen && currentRoute != Routes.DISPLAY


    MainLayout(
        isSidebarOpen = sidebarAllowed,
        onCloseSidebar = { isSidebarOpen = false },
        sideBarRight = { }
    ) {
        AppNavigation(
            settings = settings,
            onUpdateText = viewModel::updateText,
            onClearText = viewModel::clearText,
            onUpdateMode = viewModel::updateMode,
            onMenuClick = { isSidebarOpen = !isSidebarOpen },
            navController = navController,
            onUpdateSpeed = viewModel::updateSpeed,
            onUpdateBackgroundColor = viewModel::updateBgColor,
            onUpdateTextColor = viewModel::updateTextColor,
            onUpdateTextColorType = viewModel::updateTextColorType,
            onUpdateGradientColors = viewModel::updateGradientColors,
            onUpdateGradientDistance = viewModel::updateGradientDistance,
            onUpdateFontType = viewModel::updateFontType,
            onUpdateHorizontalPosition = viewModel::updateHorizontalPosition,
            onToggleStroke = viewModel::toggleStroke,
            onUpdateStrokeWidth = viewModel::updateStrokeWidth,
            onUpdateStrokeColor = viewModel::updateStrokeColor
        )
    }
}


