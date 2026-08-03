package com.nndwn.runtext.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.ui.theme.Palette

@Composable
fun MainLayout(
    isSidebarOpen: Boolean = false,
    onCloseSidebar : () -> Unit = {},
    topBarContent : @Composable  () -> Unit = {},
    sideBarRight : @Composable () -> Unit = {},
    bottomBarContent : @Composable BoxScope.()-> Unit = {},
    overlayContent : @Composable BoxScope.() -> Unit = {},
    content : @Composable BoxScope.(padding : PaddingValues) -> Unit
){
    val sidebarWidth = 240.dp
    val contentTranslationX by animateDpAsState(
        targetValue = if (isSidebarOpen) -sidebarWidth else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "ContentSlide"
    )
    val sidebarTranslationX by animateDpAsState(
        targetValue = if (isSidebarOpen) 0.dp else sidebarWidth,
        animationSpec = tween(durationMillis = 300),
        label = "SidebarSlide"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Palette.Black2)
    ){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationX = contentTranslationX.toPx() }
        ){
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    topBarContent()
                },
                bottomBar = {
                    bottomBarContent()
                }
            ) { innerPadding ->
                Box(modifier = Modifier.fillMaxSize()) {
                    content(innerPadding)
                }
            }
            Scrim(
                active = isSidebarOpen,
                onDismiss = onCloseSidebar
            )
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(sidebarWidth)
                .align(Alignment.TopEnd)
                .graphicsLayer { translationX = sidebarTranslationX.toPx() }
                .shadow(elevation = 12.dp)
                .background(Palette.Black2)
                .statusBarsPadding()
        ){
            sideBarRight()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            overlayContent()
        }
    }
}
