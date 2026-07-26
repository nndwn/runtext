package com.nndwn.runtext.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nndwn.runtext.R
import com.nndwn.runtext.ui.theme.Palette
import com.nndwn.runtext.ui.utils.Dimens

@Composable
fun Header(
    modifier: Modifier = Modifier,
    withSidebar : Boolean = false,
    onMenuClick : () -> Unit = {}
) {
    Box( modifier = modifier
        .fillMaxWidth()
        .statusBarsPadding()
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.PaddingHorizontal, vertical = 10.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                tint = Palette.White,
                modifier = Modifier
                    .size(48.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = stringResource(R.string.app_name),
                color = Palette.White,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black
                ),
                modifier = Modifier.weight(1f)
            )
            if (withSidebar) {
                ThreeDotsHorizontal(onClick = onMenuClick)
            }

        }
    }
}
@Preview
@Composable
private fun Preview(){
    Column() {
        Header(
            withSidebar = true
        ) {  }
        Header(
            withSidebar = false
        ){}
    }

}