package com.nndwn.runtext.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val uiEffectController: UiEffectController
): ViewModel() {
    val uiEffect = uiEffectController.uiEffect
    

}
