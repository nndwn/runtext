package com.nndwn.runtext.ui.features.main.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.TextStyleConfig
import com.nndwn.runtext.ui.component.ConfigCard
import com.nndwn.runtext.ui.component.LabeledSlider
import com.nndwn.runtext.ui.features.main.MainUiEvent
import com.nndwn.runtext.ui.theme.RuntextTheme
import kotlin.math.roundToInt

@Composable
fun TextSpacingConfig(
    modifier: Modifier = Modifier,
    config : TextStyleConfig,
    expandedPickerId: String?,
    onPickerToggle : (String) -> Unit,
    onEvent : (MainUiEvent) -> Unit,
){
    val isExpanded = expandedPickerId == "text_spacing"
    ConfigCard(modifier = modifier) {
        Column(modifier = Modifier.animateContentSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ){
                        onPickerToggle("text_spacing")
                    }
                ,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.set_config_text_spacing),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                )
                Icon(
                    painter = if (isExpanded)painterResource(R.drawable.ic_up)  else  painterResource(R.drawable.ic_down),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.size(30.dp)
                )

            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    LabeledSlider(
                        label = stringResource(R.string.set_config_text_spacing_text),
                        value = config.letterSpacingSp,
                        valueRange = -2f..20f,
                        displayValueText = "${config.letterSpacingSp.roundToInt()}",
                        onValueChange = { onEvent(MainUiEvent.UpdateLetterSpacing(it)) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LabeledSlider(
                        label = stringResource(R.string.set_config_text_spacing_word),
                        value = config.wordSpacingSp,
                        valueRange = 0f..30f,
                        displayValueText = "${config.wordSpacingSp.roundToInt()}",
                        onValueChange = { onEvent(MainUiEvent.UpdateWordSpacing(it)) }
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun Preview(){
    RuntextTheme {
        TextSpacingConfig(
            config = TextStyleConfig(
                letterSpacingSp = 1f,
                wordSpacingSp = 1f
            ),
            expandedPickerId = null,
            onPickerToggle = {},
            onEvent = {}
        )
    }

}

