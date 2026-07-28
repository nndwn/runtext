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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.AppMode
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.data.model.FontType
import com.nndwn.runtext.ui.features.main.components.ColorPickerConfig
import com.nndwn.runtext.ui.features.main.components.ConfigCard
import com.nndwn.runtext.ui.features.main.components.Header
import com.nndwn.runtext.ui.features.main.components.MorseFlashPreview
import com.nndwn.runtext.ui.features.main.components.RunningTextPreview
import com.nndwn.runtext.ui.features.main.components.SelectorFonts
import com.nndwn.runtext.ui.features.main.components.SpeedConfig
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.theme.toComposeColor
import com.nndwn.runtext.ui.utils.Dimens
import com.nndwn.runtext.ui.utils.LocalIsTablet
import com.nndwn.runtext.ui.utils.fontFamilyFor

@Composable
fun MainScreen(
    settings: AppSettings,
    onUpdateText: (String) -> Unit,
    onClearText: () -> Unit,
    onUpdateMode: (AppMode) -> Unit,
    onMenuClick: () -> Unit,
    onUpdateSpeed: (Float) -> Unit,
    onUpdateBackgroundColor: (Long) -> Unit,
    onUpdateTextColor: (Long) -> Unit,
    onUpdateFontType: (FontType) -> Unit

) {
    val isTablet = LocalIsTablet.current
    var isBgColorPickerExpanded by remember { mutableStateOf(false) }
    var isTextColorPickerExpanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    var showPanelFonts by remember { mutableStateOf(false) }

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
            },
            floatingActionButton = {

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
                    BasicTextField(
                        value = settings.lastText,
                        onValueChange = onUpdateText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        interactionSource = interactionSource,
                        textStyle = TextStyle(
                            color = Palette.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal
                        ),
                        cursorBrush = SolidColor(Palette.White),
                        decorationBox = { innerTextField ->
                            OutlinedTextFieldDefaults.DecorationBox(
                                value = settings.lastText,
                                innerTextField = innerTextField,
                                enabled = true,
                                singleLine = false,
                                visualTransformation = VisualTransformation.None,
                                interactionSource = interactionSource,
                                placeholder = {
                                    Text(
                                        stringResource(R.string.placeholder_input_text),
                                        color = Palette.Grey,
                                    )
                                },
                                trailingIcon = {
                                    if (settings.lastText.isNotEmpty()) {
                                        IconButton(onClick = onClearText) {
                                            Icon(Icons.Default.Clear, "Clear", tint = Palette.White)
                                        }
                                    }
                                },
                                supportingText = {
                                    Text(
                                        "${settings.lastText.length}/250",
                                        color = if (settings.lastText.length > 240) Palette.NeonRed else Palette.Black3,
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Palette.White,
                                    unfocusedBorderColor = Palette.Black3,
                                ),
                                container = {
                                    OutlinedTextFieldDefaults.Container(
                                        enabled = true,
                                        isError = false,
                                        interactionSource = interactionSource,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Palette.White,
                                            unfocusedBorderColor = Palette.Black3,
                                        ),
                                        shape = RoundedCornerShape(Dimens.RoundedCorner),
                                        focusedBorderThickness = 1.dp,
                                        unfocusedBorderThickness = 0.8.dp
                                    )
                                }
                            )
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(Dimens.ArrangementHeight))
                }
                item {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val customColors = SegmentedButtonDefaults.colors(
                            activeContainerColor = Palette.Black4,
                            activeContentColor = Palette.White,
                            activeBorderColor = Palette.Grey,
                            inactiveContainerColor = Color.Transparent,
                            inactiveContentColor = Palette.White,
                            inactiveBorderColor = Palette.Grey.copy(alpha = 0.5f)
                        )
                        SegmentedButton(
                            selected = settings.mode == AppMode.RUNNING_TEXT,
                            onClick = { onUpdateMode(AppMode.RUNNING_TEXT) },
                            colors = customColors,
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                            icon = { Icon(Icons.Default.TextFields, null, Modifier.size(18.dp)) },
                        ) { Text(text = stringResource(R.string.btn_text_running_text)) }

                        SegmentedButton(
                            selected = settings.mode == AppMode.MORSE_CODE,
                            onClick = { onUpdateMode(AppMode.MORSE_CODE) },
                            colors = customColors,
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                            icon = { Icon(Icons.Default.FlashOn, null, Modifier.size(18.dp)) },
                        ) { Text(text = stringResource(R.string.btn_text_morse_code)) }
                    }
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
                                ConfigCard {
                                    Text(stringResource(R.string.set_config_text_style), style = MaterialTheme.typography.titleSmall)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(
                                                interactionSource = interactionSource,
                                                indication = ripple(),
                                                onClick = { showPanelFonts = !showPanelFonts }
                                            )
                                    ){
                                        Text(
                                            text = settings.fontType.displayName,
                                            fontFamily = fontFamilyFor(settings.fontType),
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 20.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(Dimens.ArrangementHeight))
                                SpeedConfig(
                                    speed = settings.speed,
                                    onSpeedChange = onUpdateSpeed
                                )
                                Spacer(modifier = Modifier.height(Dimens.ArrangementHeight))
                                ColorPickerConfig(
                                    label = stringResource(R.string.set_config_color_background),
                                    currentValue = settings.bgColorArgb,
                                    isExpanded = isBgColorPickerExpanded,
                                    onToggleExpand = {
                                        isBgColorPickerExpanded = !isBgColorPickerExpanded
                                        if (isBgColorPickerExpanded) isTextColorPickerExpanded = false
                                    },
                                    onColorChange = onUpdateBackgroundColor
                                )
                                Spacer(modifier = Modifier.height(Dimens.ArrangementHeight))
                                ColorPickerConfig(
                                    label = stringResource(R.string.set_config_text_color_text),
                                    currentValue = settings.textColorArgb,
                                    isExpanded = isTextColorPickerExpanded,
                                    onToggleExpand = {
                                        isTextColorPickerExpanded = !isTextColorPickerExpanded
                                        if (isTextColorPickerExpanded) isBgColorPickerExpanded = false
                                    },
                                    onColorChange = onUpdateTextColor
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
               onUpdateFontType = onUpdateFontType,
               showPanelFonts = showPanelFonts,
               dismissPanel = { showPanelFonts = false }
           )
        }
    }
}

