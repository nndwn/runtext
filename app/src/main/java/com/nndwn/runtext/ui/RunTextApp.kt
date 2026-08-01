package com.nndwn.runtext.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nndwn.runtext.ui.component.MainLayout
import com.nndwn.runtext.ui.navigation.AppNavigation
import com.nndwn.runtext.ui.navigation.Routes

@Composable
fun RunTextApp(
    navController: NavHostController = rememberNavController(),
) {
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
            sidebarEnd = { isSidebarOpen = !isSidebarOpen },
            navController = navController,
        )
    }
}


