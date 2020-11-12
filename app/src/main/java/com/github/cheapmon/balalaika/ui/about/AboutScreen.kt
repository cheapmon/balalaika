package com.github.cheapmon.balalaika.ui.about

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.material.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.ui.tooling.preview.Preview
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.theme.MaterialColors
import com.github.cheapmon.balalaika.theme.listItemIconSize
import com.github.cheapmon.balalaika.theme.onSurfaceLight
import com.github.cheapmon.balalaika.ui.BalalaikaScaffold

@Composable
fun AboutScreen(
    navController: NavController,
    onOpenUrl: (Uri) -> Unit
) {
    val iconUrl = Uri.parse(stringResource(id = R.string.preferences_icon_url))
    val authorUrl = Uri.parse(stringResource(id = R.string.preferences_author_url))
    val licenseUrl = Uri.parse(stringResource(id = R.string.preferences_license_url))

    BalalaikaScaffold(
        navController = navController,
        title = stringResource(id = R.string.menu_about)
    ) {
        Column {
            HeaderItem(icon = Icons.Default.Photo, textId = R.string.preferences_media)
            InfoItem(
                icon = Icons.Default.Brush,
                textId = R.string.preferences_icon_title,
                secondaryTextId = R.string.preferences_icon_author,
                onClick = { onOpenUrl(iconUrl) }
            )
            Divider()
            HeaderItem(icon = Icons.Default.Info, textId = R.string.preferences_about)
            InfoItem(
                icon = Icons.Default.EmojiPeople,
                textId = R.string.preferences_author_title,
                secondaryTextId = R.string.preferences_author_name,
                onClick = { onOpenUrl(authorUrl) }
            )
            InfoItem(
                icon = Icons.Default.Gavel,
                textId = R.string.preferences_license_title,
                secondaryTextId = R.string.preferences_license_name,
                onClick = { onOpenUrl(licenseUrl) }
            )
            Divider()
        }
    }
}

@Composable
private fun HeaderItem(
    icon: VectorAsset,
    @StringRes textId: Int
) {
    ListItem(
        icon = {
            Icon(
                asset = icon,
                modifier = Modifier.preferredWidth(listItemIconSize),
                tint = MaterialColors.onSurfaceLight
            )
        }
    ) {
        Text(
            text = stringResource(id = textId),
            color = MaterialColors.secondary
        )
    }
}

@Composable
private fun InfoItem(
    icon: VectorAsset,
    @StringRes textId: Int,
    @StringRes secondaryTextId: Int,
    onClick: () -> Unit
) {
    ListItem(
        icon = {
            Icon(
                asset = icon,
                modifier = Modifier.preferredSize(listItemIconSize),
                tint = MaterialColors.onSurfaceLight
            )
        },
        modifier = Modifier.clickable(onClick = onClick),
        secondaryText = { Text(text = stringResource(id = secondaryTextId)) }
    ) {
        Text(text = stringResource(id = textId))
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutScreenPreview() {
    AboutScreen(navController = NavController(ContextAmbient.current), onOpenUrl = {})
}