package com.nndwn.runtext.ui.features.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.AppMode
import com.nndwn.runtext.extentions.shimmer
import com.nndwn.runtext.ui.component.ColorPickerField
import com.nndwn.runtext.ui.component.ConfigCard
import com.nndwn.runtext.ui.component.ConfigCardSkeleton
import com.nndwn.runtext.ui.component.SwitchRow
import com.nndwn.runtext.ui.component.ThreeDotsHorizontal
import com.nndwn.runtext.ui.features.main.components.AppModeSettings
import com.nndwn.runtext.ui.features.main.components.AppModeSettingsSkeleton
import com.nndwn.runtext.ui.features.main.components.HeaderStartSideBar
import com.nndwn.runtext.ui.features.main.components.LogoText
import com.nndwn.runtext.ui.features.main.components.PreviewAndStart
import com.nndwn.runtext.ui.features.main.components.SelectorFonts
import com.nndwn.runtext.ui.features.main.components.SpeedConfig
import com.nndwn.runtext.ui.features.main.components.SpeedConfigSkeleton
import com.nndwn.runtext.ui.features.main.components.TextColorPickerConfig
import com.nndwn.runtext.ui.features.main.components.TextFontStyleConfig
import com.nndwn.runtext.ui.features.main.components.TextInputConfig
import com.nndwn.runtext.ui.features.main.components.TextInputConfigSkeleton
import com.nndwn.runtext.ui.features.main.components.TextOutlineConfig
import com.nndwn.runtext.ui.features.main.components.TextPresetConfig
import com.nndwn.runtext.ui.features.main.components.TextShadowConfig
import com.nndwn.runtext.ui.features.main.components.TextSpacingConfig
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.theme.toComposeColor
import com.nndwn.runtext.ui.utils.Dimens
import com.nndwn.runtext.ui.utils.LocalIsTablet

@Composable
fun MainScreen(
    viewModel : MainViewModel = hiltViewModel(),
    sideBarEnd: () -> Unit,
    onNavigateToDisplay: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        sideBarEnd = sideBarEnd,
        onNavigateToDisplay = onNavigateToDisplay
    )
}

@Composable
fun MainScreenContent(
    uiState: SettingsUiState,
    onEvent: (MainUiEvent) -> Unit,
    sideBarEnd: () -> Unit,
    onNavigateToDisplay: () -> Unit
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
            animation = tween(durationMillis = 1200, easing = LinearEasing),
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
            Scaffold(
                modifier = Modifier.width(300.dp),
                containerColor = Palette.Black3,
                topBar = { HeaderStartSideBar() }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    // Configuration UI for tablet could go here
                }
            }
        }

        // Main Content Area
        Scaffold() { innerPadding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                    )
                    .navigationBarsPadding()
                ,
                contentPadding = PaddingValues(
                    bottom = innerPadding.calculateBottomPadding(),
                    start = Dimens.PaddingHorizontal,
                    end = Dimens.PaddingHorizontal,
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.ArrangementHeight)

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
                    is SettingsUiState.Loading -> {
                        stickyHeader {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .shimmer(
                                        progress = shimmerProgress,
                                        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                                        shimmerColor = Palette.Grey,
                                        shape = RoundedCornerShape(Dimens.RoundedCorner)
                                    )
                            )
                        }
                        item {
                            TextInputConfigSkeleton(shimmerProgress = shimmerProgress)
                        }
                        item {
                            AppModeSettingsSkeleton(shimmerProgress = shimmerProgress)
                        }
                        item {
                            SpeedConfigSkeleton(shimmerProgress = shimmerProgress)
                        }
                        items(5) {
                            ConfigCardSkeleton(shimmerProgress = shimmerProgress)
                        }
                    }
                    is SettingsUiState.Success -> {
                        val settings = uiState.settings
                        stickyHeader {
                            PreviewAndStart(
                              settings = settings,
                              onNavigateToDisplay = onNavigateToDisplay
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
                                onModeChange = { dispatchAndClosePicker(MainUiEvent.UpdateMode(it)) }
                            )
                        }

                        item {
                            BoxWithConstraints(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clipToBounds()
                            ) {
                                val localMaxWidth = maxWidth
                                val density = LocalDensity.current
                                val widthPx = with(density) { localMaxWidth.toPx() }

                                val translationX by animateFloatAsState(
                                    targetValue = if (settings.mode == AppMode.RUNNING_TEXT) 0f else -widthPx,
                                    animationSpec = tween(
                                        durationMillis = 300,
                                        easing = FastOutSlowInEasing
                                    ),
                                    label = "modeSettingsTranslation"
                                )

                                Row(
                                    modifier = Modifier
                                        .wrapContentWidth(unbounded = true, align = Alignment.Start)
                                        .graphicsLayer {
                                            this.translationX = translationX
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier.width(localMaxWidth),
                                        verticalArrangement = Arrangement.spacedBy(Dimens.ArrangementHeight)
                                    ) {
                                       TextPresetConfig(
                                           expandedId = expandedPickerId,
                                           onToggle = togglePicker,
                                           onEvent = dispatch
                                       )
                                        SpeedConfig(
                                            speed = settings.speed,
                                            onSpeedChange = { dispatch(MainUiEvent.UpdateSpeed(it)) }
                                        )
                                        ConfigCard {
                                            SwitchRow(
                                                title = stringResource(R.string.set_config_text_mirror),
                                                subtitle = stringResource(R.string.set_config_text_mirror_desc),
                                                checked = settings.isMirrorMode,
                                                onCheckedChange = { dispatchAndClosePicker(MainUiEvent.UpdateMirrorMode(it)) },
                                                accentColor = Palette.Yellow
                                            )
                                        }

                                        TextFontStyleConfig(
                                            config = settings.textStyle,
                                            onClick = {
                                                closePicker(); showPanelFonts = !showPanelFonts
                                            }
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
                                                onToggleExpand =  { togglePicker("bg") },
                                                onColorChange = {dispatch(MainUiEvent.UpdateBgColor(it)) }
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
                                    Box(
                                        modifier = Modifier.width(localMaxWidth)
                                    ) {
                                        Text("Morse Code Settings Content", color = Palette.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            
            (uiState as? SettingsUiState.Success)?.let { success ->

                SelectorFonts(
                    settings = success.settings,
                    onUpdateFontType = { dispatchAndClosePicker(MainUiEvent.UpdateFontType(it)) },
                    showPanelFonts = showPanelFonts,
                    dismissPanel = { showPanelFonts = false }
                )
            }
        }
    }
}

