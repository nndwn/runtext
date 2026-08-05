package com.nndwn.runtext.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.nndwn.runtext.extentions.shimmer

@Composable
fun Skeleton(
    shimmerProgress: Float?,
    height: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .shimmer(
                progress = shimmerProgress,
                backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                shimmerColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.medium
            )
    )
}