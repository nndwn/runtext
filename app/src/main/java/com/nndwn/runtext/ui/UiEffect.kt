package com.nndwn.runtext.ui

import androidx.annotation.StringRes
import com.nndwn.runtext.ui.navigation.AppRoute

sealed interface UiEffect {
  data class ShowToast(@param:StringRes val message: Int) : UiEffect

  data class NavigateTo(val route: AppRoute) : UiEffect

  data object NavigateBack : UiEffect

  data object RequestNavigateBackWithSupportDialogCheck : UiEffect
}
