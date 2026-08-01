package com.nndwn.runtext.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.extentions.shimmer
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.utils.Dimens

@Composable
fun ConfigCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun ConfigCardSkeleton(
    shimmerProgress: Float,
    modifier: Modifier = Modifier
) {
    ConfigCard(modifier = modifier
        .shimmer(
            progress = shimmerProgress,
            backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
            shimmerColor = Palette.Grey,
            shape = RoundedCornerShape(Dimens.RoundedCorner)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(20.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            Box(
                modifier = Modifier
                    .height(24.dp)
            )
        }
    }
}
