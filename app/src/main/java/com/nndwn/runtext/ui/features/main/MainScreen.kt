package com.nndwn.runtext.ui.features.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.AppMode
import com.nndwn.runtext.ui.component.ColorPickerField
import com.nndwn.runtext.ui.component.ConfigCard
import com.nndwn.runtext.ui.component.Skeleton
import com.nndwn.runtext.ui.component.SwitchRow
import com.nndwn.runtext.ui.component.ThreeDotsHorizontal
import com.nndwn.runtext.ui.features.main.components.AppModeSettings
import com.nndwn.runtext.ui.features.main.components.LogoText
import com.nndwn.runtext.ui.features.main.components.MorseColorConfig
import com.nndwn.runtext.ui.features.main.components.MorseSpeedConfig
import com.nndwn.runtext.ui.features.main.components.MorseTorchConfig
import com.nndwn.runtext.ui.features.main.components.PreviewAndStart
import com.nndwn.runtext.ui.features.main.components.SelectorFonts
import com.nndwn.runtext.ui.features.main.components.TextColorPickerConfig
import com.nndwn.runtext.ui.features.main.components.TextFontStyleConfig
import com.nndwn.runtext.ui.features.main.components.TextInputConfig
import com.nndwn.runtext.ui.features.main.components.TextOutlineConfig
import com.nndwn.runtext.ui.features.main.components.TextPresetConfig
import com.nndwn.runtext.ui.features.main.components.TextShadowConfig
import com.nndwn.runtext.ui.features.main.components.TextSpacingConfig
import com.nndwn.runtext.ui.features.main.components.TextSpeedConfig
import com.nndwn.runtext.ui.theme.dimens
import com.nndwn.runtext.ui.theme.toComposeColor
import com.nndwn.runtext.ui.utils.LocalIsTablet

@Composable
fun MainScreen(
    viewModel : MainViewModel = hiltViewModel(),
    padding: PaddingValues,
    sideBarEnd: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    MainScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        sideBarEnd = sideBarEnd,
        padding =  padding,
    )
}

