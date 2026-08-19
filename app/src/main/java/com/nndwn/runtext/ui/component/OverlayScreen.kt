package com.nndwn.runtext.ui.component

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource


data class OverlayScreenState (
    val showAdsDialog: Boolean,
    val isLoadingAd: Boolean,
    @StringRes val noticeMessage: Int?,
    val adPrice: String?
)
@Composable
fun OverlayScreen(
    state: OverlayScreenState,
    onWatchAds: () -> Unit,
    onDismissAds: () -> Unit,
    onRemoveAds: () -> Unit,
    onDismissNoticeMessage : () -> Unit
) {
    DialogWatchAds(
        showPanel = state.showAdsDialog,
        price = state.adPrice,
        onDismiss = onDismissAds,
        onWatchAds = onWatchAds,
        onRemoveAds = onRemoveAds
    )

    LoadingScreen(show = state.isLoadingAd)

    DialogNotice(
        visible = state.noticeMessage != null,
        text = state.noticeMessage?.let { stringResource(it) } ?: "",
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        onDismiss = onDismissNoticeMessage
    )
}
