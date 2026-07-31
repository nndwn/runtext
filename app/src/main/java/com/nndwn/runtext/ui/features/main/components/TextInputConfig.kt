package com.nndwn.runtext.ui.features.main.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.runtext.R
import com.nndwn.runtext.extentions.shimmer
import com.nndwn.runtext.ui.RunTextApp
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.theme.RuntextTheme
import com.nndwn.runtext.ui.utils.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputConfig(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    onClearText: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    BasicTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier
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
                value = text,
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
                    if (text.isNotEmpty()) {
                        IconButton(onClick = onClearText) {
                            Icon(
                                painterResource(R.drawable.ic_clear),
                                contentDescription = "Clear",
                                tint = Palette.White,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }
                },
                supportingText = {
                    Text(
                        text = "${text.length}/250",
                        color = if (text.length > 240) Palette.NeonRed else Palette.Black3,
                        style = MaterialTheme.typography.bodySmall
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

@Composable
fun TextInputConfigSkeleton(
    modifier: Modifier = Modifier,
    shimmerProgress: Float
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .heightIn(min = 120.dp)
                .shimmer(
                    progress = shimmerProgress,
                    backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                    shimmerColor = Palette.Grey,
                    shape = RoundedCornerShape(Dimens.RoundedCorner)
                )
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "0/250",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Transparent,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 15.dp)
                .shimmer(
                    progress = shimmerProgress,
                    backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                    shimmerColor = Palette.Grey,
                    shape = RoundedCornerShape(4.dp)
                )
        )
    }
}

@Preview
@Composable
private fun Preview() {
    RuntextTheme {

        val transition = rememberInfiniteTransition(label = "SharedShimmerTransition")
        val shimmerProgress by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "SharedShimmerProgress"
        )
        Column() {
            TextInputConfig(
                modifier = Modifier.padding(16.dp),
                text = "",
                onTextChange = {},
                onClearText = {}
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextInputConfigSkeleton(
                modifier = Modifier.padding(16.dp),
                shimmerProgress = shimmerProgress
            )

        }
    }
}