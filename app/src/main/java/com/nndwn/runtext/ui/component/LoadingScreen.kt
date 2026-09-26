package com.nndwn.runtext.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.R
import com.nndwn.runtext.ui.theme.RuntextTheme

@Composable
fun LoadingScreen(
  show: Boolean,
  value : Int = 0
) {
  Scrim(
    active = show,
    onDismiss = {},
  )
  AnimatedVisibility(
    visible = show,
    enter = fadeIn(),
    exit = fadeOut(),
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize(),
      contentAlignment = Alignment.Center,
    ) {
      Scrim(true) { }
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        LogoAnimation(
          modifier = Modifier,
          tint = MaterialTheme.colorScheme.onBackground,
          sizeLogo = 100.dp,
        )
        Text(
          text = stringResource(R.string.loading_creating, "$value%"),
          style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.onBackground
          )
        )
      }

    }
  }
}

@Preview
@Composable
private fun Preview() {
  RuntextTheme {
    LoadingScreen(show = true, value = 10)
  }
}
