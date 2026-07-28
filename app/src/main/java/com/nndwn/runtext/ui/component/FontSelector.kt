package com.nndwn.runtext.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.runtext.data.model.FontType
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.theme.NeonGreen
import com.nndwn.runtext.ui.theme.googleFontFamily

/**
 * Dropdown font selector showing a curated list of fonts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSelector(
    selectedFont: FontType,
    onFontSelected: (FontType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            OutlinedTextField(
                value = selectedFont.displayName,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonGreen,
                    unfocusedBorderColor = Palette.Grey.copy(alpha = 0.5f)
                ),
                textStyle = TextStyle(
                    fontFamily = googleFontFamily(selectedFont.name.replace("_", " ")),
                    fontSize = 16.sp,
                    color = Palette.White
                ),
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = Palette.Black3,
            ) {
                FontType.entries.forEach { fontType ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = fontType.displayName,
                                style = TextStyle(
                                    fontFamily = googleFontFamily(fontType.name.replace("_", " ")),
                                    fontSize = 16.sp,
                                    color = Palette.White
                                ),
                            )
                        },
                        onClick = {
                            onFontSelected(fontType)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

/** Resolve [FontType] to the matching Compose [FontFamily]. */
fun fontFamilyFor(fontType: FontType) = googleFontFamily(fontType.name.replace("_", " "))