@Composable
fun MainScreenContent(
    padding: PaddingValues,
    uiState: MainUiState,
    onEvent: (MainUiEvent) -> Unit,
    sideBarEnd: () -> Unit,
) {
    val isTablet = LocalIsTablet.current
    val focusManager = LocalFocusManager.current
    var expandedPickerId by remember { mutableStateOf<String?>("text_presets") }
    var showPanelFonts by remember { mutableStateOf(false) }
    
    val closePicker = { expandedPickerId = null }

    val togglePicker: (String) -> Unit = { id ->
        expandedPickerId = if (expandedPickerId == id) null else id
        focusManager.clearFocus()
    }

    val dispatch: (MainUiEvent) -> Unit = { event ->
        if (event !is MainUiEvent.UpdateText && event !is MainUiEvent.ClearText) {
            focusManager.clearFocus()
        }
        onEvent(event)
    }

    val dispatchAndClosePicker: (MainUiEvent) -> Unit = { event ->
        focusManager.clearFocus()
        closePicker()
        dispatch(event)
    }

    val transition = rememberInfiniteTransition(label = "MainShimmerTransition")
    val shimmerProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "MainShimmerProgress"
    )

    Row(modifier = Modifier.fillMaxWidth()) {
        // Sidebar for Tablet
        AnimatedVisibility(
            visible = isTablet,
            enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        ) {
//            Scaffold(
//                modifier = Modifier.width(300.dp),
//                containerColor = Palette.Black3,
//                topBar = { HeaderStartSideBar() }
//            ) { innerPadding ->
//                Box(modifier = Modifier.padding(innerPadding)) {
//                    // Configuration UI for tablet could go here
//                }
//            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(padding),
            contentPadding = PaddingValues(
                bottom = padding.calculateBottomPadding(),
                start = MaterialTheme.dimens.medium,
                end =  MaterialTheme.dimens.medium,

            ),
            verticalArrangement = Arrangement.spacedBy( MaterialTheme.dimens.medium)

        ) {
            item {
                AnimatedVisibility(
                    visible = !isTablet,
                    enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                ) {
                    LogoText(
                        modifier = Modifier
                            .padding( vertical = 8.dp),
                        content = {
                            ThreeDotsHorizontal(onClick = { sideBarEnd() })
                        }
                    )
                }
            }
            when (uiState) {
                is MainUiState.Loading -> {
                    stickyHeader {
                        Skeleton(
                            shimmerProgress = shimmerProgress,
                            height = 140.dp
                        )
                    }
                    item {
                        Skeleton(
                            shimmerProgress = shimmerProgress,
                            height = 120.dp
                        )
                    }
                    items(7) {
                        Skeleton(
                            shimmerProgress = shimmerProgress,
                            height = 56.dp
                        )
                    }
                }
                is MainUiState.Success -> {
                    val settings = uiState.settings
                    stickyHeader {
                        PreviewAndStart(
                            settings = settings,
                            onNavigateToDisplay = {dispatch(MainUiEvent.NavigateToDisplay)}
                        )
                    }

                    item {
                        TextInputConfig(
                            text = settings.lastText,
                            onTextChange = { dispatch(MainUiEvent.UpdateText(it)) },
                            onClearText = { dispatch(MainUiEvent.ClearText) }
                        )
                    }

                    item {
                        AppModeSettings(
                            currentMode = settings.mode,
                            onModeChange = {
                                expandedPickerId = when (it){
                                    AppMode.MORSE_CODE -> "morse_color"
                                    AppMode.RUNNING_TEXT -> "text_presets"
                                }

                                dispatch(MainUiEvent.UpdateMode(it)) }
                        )
                    }

                    item {
                        AnimatedContent(
                            targetState = settings.mode,
                            transitionSpec = {
                                if (targetState == AppMode.MORSE_CODE) {
                                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                                            slideOutHorizontally { width -> -width } + fadeOut()
                                } else {
                                    slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                            slideOutHorizontally { width -> width } + fadeOut()
                                }
                            },
                            label = "ModeSettingsTransition"
                        ) { mode ->
                            when (mode) {
                                AppMode.RUNNING_TEXT -> {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium)
                                    ) {
                                        TextPresetConfig(
                                            expandedId = expandedPickerId,
                                            onToggle = togglePicker,
                                            onEvent = dispatch
                                        )
                                        TextSpeedConfig(
                                            speed = settings.speed,
                                            onSpeedChange = { dispatch(MainUiEvent.UpdateSpeed(it)) }
                                        )
                                        ConfigCard {
                                            SwitchRow(
                                                title = stringResource(R.string.set_config_text_mirror),
                                                subtitle = stringResource(R.string.set_config_text_mirror_desc),
                                                checked = settings.isMirrorMode,
                                                onCheckedChange = { dispatchAndClosePicker(MainUiEvent.UpdateMirrorMode(it)) },
                                            )
                                        }
                                        TextFontStyleConfig(
                                            config = settings.textStyle,
                                            onClick = { closePicker(); showPanelFonts = !showPanelFonts }
                                        )
                                        TextSpacingConfig(
                                            config = settings.textStyle,
                                            expandedId = expandedPickerId,
                                            onToggle = togglePicker,
                                            onEvent = dispatch
                                        )
                                        ConfigCard {
                                            ColorPickerField(
                                                label = stringResource(R.string.set_config_color_background),
                                                color = settings.bgColorArgb.toComposeColor(),
                                                isExpanded = expandedPickerId == "bg",
                                                onToggleExpand = { togglePicker("bg") },
                                                onColorChange = { dispatch(MainUiEvent.UpdateBgColor(it)) }
                                            )
                                        }
                                        TextColorPickerConfig(
                                            label = stringResource(R.string.set_config_text_color_text),
                                            config = settings.textStyle,
                                            expandedPickerId = expandedPickerId,
                                            onPickerToggle = togglePicker,
                                            onEvent = dispatch
                                        )
                                        TextOutlineConfig(
                                            config = settings.stroke,
                                            expandedPickerId = expandedPickerId,
                                            onPickerToggle = togglePicker,
                                            onEvent = dispatch
                                        )
                                        TextShadowConfig(
                                            config = settings.shadow,
                                            expandedPickerId = expandedPickerId,
                                            onPickerToggle = togglePicker,
                                            onEvent = dispatch
                                        )
                                    }
                                }

                                AppMode.MORSE_CODE -> {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium)
                                    ) {
                                        MorseColorConfig(
                                            currentColor = settings.morseConfig.bgColorMorse.toComposeColor(),
                                            expandedId = expandedPickerId,
                                            onToggle = togglePicker,
                                            onEvent = dispatch
                                        )
                                        MorseSpeedConfig(
                                            speed = settings.morseConfig.morseWpm,
                                            event = dispatch
                                        )
                                        ConfigCard {
                                            SwitchRow(
                                                title = stringResource(R.string.set_config_morse_flash_screen),
                                                subtitle = stringResource(R.string.set_config_morse_flash_screen_desc),
                                                checked = settings.morseConfig.isFlashScreen,
                                                onCheckedChange = { dispatch(MainUiEvent.UpdateFlashScreen(it)) },
                                            )
                                        }
                                        MorseTorchConfig(
                                            enable = settings.morseConfig.isTorchEnabled,
                                            event = dispatch
                                        )
                                        ConfigCard {
                                            SwitchRow(
                                                title = stringResource(R.string.set_config_morse_sound),
                                                subtitle = stringResource(R.string.set_config_morse_sound_desc),
                                                checked = settings.morseConfig.isSoundEnabled,
                                                onCheckedChange = { dispatch(MainUiEvent.UpdateSoundEnabled(it)) },
                                            )
                                        }
                                        ConfigCard {
                                            SwitchRow(
                                                title = stringResource(R.string.set_config_morse_vibration),
                                                subtitle = stringResource(R.string.set_config_morse_vibration_desc),
                                                checked = settings.morseConfig.isVibrateEnabled,
                                                onCheckedChange = { dispatch(MainUiEvent.UpdateVibrateEnabled(it)) },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    (uiState as? MainUiState.Success)?.let { success ->

        SelectorFonts(
            settings = success.settings,
            onUpdateFontType = { dispatchAndClosePicker(MainUiEvent.UpdateFontType(it)) },
            showPanelFonts = showPanelFonts,
            dismissPanel = { showPanelFonts = false }
        )
    }
}

