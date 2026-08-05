package com.nndwn.runtext.ui.features.main.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.R
import com.nndwn.runtext.ui.theme.dimens

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
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        IconButton(onClick = onClearText) {
                            Icon(
                                painterResource(R.drawable.ic_clear),
                                contentDescription = stringResource(R.string.clear),
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
                            )
                        }
                    }
                },
                supportingText = {
                    Text(
                        text = "${text.length}/250",
                        color = if (text.length > 240) MaterialTheme.colorScheme.error
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = true,
                        isError = false,
                        interactionSource = interactionSource,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        shape = MaterialTheme.shapes.medium,
                        focusedBorderThickness = MaterialTheme.dimens.borderMedium,
                        unfocusedBorderThickness = MaterialTheme.dimens.borderSmall
                    )
                }
            )
        }
    )
}

