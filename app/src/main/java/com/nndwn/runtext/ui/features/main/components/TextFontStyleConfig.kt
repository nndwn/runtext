package com.nndwn.runtext.ui.features.main.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.AppSettings
import com.nndwn.runtext.data.model.FontType
import com.nndwn.runtext.data.model.TextStyleConfig
import com.nndwn.runtext.ui.component.ConfigCard
import com.nndwn.runtext.ui.component.SlideUpPanel
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.utils.fontFamilyFor


@Composable
fun TextFontStyleConfig(
    config: TextStyleConfig,
    onClick : () -> Unit,
){
    ConfigCard (
        modifier = Modifier
            .clickable {
                onClick()
            }
    ){
        Text(stringResource(R.string.set_config_text_style), style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = config.fontType.displayName,
            fontFamily = fontFamilyFor(config.fontType),
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp
        )
    }
}

@Composable
fun SelectorFonts(
    settings: AppSettings,
    onUpdateFontType : (FontType) -> Unit,
    showPanelFonts : Boolean,
    dismissPanel : () -> Unit
){
    SlideUpPanel(
        visible = showPanelFonts,
        enableDragToDismiss = true,
        onDismiss = dismissPanel
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp)
                .navigationBarsPadding()
            ,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                color = Palette.Black2,
                text = stringResource(R.string.set_config_text_style  ),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 0.5.dp,
                color = Palette.Black3.copy(alpha = 0.1f)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(FontType.entries.size) { index ->
                    val item = FontType.entries[index]
                    val isSelected = settings.textStyle.fontType == item
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                indication = ripple(),
                                interactionSource = remember { MutableInteractionSource() },
                                onClick = {
                                    onUpdateFontType(item)
                                    dismissPanel()
                                }
                            )
                            .padding(vertical = 16.dp, horizontal = 24.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = item.displayName,
                            fontWeight =if (isSelected) FontWeight.Bold else FontWeight.Normal ,
                            style = TextStyle(
                                fontFamily = fontFamilyFor(item),
                                fontSize = 18.sp,
                                color = Palette.Black2
                            )
                        )
                    }
                }

            }
        }
    }
}