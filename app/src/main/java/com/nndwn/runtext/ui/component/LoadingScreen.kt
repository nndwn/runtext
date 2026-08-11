package com.nndwn.runtext.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.ui.theme.RuntextTheme

@Composable
fun LoadingScreen(
    show : Boolean
) {
    Scrim(
        active = show, onDismiss = {}
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        LogoAnimation(
            modifier = Modifier,
            tint = MaterialTheme.colorScheme.onBackground,
            sizeLogo = 150.dp
        )
    }
}

@Preview
@Composable
private fun Preview() {
    RuntextTheme {
        LoadingScreen(
            show = true
        )
    }
}