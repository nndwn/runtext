package com.nndwn.runtext.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class MainLayoutState (
    val isOpen : Boolean = false,
    val sidebarWidth : Dp = 240.dp,
    val backgroundColor : Color = Color.Unspecified,
    val sidebarBackgroundColor : Color = Color.Unspecified
)
@Composable
fun MainLayout(
    state : MainLayoutState = MainLayoutState(),
    onCloseSidebar : () -> Unit = {},
    topBarContent : @Composable  () -> Unit = {},
    sideBarEnd : @Composable () -> Unit = {},
    bottomBarContent : @Composable BoxScope.()-> Unit = {},
    overlayContent : @Composable BoxScope.() -> Unit = {},
    content : @Composable BoxScope.(padding : PaddingValues) -> Unit
){
    val backgroundColor = if (state.backgroundColor != Color.Unspecified) {
        state.backgroundColor
    } else {
        MaterialTheme.colorScheme.background
    }

    val sideBackgroundColor = if (state.sidebarBackgroundColor != Color.Unspecified) {
        state.sidebarBackgroundColor
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentTranslationX by animateDpAsState(
        targetValue = if (state.isOpen) -state.sidebarWidth else 0.dp,
        animationSpec = tween(durationMillis = 300),
        label = "ContentSlide"
    )
    val sidebarTranslationX by animateDpAsState(
        targetValue = if (state.isOpen) 0.dp else state.sidebarWidth,
        animationSpec = tween(durationMillis = 300),
        label = "SidebarSlide"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
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
                },
            ) { innerPadding ->
                content(innerPadding)
            }
            Scrim(
                active = state.isOpen,
                onDismiss = onCloseSidebar
            )
        }
        Surface(
            color = sideBackgroundColor,
            modifier = Modifier
                .fillMaxHeight()
                .width(state.sidebarWidth)
                .align(Alignment.TopEnd)
                .graphicsLayer{ translationX = sidebarTranslationX.toPx() }
                .statusBarsPadding()
        ) {
            sideBarEnd()
        }
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            overlayContent()
        }
    }
}

