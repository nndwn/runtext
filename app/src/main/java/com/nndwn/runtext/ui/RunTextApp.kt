package com.nndwn.runtext.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nndwn.runtext.BuildConfig
import com.nndwn.runtext.R
import com.nndwn.runtext.ui.component.DialogNotice
import com.nndwn.runtext.ui.component.MainLayout
import com.nndwn.runtext.ui.component.MenuOptions
import com.nndwn.runtext.ui.navigation.AppNavigation
import com.nndwn.runtext.ui.navigation.Routes
import com.nndwn.runtext.ui.utils.LocalIsPremium
import com.nndwn.runtext.ui.utils.LocalMenuOptionHandler
import com.nndwn.runtext.ui.utils.LocalToggleSidebar

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
    val context = LocalContext.current

    val handleMenuOption: (MenuOptions) -> Unit = { menu ->
        when (menu) {
            MenuOptions.DEBUG -> {}
            MenuOptions.RATE_APP -> gotoPlayStore(context)
            MenuOptions.REMOVE_ADS -> {}
            MenuOptions.REPORT_ISSUE -> gotoMail(context)
        }
        isSidebarOpen = false
    }

    val toggleSidebar: () -> Unit = { isSidebarOpen = !isSidebarOpen }

    LaunchedEffect(appViewModel.uiEffect, lifecycle) {
        appViewModel.uiEffect.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { effect ->
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
    //todo : put provides google billing
    CompositionLocalProvider(
        LocalIsPremium provides false,
        LocalToggleSidebar provides toggleSidebar,
        LocalMenuOptionHandler provides handleMenuOption
    ) {
        MainLayout(
            isSidebarOpen = sidebarAllowed,
            onCloseSidebar = { isSidebarOpen = false },
            sideBarRight = {
                MenuOptions(
                    onMenuSelected = handleMenuOption
                )
            },
            overlayContent = {
                DialogNotice(
                    visible = noticeMessage != null,
                    text = noticeMessage?.let { stringResource(it) } ?: "",
                    onDismiss = { noticeMessage = null })
            }) { innerPadding ->
            AppNavigation(
                navController = navController,
                padding = innerPadding,
            )
        }
    }
}

private fun gotoPlayStore(context: Context) {
    val packageName = context.packageName
    val marketIntent = Intent(
        Intent.ACTION_VIEW, "market://details?id=$packageName".toUri()
    )
    marketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        context.startActivity(marketIntent)
    } catch (_: ActivityNotFoundException) {
        val webIntent = Intent(
            Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$packageName".toUri()
        )
        context.startActivity(webIntent)
    }
}

private fun gotoMail(context: Context) {
    val deviceModel = Build.MODEL
    val androidVersion = Build.VERSION.RELEASE
    val appVersion = context.packageManager.getPackageInfo(context.packageName, 0).versionName

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.EMAIL))
        putExtra(Intent.EXTRA_SUBJECT, "Report Issue - ${context.getString(R.string.app_name)}")
        putExtra(
            Intent.EXTRA_TEXT,
            "\n\n\n---\nDevice: $deviceModel\nAndroid: $androidVersion\nApp Version: $appVersion"
        )
    }
    context.startActivity(Intent.createChooser(intent, "Send Email"))
}
