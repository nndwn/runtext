package com.nndwn.runtext.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface AppRoute : NavKey {
  @Serializable
  data object Input : AppRoute

  @Serializable
  data object Display : AppRoute

  @Serializable
  data object Debug : AppRoute
}
