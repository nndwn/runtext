package com.nndwn.runtext.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nndwn.runtext.data.model.AppMode
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.ui.component.Header
import com.nndwn.runtext.ui.component.InputTextPreview
import com.nndwn.runtext.ui.component.MainLayout
import com.nndwn.runtext.ui.navigation.Routes
import com.nndwn.runtext.ui.theme.NeonGreen
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.theme.RuntextTheme
import com.nndwn.runtext.ui.utils.Dimens
import com.nndwn.runtext.ui.utils.LocalIsTablet
import com.nndwn.runtext.ui.viewmodel.RunTextViewModel

@Composable
fun RunTextApp(
    navController: NavHostController = rememberNavController(),
    viewModel: RunTextViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val isTablet = LocalIsTablet.current

    RunTextAppContent(
        isTablet = isTablet,
        currentRoute = currentRoute,
        settings = settings,
        onUpdateMode = viewModel::updateMode,
        onUpdateText = viewModel::updateText,
        onClearText = viewModel::clearText
    )
}

@Composable
fun RunTextAppContent(
    isTablet : Boolean,
    currentRoute: String?,
    settings: AppSettings,
    onUpdateMode: (AppMode) -> Unit,
    onUpdateText: (String) -> Unit,
    onClearText: () -> Unit
) {
    var isSidebarOpen by remember { mutableStateOf(false) }
    val sidebarAllowed = isSidebarOpen && currentRoute != Routes.DISPLAY


    MainLayout(
        isSidebarOpen = sidebarAllowed,
        onCloseSidebar = { isSidebarOpen = false },
        sideBarRight = { }
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Sidebar for Tablet
            AnimatedVisibility(
                visible = isTablet,
                enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            ) {
                Scaffold(
                    modifier = Modifier.width(300.dp),
                    containerColor = Palette.Black3,
                    topBar = { Header() }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        // Configuration UI for tablet could go here
                    }
                }
            }

            // Main Content Area
            Scaffold(
                topBar = {
                    AnimatedVisibility(
                        visible = !isTablet,
                        enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                        exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                    ) {
                        Header(
                            withSidebar = true,
                            onMenuClick = { isSidebarOpen = true }
                        )
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(
                            horizontal = Dimens.PaddingHorizontal,
                            vertical = Dimens.ArrangementHeight
                        ),
                    verticalArrangement = Arrangement.spacedBy(Dimens.ArrangementHeight)
                ) {
                    InputTextPreview(
                        settings = settings,
                        modifier = Modifier,
                        onUpdateText = onUpdateText,
                        onClearText = onClearText
                    )
                    SingleChoiceSegmentedButtonRow(modifier = Modifier
                        .fillMaxWidth()) {
                        val customColors = SegmentedButtonDefaults.colors(
                            activeContainerColor = Palette.Black3,
                            activeContentColor = Palette.White,
                            activeBorderColor = Palette.Black3,
                            inactiveContainerColor = Color.Transparent,
                            inactiveContentColor = Palette.White,
                            inactiveBorderColor = Palette.Grey.copy(alpha = 0.5f)
                        )
                        SegmentedButton(
                            selected = settings.mode == AppMode.RUNNING_TEXT,
                            onClick = { onUpdateMode(AppMode.RUNNING_TEXT) },
                            colors = customColors,
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            icon = { Icon(Icons.Default.TextFields, null, Modifier.size(18.dp)) },
                        ) { Text("Running Text") }

                        SegmentedButton(
                            selected = settings.mode == AppMode.MORSE_CODE,
                            onClick = { onUpdateMode(AppMode.MORSE_CODE) },
                            colors = customColors,
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            icon = { Icon(Icons.Default.FlashOn, null, Modifier.size(18.dp)) },
                        ) { Text("Morse Code") }
                    }
                }

            }
        }
    }
}

//@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape",
//    showSystemUi = true
//)
//@Composable
//private fun RunTextAppTabletPreview() {
//    RuntextTheme {
//        RunTextAppContent(
//            isTablet = true,
//            currentRoute = Routes.INPUT,
//            settings = AppSettings(lastText = "PREVIEW TABLET"),
//            onUpdateText = {},
//            onClearText = {},
//            onUpdateMode = {}
//        )
//    }
//
//}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RunTextAppMobilePreview() {
    RuntextTheme {
        RunTextAppContent(
            isTablet = false,
            currentRoute = Routes.INPUT,
            settings = AppSettings(lastText = "PREVIEW MOBILE"),
            onUpdateText = {},
            onClearText = {},
            onUpdateMode = {}
        )
    }

}
