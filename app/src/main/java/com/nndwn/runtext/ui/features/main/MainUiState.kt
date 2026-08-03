package com.nndwn.runtext.ui.features.main

import com.nndwn.runtext.data.model.AppSettings

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val settings: AppSettings) : MainUiState
}