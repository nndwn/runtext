package com.nndwn.runtext.ui.features.main.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.R
import com.nndwn.runtext.ui.component.CardExpanded
import com.nndwn.runtext.ui.features.main.MainUiEvent

@Composable
fun TextPresetConfig(
    modifier: Modifier = Modifier,
    expandedId : String?,
    onToggle: (String) -> Unit,
    onEvent : (MainUiEvent) -> Unit,
){
    CardExpanded(
        title = stringResource(R.string.set_config_text_presets),
        modifier = modifier,
        idString = "text_presets",
        expandedId = expandedId,
        onToggle = onToggle
    ) {
        LazyRow(

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            items(Presets) { preset ->
                PresetItem(
                    preset = preset,
                    onClick = {onEvent(MainUiEvent.ApplyPreset(preset.settings))}
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }

}

@Preview
@Composable
private fun Preview(){
    TextPresetConfig(
        expandedId = "text_presets",
        onToggle = {},
        onEvent = {}
    )
}