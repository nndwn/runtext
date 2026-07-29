package com.nndwn.runtext.ui.features.main.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.ui.theme.toComposeColor

@Composable
fun ColorPickerConfig(
    label: String,
    currentValue: Long,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onColorChange: (Long) -> Unit
) {
    ConfigCard {
        Column(modifier = Modifier.animateContentSize()) {
            Text(label, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(currentValue.toComposeColor())
                    .clickable { onToggleExpand() }
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                ColorSliders(
                    color = currentValue.toComposeColor(),
                    onColorChange = onColorChange
                )
            }
        }
    }
}
