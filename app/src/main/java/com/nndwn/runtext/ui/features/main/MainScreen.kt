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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.nndwn.runtext.extentions.WindowSize
import com.nndwn.runtext.ui.component.ColorPickerField
import com.nndwn.runtext.ui.component.ConfigCard
import com.nndwn.runtext.ui.component.MenuOptions
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
import com.nndwn.runtext.ui.utils.LocalMenuOptionHandler
import com.nndwn.runtext.ui.utils.LocalToggleSidebar
import com.nndwn.runtext.ui.utils.LocalWindowSize

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    padding: PaddingValues,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toggleSidebar = LocalToggleSidebar.current
    val onMenuSelected = LocalMenuOptionHandler.current
    MainScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        sideBarEnd = toggleSidebar,
        padding = padding,
        limitText = viewModel.limitText,
        menus = onMenuSelected
    )
}

@Composable
fun MainScreenContent(
    padding: PaddingValues,
    uiState: MainUiState,
    onEvent: (MainUiEvent) -> Unit,
    limitText: Int = 100,
    menus : (MenuOptions) -> Unit,
    sideBarEnd: () -> Unit,
) {
    val windowSize = LocalWindowSize.current
    val focusManager = LocalFocusManager.current
    var expandedPickerId by remember { mutableStateOf<String?>(null) }
    var showPanelFonts by remember { mutableStateOf(false) }

    val closePicker = { expandedPickerId = null }

    val togglePicker: (String) -> Unit = { id ->
        expandedPickerId =  if (expandedPickerId == id) null else id
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

    val listState = rememberLazyListState()

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            focusManager.clearFocus()
        }
    }
    if (uiState is MainUiState.Success) {
        LaunchedEffect(uiState.settings.mode) {
            expandedPickerId = when (uiState.settings.mode) {
                AppMode.MORSE_CODE -> "morse_color"
                AppMode.RUNNING_TEXT -> "text_presets"
            }
        }
    }



    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding)
    ) {
        if ( windowSize == WindowSize.EXPAND || windowSize == WindowSize.PHONE_LANDSCAPE){
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(250.dp)

                ,
                color = MaterialTheme.colorScheme.secondary
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    LogoText(
                        modifier = Modifier
                            .padding(vertical = MaterialTheme.dimens.medium , horizontal = MaterialTheme.dimens.small)
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small))
                    if (windowSize == WindowSize.PHONE_LANDSCAPE && uiState is MainUiState.Success) {
                        PreviewAndStart(
                            settings = uiState.settings,
                            modifier = Modifier.padding(horizontal = MaterialTheme.dimens.small),
                            onNavigateToDisplay = { dispatch(MainUiEvent.NavigateToDisplay) }

                        )
                    }
                    if (windowSize != WindowSize.PHONE_LANDSCAPE){
                        MenuOptions (
                            onMenuSelected = menus
                        )
                    }

                }
            }

        }


        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(
                bottom = padding.calculateBottomPadding(),
                start = MaterialTheme.dimens.medium,
                end = MaterialTheme.dimens.medium,

                ),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium)

        ) {
            item {
                AnimatedVisibility(
                    visible = windowSize == WindowSize.PHONE_PORTRAIT || windowSize == WindowSize.TABLET_PORTRAIT,
                    enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                ) {
                    LogoText(
                        modifier = Modifier
                            .padding(vertical = MaterialTheme.dimens.small),
                        content = {
                            ThreeDotsHorizontal(onClick = { sideBarEnd() })
                        }
                    )
                }
            }
            when (uiState) {
                is MainUiState.Loading -> {
                    if (windowSize != WindowSize.PHONE_LANDSCAPE) {
                        stickyHeader {
                            Skeleton(
                                shimmerProgress = shimmerProgress,
                                height = 140.dp
                            )
                        }
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
                    if (windowSize != WindowSize.PHONE_LANDSCAPE) {
                        stickyHeader {
                            PreviewAndStart(
                                settings = settings,
                                onNavigateToDisplay = { dispatch(MainUiEvent.NavigateToDisplay) }
                            )
                        }
                    }

                    item {
                        TextInputConfig(
                            text = settings.lastText,
                            onTextChange = { dispatch(MainUiEvent.UpdateText(it)) },
                            onClearText = { dispatch(MainUiEvent.ClearText) },
                            limitText = limitText
                        )
                    }

                    item {
                        AppModeSettings(
                            currentMode = settings.mode,
                            onModeChange = {
                                dispatch(MainUiEvent.UpdateMode(it))
                            }
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
                                            speed = settings.textConfig.speed,
                                            onSpeedChange = { dispatch(MainUiEvent.UpdateSpeed(it)) }
                                        )
                                        ConfigCard {
                                            SwitchRow(
                                                title = stringResource(R.string.set_config_text_mirror),
                                                subtitle = stringResource(R.string.set_config_text_mirror_desc),
                                                checked = settings.textConfig.isMirrorMode,
                                                onCheckedChange = {
                                                    dispatchAndClosePicker(
                                                        MainUiEvent.UpdateMirrorMode(it)
                                                    )
                                                },
                                            )
                                        }
                                        TextFontStyleConfig(
                                            config = settings.textConfig.textStyle,
                                            onClick = {
                                                closePicker(); showPanelFonts = !showPanelFonts
                                            }
                                        )
                                        TextSpacingConfig(
                                            config = settings.textConfig.textStyle,
                                            expandedId = expandedPickerId,
                                            onToggle = togglePicker,
                                            onEvent = dispatch
                                        )
                                        ConfigCard {
                                            ColorPickerField(
                                                label = stringResource(R.string.set_config_color_background),
                                                color = settings.textConfig.bgColorArgb.toComposeColor(),
                                                isExpanded = expandedPickerId == "bg",
                                                onToggleExpand = { togglePicker("bg") },
                                                onColorChange = {
                                                    dispatch(
                                                        MainUiEvent.UpdateBgColor(
                                                            it
                                                        )
                                                    )
                                                }
                                            )
                                        }
                                        TextColorPickerConfig(
                                            label = stringResource(R.string.set_config_text_color_text),
                                            config = settings.textConfig.textStyle,
                                            expandedPickerId = expandedPickerId,
                                            onPickerToggle = togglePicker,
                                            onEvent = dispatch
                                        )
                                        TextOutlineConfig(
                                            config = settings.textConfig.stroke,
                                            expandedPickerId = expandedPickerId,
                                            onPickerToggle = togglePicker,
                                            onEvent = dispatch
                                        )
                                        TextShadowConfig(
                                            config = settings.textConfig.shadow,
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
                                                onCheckedChange = {
                                                    dispatch(
                                                        MainUiEvent.UpdateFlashScreen(
                                                            it
                                                        )
                                                    )
                                                },
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
                                                onCheckedChange = {
                                                    dispatch(
                                                        MainUiEvent.UpdateSoundEnabled(
                                                            it
                                                        )
                                                    )
                                                },
                                            )
                                        }
                                        ConfigCard {
                                            SwitchRow(
                                                title = stringResource(R.string.set_config_morse_vibration),
                                                subtitle = stringResource(R.string.set_config_morse_vibration_desc),
                                                checked = settings.morseConfig.isVibrateEnabled,
                                                onCheckedChange = {
                                                    dispatch(
                                                        MainUiEvent.UpdateVibrateEnabled(
                                                            it
                                                        )
                                                    )
                                                },
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

