package com.nndwn.runtext.ui.features.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.nndwn.runtext.R
import com.nndwn.runtext.ui.component.CardExpanded
import com.nndwn.runtext.ui.features.main.MainUiEvent
import com.nndwn.runtext.ui.theme.ColorPresets
import com.nndwn.runtext.ui.theme.dimens
import com.nndwn.runtext.ui.theme.toArgbLong

@Composable
fun MorseColorConfig(
  modifier: Modifier = Modifier,
  currentColor: Color,
  expandedId: String?,
  onToggle: (String) -> Unit,
  onEvent: (MainUiEvent) -> Unit,
) {
  CardExpanded(
    modifier = modifier,
    title = stringResource(R.string.set_config_morse_color),
    idString = "morse_color",
    expandedId = expandedId,
    onToggle = onToggle,
  ) {
    LazyRow(
      modifier = Modifier.fillMaxWidth().padding(vertical = MaterialTheme.dimens.medium),
      horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      items(ColorPresets.Vibrant) { color ->
        val colorArgb = color.toArgbLong()
        val selected = colorArgb == currentColor.toArgbLong()
        Box(
          modifier =
            Modifier.size(MaterialTheme.dimens.iconExtraLarge)
              .clip(CircleShape)
              .background(color)
              .border(
                width = if (selected) MaterialTheme.dimens.borderExtraLarge else MaterialTheme.dimens.borderMedium,
                color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
                shape = CircleShape,
              )
              .clickable {
                onEvent(MainUiEvent.UpdateBgColorMorse(colorArgb))
              }
        )
      }
    }
  }
}
