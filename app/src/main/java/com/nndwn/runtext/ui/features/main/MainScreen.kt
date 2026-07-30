package com.nndwn.runtext.ui.features.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.AppMode
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.ui.component.ConfigCard
import com.nndwn.runtext.ui.features.main.components.AppModeSettings
import com.nndwn.runtext.ui.features.main.components.ColorPickerConfig
import com.nndwn.runtext.ui.features.main.components.Header
import com.nndwn.runtext.ui.features.main.components.MorseFlashPreview
import com.nndwn.runtext.ui.features.main.components.RunningTextPreview
import com.nndwn.runtext.ui.features.main.components.SelectorFonts
import com.nndwn.runtext.ui.features.main.components.SpeedConfig
import com.nndwn.runtext.ui.features.main.components.TextColorPickerConfig
import com.nndwn.runtext.ui.features.main.components.TextInputConfig
import com.nndwn.runtext.ui.features.main.components.TextOutlineConfig
import com.nndwn.runtext.ui.features.main.components.TextShadowConfig
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.theme.toComposeColor
import com.nndwn.runtext.ui.utils.Dimens
import com.nndwn.runtext.ui.utils.LocalIsTablet
import com.nndwn.runtext.ui.utils.fontFamilyFor

@Composable
fun MainScreen(
    settings: AppSettings,
    onEvent: (MainUiEvent) -> Unit,
    onMenuClick: () -> Unit
) {
    val isTablet = LocalIsTablet.current
    var expandedPickerId by remember { mutableStateOf<String?>(null) }
    val interactionSource = remember { MutableInteractionSource() }
    var showPanelFonts by remember { mutableStateOf(false) }
    
    val closePicker = { expandedPickerId = null }

    val dispatch: (MainUiEvent) -> Unit = { event ->
        onEvent(event)
    }

    val dispatchAndClosePicker: (MainUiEvent) -> Unit = { event ->
        closePicker()
        onEvent(event)
    }

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
                topBar = { Header() }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    // Configuration UI for tablet could go here
                }
            }
        }

        // Main Content Area
        Scaffold(
            topBar = {
                AnimatedVisibility(
                    visible = !isTablet,
                    enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                ) {
                    Header(
                        withSidebar = true,
                        onMenuClick = onMenuClick
                    )
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(
                        horizontal = Dimens.PaddingHorizontal,
                        vertical = Dimens.ArrangementHeight
                    )

            ) {
                item {
                    Spacer(modifier = Modifier.height(Dimens.ArrangementHeight))
                }
                stickyHeader {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(Dimens.RoundedCorner))
                            .background(settings.bgColorArgb.toComposeColor()),
                        contentAlignment = Alignment.Center
                    ) {
                        if (settings.mode == AppMode.RUNNING_TEXT) {
                            RunningTextPreview(settings)
                        } else {
                            MorseFlashPreview(settings)
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(Dimens.ArrangementHeight))
                }
                item {
                    TextInputConfig(
                        text = settings.lastText,
                        onTextChange = { dispatchAndClosePicker(MainUiEvent.UpdateText(it)) },
                        onClearText = { dispatchAndClosePicker(MainUiEvent.ClearText) },
                        interactionSource = interactionSource
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(Dimens.ArrangementHeight))
                }
                item {
                    AppModeSettings(
                        currentMode = settings.mode,
                        onModeChange = { dispatchAndClosePicker(MainUiEvent.UpdateMode(it)) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(Dimens.ArrangementHeight))
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
                                modifier = Modifier.width(localMaxWidth)
                            ) {
                                SpeedConfig(
                                    speed = settings.speed,
                                    onSpeedChange = { dispatch(MainUiEvent.UpdateSpeed(it)) }
                                )
                                Spacer(modifier = Modifier.height(Dimens.ArrangementHeight))
                                ConfigCard {
                                    Text(stringResource(R.string.set_config_text_style), style = MaterialTheme.typography.titleSmall)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                closePicker(); showPanelFonts = !showPanelFonts
                                            }
                                    ) {
                                        Text(
                                            text = settings.textStyle.fontType.displayName,
                                            fontFamily = fontFamilyFor(settings.textStyle.fontType),
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 20.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(Dimens.ArrangementHeight))
                                ColorPickerConfig(
                                    label = stringResource(R.string.set_config_color_background),
                                    currentValue = settings.bgColorArgb,
                                    isExpanded = expandedPickerId == "bg",
                                    onToggleExpand = {
                                        expandedPickerId = if (expandedPickerId == "bg") null else "bg"
                                    },
                                    onColorChange = {dispatch(MainUiEvent.UpdateBgColor(it)) }
                                )
                                Spacer(modifier = Modifier.height(Dimens.ArrangementHeight))
                                TextColorPickerConfig(
                                    label = stringResource(R.string.set_config_text_color_text),
                                    config = settings.textStyle,
                                    expandedPickerId = expandedPickerId,
                                    onPickerToggle = { id -> expandedPickerId = if (expandedPickerId == id) null else id },
                                    onEvent = dispatch
                                )
                                Spacer(modifier = Modifier.height(Dimens.ArrangementHeight))
                                TextOutlineConfig(
                                    config = settings.stroke,
                                    expandedPickerId = expandedPickerId,
                                    onPickerToggle = { id -> expandedPickerId = if (expandedPickerId == id) null else id },
                                    onEvent = dispatch
                                )
                                Spacer(modifier = Modifier.height(Dimens.ArrangementHeight))
                                TextShadowConfig(
                                    config = settings.shadow,
                                    expandedPickerId = expandedPickerId,
                                    onPickerToggle = { id ->
                                        expandedPickerId = if (expandedPickerId == id) null else id
                                    },
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
           SelectorFonts(
               settings = settings,
               onUpdateFontType = { dispatchAndClosePicker(MainUiEvent.UpdateFontType(it)) },
               showPanelFonts = showPanelFonts,
               dismissPanel = { showPanelFonts = false }
           )
        }
    }
}

