package com.nndwn.runtext.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.nndwn.runtext.R
import com.nndwn.runtext.ui.theme.RuntextTheme
import com.nndwn.runtext.ui.theme.dimens
import com.nndwn.runtext.ui.utils.LocalSizeWidth

@Composable
fun DialogWatchAds(
    showPanel : Boolean,
    price: String? = null,
    onDismiss : () -> Unit = {},
    onWatchAds : () -> Unit = {},
    onRemoveAds : () -> Unit = {}
) {
    SlideUpPanel(
        state = SlideUpPanelState(
            visible = showPanel,
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.dimens.medium)
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.medium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_coffee),
                    contentDescription = stringResource(R.string.buy_coffee),
                    modifier = Modifier.size(MaterialTheme.dimens.iconLarge)
                )
                Text(
                    text = stringResource(R.string.buy_coffee),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Text(
                text = stringResource(R.string.text_dialog_watch_ads),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small)
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    onClick = onWatchAds,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.btn_text_watch_ad),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(vertical = MaterialTheme.dimens.small)
                    )

                }

                OutlinedButton(
                    colors = ButtonDefaults.outlinedButtonColors().copy(
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    onClick = onRemoveAds,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val label = if (price != null) {
                        "${stringResource(R.string.btn_text_remove_ad)} ($price)"
                    } else {
                        stringResource(R.string.btn_text_remove_ad)
                    }
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = MaterialTheme.dimens.small)
                    )
                }
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onDismiss,
                    colors = ButtonDefaults.outlinedButtonColors().copy(
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                            alpha = 0.5f
                        )
                    )
                ) {
                    Text(
                        text = stringResource(R.string.btn_maybe_later),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = MaterialTheme.dimens.small)
                    )
                }
            }
        }
    }
}


@Preview(device = "id:medium_phone")
@Composable
private fun Preview() {
    var show by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = {
                show = !show
            }
        ) {
            Text("Show and Hide")
        }


    }
    CompositionLocalProvider(
        LocalSizeWidth provides WindowWidthSizeClass.Compact
    ) {
        RuntextTheme {
            DialogWatchAds(true)
        }
    }


}
