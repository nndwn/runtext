package com.nndwn.runtext.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.nndwn.runtext.ui.theme.dimens

@Composable
fun ThreeDotsHorizontal(
    onClick : () -> Unit
){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(MaterialTheme.dimens.iconExtraLarge)
            .clip(CircleShape)
            .semantics { contentDescription = "Menu" }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )

        ,
    ) {
        val sizeDot = MaterialTheme.dimens.extraSmall
        val spaceBetween = MaterialTheme.dimens.extraSmall
        Column(
            verticalArrangement = Arrangement.spacedBy(spaceBetween, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Dots(sizeDot)
            Dots(sizeDot)
            Dots(sizeDot)
        }
    }
}

@Composable
private fun Dots(size: Dp){
    Box(
        modifier = Modifier
            .size(size)
            .background(
                color = MaterialTheme.colorScheme.onSurface,
                shape = CircleShape)
    )
}
