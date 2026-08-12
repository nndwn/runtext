package com.nndwn.runtext.ui

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nndwn.runtext.AppFlavor
import com.nndwn.runtext.ui.component.DialogNotice
import com.nndwn.runtext.ui.component.DialogWatchAds
import com.nndwn.runtext.ui.component.LoadingScreen
import com.nndwn.runtext.ui.component.MainLayout
import com.nndwn.runtext.ui.component.MenuOptions
import com.nndwn.runtext.ui.navigation.AppNavigation
import com.nndwn.runtext.ui.navigation.Routes
import com.nndwn.runtext.ui.theme.dimens
import com.nndwn.runtext.ui.utils.LocalIsPremium
import com.nndwn.runtext.ui.utils.LocalMenuOptionHandler
import com.nndwn.runtext.ui.utils.LocalToggleSidebar
import com.nndwn.runtext.ui.utils.gotoMail
import com.nndwn.runtext.ui.utils.gotoPlayStore

@Composable
fun RunTextApp(
    appViewModel: AppViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    
    val isPremium by appViewModel.isPremium.collectAsStateWithLifecycle()
    val shouldShowAd by appViewModel.shouldShowAd.collectAsStateWithLifecycle()
    val isLoadingAd by appViewModel.isLoadingAd.collectAsStateWithLifecycle()
    val adPrice by appViewModel.removeAdsPrice.collectAsStateWithLifecycle()

    var isSidebarOpen by remember { mutableStateOf(false) }
    var noticeMessage by remember { mutableStateOf<Int?>(null) }
    var showAdsDialog by remember { mutableStateOf(false) }
    var pendingRoute by remember { mutableStateOf<String?>(null) }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val sidebarAllowed = isSidebarOpen && currentRoute != Routes.DISPLAY

    // UI Effects handling
    LaunchedEffect(appViewModel.uiEffect, lifecycle) {
        appViewModel.uiEffect.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
                when (effect) {
                    is UiEffect.ShowToast -> noticeMessage = effect.message
                    is UiEffect.NavigateTo -> navController.navigate(effect.route)
                    is UiEffect.NavigateBack -> navController.popBackStack()
                    is UiEffect.RequestNavigationWithAdCheck -> {
                        val isPlayStore = AppFlavor.current == AppFlavor.PLAYSTORE
                        if (isPlayStore && shouldShowAd && !isPremium) {
                            pendingRoute = effect.targetRoute
                            showAdsDialog = true
                        } else {
                            navController.navigate(effect.targetRoute)
                        }
                    }
                }
            }
    }

    val handleMenuOption: (MenuOptions) -> Unit = { menu ->
        isSidebarOpen = false
        when (menu) {
            MenuOptions.DEBUG -> navController.navigate(Routes.DEBUG)
            MenuOptions.RATE_APP -> gotoPlayStore(context)
            MenuOptions.REMOVE_ADS -> if (!isPremium) showAdsDialog = true
            MenuOptions.REPORT_ISSUE -> gotoMail(context)
        }
    }

    CompositionLocalProvider(
        LocalIsPremium provides isPremium,
        LocalToggleSidebar provides { isSidebarOpen = !isSidebarOpen },
        LocalMenuOptionHandler provides handleMenuOption
    ) {
        MainLayout(
            isSidebarOpen = sidebarAllowed,
            onCloseSidebar = { isSidebarOpen = false },
            sideBarRight = { MenuOptions(
                onMenuSelected = handleMenuOption,
                modifier = Modifier.padding(vertical = MaterialTheme.dimens.large)
            )
                           },
            overlayContent = {
                AppOverlay(
                    showAdsDialog = showAdsDialog,
                    isLoadingAd = isLoadingAd,
                    noticeMessage = noticeMessage,
                    adPrice = adPrice,
                    onWatchAds = {
                        val activity = context as? Activity ?: return@AppOverlay
                        appViewModel.performAdFlow(activity) {
                            showAdsDialog = false
                            pendingRoute?.let { route ->
                                navController.navigate(route)
                                pendingRoute = null
                            }
                        }
                    },
                    onDismissAds = {
                        showAdsDialog = false
                        pendingRoute?.let { route ->
                            navController.navigate(route)
                            pendingRoute = null
                        }
                    },
                    onRemoveAds = {
                        val activity = context as? Activity ?: return@AppOverlay
                        showAdsDialog = false
                        appViewModel.onRemoveAdsClicked(activity)
                    },
                    onDismissNotice = { noticeMessage = null }
                )
            }
        ) { innerPadding ->
            AppNavigation(navController = navController, padding = innerPadding)
        }
    }
}

@Composable
private fun AppOverlay(
    showAdsDialog: Boolean,
    isLoadingAd: Boolean,
    noticeMessage: Int?,
    adPrice: String?,
    onWatchAds: () -> Unit,
    onDismissAds: () -> Unit,
    onRemoveAds: () -> Unit,
    onDismissNotice: () -> Unit
) {
    DialogWatchAds(
        showPanel = showAdsDialog,
        price = adPrice,
        onDismiss = onDismissAds,
        onWatchAds = onWatchAds,
        onRemoveAds = onRemoveAds
    )

    LoadingScreen(show = isLoadingAd)

    DialogNotice(
        visible = noticeMessage != null,
        text = noticeMessage?.let { stringResource(it) } ?: "",
        onDismiss = onDismissNotice
    )
}
