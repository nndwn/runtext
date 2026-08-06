package com.nndwn.runtext.ui.features.main.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nndwn.runtext.R
import com.nndwn.runtext.data.model.TextStyleConfig
import com.nndwn.runtext.ui.component.CardExpanded
import com.nndwn.runtext.ui.component.LabeledSlider
import com.nndwn.runtext.ui.features.main.MainUiEvent
import com.nndwn.runtext.ui.theme.RuntextTheme
import com.nndwn.runtext.ui.theme.dimens
import kotlin.math.roundToInt

@Composable
fun TextSpacingConfig(
    config : TextStyleConfig,
    expandedId: String?,
    onToggle : (String) -> Unit,
    onEvent : (MainUiEvent) -> Unit,
){
    CardExpanded(
        title = stringResource(R.string.set_config_text_spacing),
        idString = "text_spacing",
        expandedId = expandedId,
        onToggle = onToggle
    ) {
        Column(
            Modifier.padding(MaterialTheme.dimens.medium)
        ){
            LabeledSlider(
                label = stringResource(R.string.set_config_text_spacing_text),
                value = config.letterSpacingSp,
                valueRange = -2f..20f,
                displayValueText = "${config.letterSpacingSp.roundToInt()}",
                onValueChange = { onEvent(MainUiEvent.UpdateLetterSpacing(it)) }
            )
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium))
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

@Composable
@Preview
private fun Preview(){
    RuntextTheme {
        TextSpacingConfig(
            config = TextStyleConfig(
                letterSpacingSp = 1f,
                wordSpacingSp = 1f
            ),
            expandedId = null,
            onToggle = {},
            onEvent = {}
        )
    }

}

