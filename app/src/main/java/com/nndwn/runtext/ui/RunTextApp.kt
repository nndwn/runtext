package com.nndwn.runtext.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nndwn.runtext.R
import com.nndwn.runtext.ui.component.MainLayout
import com.nndwn.runtext.ui.component.ThreeDotsHorizontal
import com.nndwn.runtext.ui.navigation.Routes
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.utils.Dimens
import com.nndwn.runtext.ui.viewmodel.RunTextViewModel



@Composable
fun RunTextApp(
    navController: NavHostController = rememberNavController(),
    viewModel : RunTextViewModel = hiltViewModel()
){

    var isSidebarOpen by remember { mutableStateOf(false) }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val sidebarAllowed = isSidebarOpen && currentRoute != Routes.DISPLAY
    MainLayout(
        isSidebarOpen = sidebarAllowed,
        onCloseSidebar = { isSidebarOpen = false},
        sideBarRight = {

        },
        topBarContent = {

        }
    ) { }
}

