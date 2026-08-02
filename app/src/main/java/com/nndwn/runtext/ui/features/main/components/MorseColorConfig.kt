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
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.R
import com.nndwn.runtext.ui.component.CardExpanded
import com.nndwn.runtext.ui.features.main.MainUiEvent
import com.nndwn.runtext.ui.theme.toArgbLong

val BrightMorseColorPresets = listOf(
    Color(0xFFFFFFFF),
    Color(0xFFF5F5F5),

    Color(0xFFFFEB3B),
    Color(0xFFFFD700),
    Color(0xFFFFC107),

    Color(0xFF00E676),
    Color(0xFF76FF03),
    Color(0xFFB2FF59),
    Color(0xFF00FFCC),

    Color(0xFF00E5FF),
    Color(0xFF1DE9B6),
    Color(0xFF80D8FF),
    Color(0xFF40C4FF),

    Color(0xFFFF80AB),
    Color(0xFFEA80FC),
    Color(0xFFE040FB),

    Color(0xFFFF9100),
    Color(0xFFFF6D00),
    Color(0xFFFF5252)
)

@Composable
fun MorseColorConfig(

    modifier: Modifier = Modifier,
    currentColor: Color,
    expandedId : String?,
    onToggle : (String) -> Unit,
    onEvent : (MainUiEvent) -> Unit,
    ){
    CardExpanded(
        modifier = modifier,
        title = stringResource(R.string.set_config_morse_color),
        idString = "morse_color",
        expandedId = expandedId,
        onToggle = onToggle,
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(BrightMorseColorPresets){ color ->
                val colorArgb = color.toArgbLong()
                val selected = colorArgb == currentColor.toArgbLong()
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = if (selected) 3.dp else 1.dp,
                            color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                        .clickable{
                            onEvent(MainUiEvent.UpdateBgColorMorse(colorArgb))
                        }
                )
            }
        }
    }
}
