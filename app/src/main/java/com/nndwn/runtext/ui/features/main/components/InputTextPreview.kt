package com.nndwn.runtext.ui.features.main.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.AppMode
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.ui.component.fontFamilyFor
import com.nndwn.runtext.ui.component.fontWeightFor
import com.nndwn.runtext.ui.theme.NeonRed
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.theme.toComposeColor
import com.nndwn.runtext.ui.utils.Dimens
import kotlinx.coroutines.isActive
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputTextPreview(
    modifier: Modifier = Modifier,
    settings: AppSettings,
    onUpdateText: (String) -> Unit,
    onClearText: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Column(modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.ArrangementHeight)) {
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
                            color = if (settings.lastText.length > 240) NeonRed else Palette.Grey,
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Palette.White,
                        unfocusedBorderColor = Palette.Grey.copy(alpha = 0.5f),
                    ),
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = true,
                            isError = false,
                            interactionSource = interactionSource,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Palette.White,
                                unfocusedBorderColor = Palette.Grey.copy(alpha = 0.5f),
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
}

@Composable
private fun RunningTextPreview(settings: AppSettings) {
    val text = settings.lastText.ifEmpty { "PREVIEW" }
    val textMeasurer = rememberTextMeasurer()

    val fontFamily = fontFamilyFor(settings.fontType, settings.googleFontName)
    val fontWeight = fontWeightFor(settings.fontType)

    // RTL detection for consistency
    val isRtl = remember(text) {
        if (text.isEmpty()) false
        else {
            val bidi = java.text.Bidi(
                text,
                java.text.Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT,
            )
            bidi.isRightToLeft
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.CenterStart
    ) {
        val containerWidthPx = constraints.maxWidth.toFloat()

        val textStyle = TextStyle(
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontSize = 40.sp,
            color = settings.textColorArgb.toComposeColor(),
        )

        val textLayoutResult = remember(text, fontFamily, fontWeight) {
            textMeasurer.measure(
                text = text,
                style = textStyle,
                maxLines = 1,
                softWrap = false,
            )
        }

        val textWidth = textLayoutResult.size.width.toFloat()
        
        val startX = if (isRtl) -textWidth else containerWidthPx
        val offsetX = remember { Animatable(startX) }

        LaunchedEffect(settings.speed, textWidth, containerWidthPx, isRtl) {
            val sX = if (isRtl) -textWidth else containerWidthPx
            val eX = if (isRtl) containerWidthPx else -textWidth
            val dist = abs(eX - sX)
            
            val dur = ((dist / settings.speed.coerceAtLeast(1f)) * 1000).toInt().coerceAtLeast(500)

            offsetX.snapTo(sX)
            while (isActive) {
                offsetX.animateTo(
                    targetValue = eX,
                    animationSpec = tween(durationMillis = dur, easing = LinearEasing),
                )
                offsetX.snapTo(sX)
            }
        }

        Text(
            text = text,
            style = textStyle,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier
                .wrapContentWidth(unbounded = true, align = Alignment.Start)
                .graphicsLayer {
                    translationX = offsetX.value
                    rotationY = if (settings.isMirrorMode) 180f else 0f
                }
        )
    }
}

@Composable
private fun MorseFlashPreview(settings: AppSettings) {
    val infiniteTransition = rememberInfiniteTransition(label = "morse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flash"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                settings.textColorArgb
                    .toComposeColor()
                    .copy(alpha = if (settings.isFlashScreen) alpha else 1f)
            )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF121219)
@Composable
private fun InputTextPreview_Simulation() {
    // مرحباً، هذا اختبار للنص المتحرك
    var settings by remember { 
        mutableStateOf(
            AppSettings(
                lastText = "",
                mode = AppMode.RUNNING_TEXT,
                speed = 200f
            )
        ) 
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Interactive Prototype", color = Palette.White, fontWeight = FontWeight.Bold)
        InputTextPreview(
            settings = settings,
            onUpdateText = { settings = settings.copy(lastText = it) },
            onClearText = { settings = settings.copy(lastText = "") }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { settings = settings.copy(mode = AppMode.RUNNING_TEXT) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(settings.mode == AppMode.RUNNING_TEXT) Palette.White else Palette.Grey
                )
            ) {
                Text("Run Mode", color = Color.Black)
            }
            
            Button(
                onClick = { settings = settings.copy(mode = AppMode.MORSE_CODE) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(settings.mode == AppMode.MORSE_CODE) Palette.White else Palette.Grey
                )
            ) {
                Text("Morse Mode", color = Color.Black)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = settings.isMirrorMode,
                onCheckedChange = { settings = settings.copy(isMirrorMode = it) }
            )
            Text("Mirror Mode", color = Palette.White)
        }
    }
}
