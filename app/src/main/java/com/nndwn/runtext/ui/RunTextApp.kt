package com.nndwn.runtext.ui

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nndwn.runtext.ui.component.DialogNotice
import com.nndwn.runtext.ui.component.MainLayout
import com.nndwn.runtext.ui.navigation.AppNavigation
import com.nndwn.runtext.ui.navigation.Routes

@Composable
fun RunTextApp(
    appViewModel: AppViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    var isSidebarOpen by remember { mutableStateOf(false) }
    val sidebarAllowed = isSidebarOpen && currentRoute != Routes.DISPLAY

    var noticeMessage by remember { mutableStateOf<Int?>(null) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(   appViewModel.uiEffect, lifecycle) {
        appViewModel.uiEffect
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect{effect ->
                when (effect) {
                    is UiEffect.ShowToast -> {
                        noticeMessage = effect.message
                    }
                    is UiEffect.NavigateTo -> {
                        navController.navigate(effect.route)
                    }
                    is UiEffect.NavigateBack -> {
                        navController.popBackStack()
                    }
                }
            }

    }

    MainLayout(
        isSidebarOpen = sidebarAllowed,
        onCloseSidebar = { isSidebarOpen = false },
        sideBarRight = { },
        overlayContent = {
            DialogNotice(
                visible = noticeMessage != null,
                text = noticeMessage?.let { stringResource(it) } ?: "",
                onDismiss = { noticeMessage = null },
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) {innerPadding ->
        AppNavigation(
            sidebarEnd = { isSidebarOpen = !isSidebarOpen },
            navController = navController,
            padding = innerPadding
        )
    }
}


