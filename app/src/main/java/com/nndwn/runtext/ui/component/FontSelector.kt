package com.nndwn.runtext.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.runtext.data.model.FontType
import com.nndwn.runtext.ui.theme.AntonFamily
import com.nndwn.runtext.ui.theme.DotGothic16Family
import com.nndwn.runtext.ui.theme.NeonGreen
import com.nndwn.runtext.ui.theme.JakartaPlusFamily
import com.nndwn.runtext.ui.theme.ShareTechMonoFamily
import com.nndwn.runtext.ui.theme.googleFontFamily

/**
 * Dropdown font selector showing bundled fonts with preview text.
 * When [FontType.GOOGLE_FONT] is selected, an extra text field appears for the font name.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSelector(
    selectedFont: FontType,
    googleFontName: String,
    onFontSelected: (FontType) -> Unit,
    onGoogleFontNameChanged: (String) -> Unit,
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
                ),
                textStyle = TextStyle(
                    fontFamily = fontFamilyFor(selectedFont, googleFontName),
                    fontSize = 16.sp,
                ),
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                FontType.entries.forEach { fontType ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = fontType.displayName,
                                style = TextStyle(
                                    fontFamily = fontFamilyFor(fontType, googleFontName),
                                    fontWeight = fontWeightFor(fontType),
                                    fontSize = 16.sp,
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

        // Google Font name input (shown only when GOOGLE_FONT is selected)
        AnimatedVisibility(visible = selectedFont == FontType.GOOGLE_FONT) {
            Column {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = googleFontName,
                    onValueChange = onGoogleFontNameChanged,
                    label = { Text("Google Font name") },
                    placeholder = { Text("e.g. Lobster, Press Start 2P") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                    ),
                )
                Text(
                    text = "Requires internet connection",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** Resolve [FontType] to the matching Compose [FontFamily]. */
internal fun fontFamilyFor(fontType: FontType, googleFontName: String) = when (fontType) {
    FontType.SHARE_TECH_MONO -> ShareTechMonoFamily
    FontType.DOT_GOTHIC      -> DotGothic16Family
    FontType.JAKARTAPLUSBOLD     -> JakartaPlusFamily
    FontType.JAKARTAPLUSLIGHT -> JakartaPlusFamily
    FontType.ANTON            -> AntonFamily
    FontType.GOOGLE_FONT      -> if (googleFontName.isNotBlank()) googleFontFamily(googleFontName) else JakartaPlusFamily
}

internal fun fontWeightFor(fontType: FontType) = when (fontType) {
    FontType.JAKARTAPLUSBOLD -> FontWeight.Bold
    FontType.JAKARTAPLUSLIGHT -> FontWeight.Light
    else                     -> FontWeight.Normal
}
