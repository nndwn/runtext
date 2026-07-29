package com.nndwn.runtext.ui.features.main.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.runtext.R
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.utils.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextInputConfig(
    text: String,
    onTextChange: (String) -> Unit,
    onClearText: () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    modifier: Modifier = Modifier
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
                        "${text.length}/250",
                        color = if (text.length > 240) Palette.NeonRed else Palette.Black3,
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
