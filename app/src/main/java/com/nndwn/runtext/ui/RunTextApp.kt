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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.nndwn.runtext.AppFlavor
import com.nndwn.runtext.ui.component.MainLayout
import com.nndwn.runtext.ui.component.MainLayoutState
import com.nndwn.runtext.ui.component.MenuOptions
import com.nndwn.runtext.ui.component.OverlayScreen
import com.nndwn.runtext.ui.component.OverlayScreenState
import com.nndwn.runtext.ui.features.debug.DebugScreen
import com.nndwn.runtext.ui.features.display.DisplayScreen
import com.nndwn.runtext.ui.features.main.MainScreen
import com.nndwn.runtext.ui.navigation.AppRoute
import com.nndwn.runtext.ui.navigation.Navigator
import com.nndwn.runtext.ui.navigation.rememberNavigationState
import com.nndwn.runtext.ui.navigation.toEntries
import com.nndwn.runtext.ui.theme.dimens
import com.nndwn.runtext.ui.utils.gotoMail
import com.nndwn.runtext.ui.utils.gotoPlayStore
import com.nndwn.runtext.ui.utils.handleSupportAction

@Composable
fun RunTextApp(
  appViewModel: AppViewModel = hiltViewModel(),
) {
  val context = LocalContext.current
  val lifecycle = LocalLifecycleOwner.current.lifecycle

  val isPremium by appViewModel.isPremium.collectAsStateWithLifecycle()
  val shouldShowSupportDialog by appViewModel.shouldShowSupportDialog.collectAsStateWithLifecycle()
  val appPrice by appViewModel.appPrice.collectAsStateWithLifecycle()

  var isSidebarOpen by remember { mutableStateOf(false) }
  var noticeMessage by remember { mutableStateOf<Int?>(null) }
  var showDialogSupport by remember { mutableStateOf(false) }
  var pendingRoute by remember { mutableStateOf<AppRoute?>(null) }

  val navigationState = rememberNavigationState(
    startRoute = AppRoute.Input,
    topLevelRoutes = setOf(AppRoute.Input)
  )
  val navigator = remember { Navigator(navigationState) }

  val currentRoute = navigationState.backStacks[navigationState.topLevelRoute]?.last()
  val sidebarAllowed = isSidebarOpen && currentRoute != AppRoute.Display
  val isPlayStore = AppFlavor.current == AppFlavor.PLAYSTORE

  // UI Effects handling
  LaunchedEffect(appViewModel.uiEffect, lifecycle) {
    appViewModel.uiEffect.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect { effect ->
      when (effect) {
        is UiEffect.ShowToast -> noticeMessage = effect.message
        is UiEffect.NavigateTo -> navigator.navigate(effect.route)
        is UiEffect.NavigateBack -> navigator.goBack()
        is UiEffect.RequestNavigationWithSupportDialogCheck -> {
          if (isPlayStore && shouldShowSupportDialog && !isPremium) {
            pendingRoute = effect.targetRoute
            showDialogSupport = true
          } else {
            navigator.navigate(effect.targetRoute)
          }
        }
      }
    }
  }

  val handleMenuOption: (MenuOptions) -> Unit = { menu ->
    isSidebarOpen = false
    when (menu) {
      MenuOptions.DEBUG -> navigator.navigate(AppRoute.Debug)
      MenuOptions.RATE_APP -> gotoPlayStore(context)
      MenuOptions.SUPPORT -> handleSupportAction(context) { showDialogSupport = true }
      MenuOptions.REPORT_ISSUE -> gotoMail(context)
    }
  }

  CompositionLocalProvider(
    LocalToggleSidebar provides { isSidebarOpen = !isSidebarOpen },
    LocalMenuOptionHandler provides handleMenuOption,
  ) {
    MainLayout(
      state =
        MainLayoutState().copy(isOpen = sidebarAllowed, sidebarBackgroundColor = MaterialTheme.colorScheme.secondary),
      onCloseSidebar = { isSidebarOpen = false },
      sideBarEnd = {
        MenuOptions(
          onMenuSelected = handleMenuOption,
          modifier = Modifier.padding(vertical = MaterialTheme.dimens.large),
        )
      },
      overlayContent = {
        OverlayScreen(
          state = OverlayScreenState(showDialogSupport, noticeMessage, appPrice),
          onDismissSupportDialog = {
            showDialogSupport = false
            pendingRoute?.let { route ->
              navigator.navigate(route)
              pendingRoute = null
            }
            appViewModel.resetCooldownSupportDialog()
          },
          onClickBuyApp = {
            val activity = context as? Activity ?: return@OverlayScreen
            showDialogSupport = false
            appViewModel.onBuyApp(activity)
          },
          onDismissNoticeMessage = { noticeMessage = null },
        )
      },
    ) { innerPadding ->
      val entryProvider = remember(innerPadding) {
        entryProvider<NavKey> {
          entry<AppRoute.Input> { MainScreen(padding = innerPadding) }
          entry<AppRoute.Display> { DisplayScreen() }
          entry<AppRoute.Debug> { DebugScreen(onBack = { navigator.goBack() }) }
        }
      }

      NavDisplay(
        entries = navigationState.toEntries(entryProvider),
        onBack = { navigator.goBack() }
      )
    }
  }
}
