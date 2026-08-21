package com.nndwn.runtext.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nndwn.runtext.AppFlavor
import com.nndwn.runtext.BuildConfig
import com.nndwn.runtext.R
import com.nndwn.runtext.ui.theme.RuntextTheme
import com.nndwn.runtext.ui.theme.dimens
import com.nndwn.runtext.ui.LocalIsPremium

enum class MenuOptions(
    @param:StringRes val label: Int,
    @param:DrawableRes val iconRes: Int
) {
    REMOVE_ADS(
        label = R.string.menu_remove_ad,
        iconRes = R.drawable.ic_coffee2
    ),
    RATE_APP(
        label = R.string.menu_review,
        iconRes = R.drawable.ic_star
    ),

    REPORT_ISSUE(
        label = R.string.menu_report_issue,
        iconRes = R.drawable.ic_bug
    ),
    DEBUG(
        label = R.string.menu_debug,
        iconRes = R.drawable.ic_bug
    )
}

@Composable
fun MenuOptions(
    modifier: Modifier = Modifier,
    onMenuSelected: (MenuOptions) -> Unit
) {
    val isPremium = LocalIsPremium.current
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.extraSmall)
    ) {
        MenuOptions.entries
            .filter {
                when (it) {
                    MenuOptions.DEBUG -> BuildConfig.DEBUG
                    MenuOptions.REMOVE_ADS -> !isPremium || AppFlavor.current == AppFlavor.PLAYSTORE
                    else -> true
                }
            }
            .forEach { menu ->
                ItemOptions(
                    menu = menu,
                    onClick = { onMenuSelected(menu) }
                )
            }
    }
}

@Composable
private fun ItemOptions(
    menu: MenuOptions,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .padding( vertical = MaterialTheme.dimens.small, horizontal = MaterialTheme.dimens.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(menu.iconRes),
            contentDescription = "icon ${stringResource(menu.label)}",
            modifier = Modifier.size(MaterialTheme.dimens.iconMedium)
        )
        Spacer(modifier = Modifier.width(MaterialTheme.dimens.medium))
        Text(
            text = stringResource(menu.label),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
@Preview
private fun Preview(){
    RuntextTheme {
        MenuOptions(){

        }
    }
}