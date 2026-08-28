package com.nndwn.runtext.ui.component

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

data class OverlayScreenState(val showDialogSupport: Boolean, @StringRes val noticeMessage: Int?, val appPrice: String?)

@Composable
fun OverlayScreen(
  state: OverlayScreenState,
  onDismissSupportDialog: () -> Unit,
  onClickBuyApp: () -> Unit,
  onDismissNoticeMessage: () -> Unit,
) {
  DialogSupport(
    showPanel = state.showDialogSupport,
    price = state.appPrice,
    onDismiss = onDismissSupportDialog,
    onClickBuyApp = onClickBuyApp,
  )

  DialogNotice(
    visible = state.noticeMessage != null,
    text = state.noticeMessage?.let { stringResource(it) } ?: "",
    containerColor = MaterialTheme.colorScheme.secondaryContainer,
    onDismiss = onDismissNoticeMessage,
  )
}
