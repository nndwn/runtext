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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.AppMode
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.ui.features.main.components.Header
import com.nndwn.runtext.ui.features.main.components.InputTextPreview
import com.nndwn.runtext.ui.theme.NeonCyan
import com.nndwn.runtext.ui.theme.NeonGreen
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.utils.Dimens
import com.nndwn.runtext.ui.utils.LocalIsTablet

@Composable
fun MainScreen(
    settings: AppSettings,
    onUpdateText: (String) -> Unit,
    onClearText: () -> Unit,
    onUpdateMode: (AppMode) -> Unit,
    onMenuClick: () -> Unit,
    onUpdateSpeed : (Float) -> Unit

){
    val isTablet = LocalIsTablet.current
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
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(
                        horizontal = Dimens.PaddingHorizontal,
                        vertical = Dimens.ArrangementHeight
                    ),
                verticalArrangement = Arrangement.spacedBy(Dimens.ArrangementHeight)
            ) {
                InputTextPreview(
                    settings = settings,
                    modifier = Modifier,
                    onUpdateText = onUpdateText,
                    onClearText = onClearText
                )
                SingleChoiceSegmentedButtonRow(modifier = Modifier
                    .fillMaxWidth()) {
                    val customColors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Palette.Black3,
                        activeContentColor = Palette.White,
                        activeBorderColor = Palette.Black3,
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
                    ) { Text( text = stringResource(R.string.btn_text_running_text)) }

                    SegmentedButton(
                        selected = settings.mode == AppMode.MORSE_CODE,
                        onClick = { onUpdateMode(AppMode.MORSE_CODE) },
                        colors = customColors,
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        icon = { Icon(Icons.Default.FlashOn, null, Modifier.size(18.dp)) },
                    ) { Text(text = stringResource(R.string.btn_text_morse_code)) }
                }

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
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
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
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Speed", style = MaterialTheme.typography.titleSmall)
                                    Text("${settings.speed.toInt()} px/s", style = MaterialTheme.typography.bodySmall, color = NeonCyan)
                                }
                                Slider(
                                    value = settings.speed,
                                    onValueChange = onUpdateSpeed,
                                    valueRange = 50f..500f,
                                    colors = SliderDefaults.colors(thumbColor = NeonGreen, activeTrackColor = NeonGreen),
                                )
                            }
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

@Composable
fun ConfigCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}
