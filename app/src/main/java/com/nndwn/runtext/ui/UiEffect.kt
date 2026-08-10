package com.nndwn.runtext.ui

import androidx.annotation.StringRes

sealed interface UiEffect {
    data class ShowToast(  @param:StringRes val message : Int) : UiEffect
    data class NavigateTo(val route: String) : UiEffect
    data class RequestNavigationWithAdCheck(val targetRoute: String) : UiEffect
    object NavigateBack : UiEffect
}