package com.github.cheapmon.balalaika.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.HorizontalGradient
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.theme.BalalaikaTheme
import com.github.cheapmon.balalaika.theme.itemPadding
import com.github.cheapmon.balalaika.theme.itemSpacing

@Composable
fun NavigationDrawer(
    currentScreen: Screen = Screen.Dictionary,
    screens: List<List<Screen>> = balalaikaScreens,
    onNavigate: (Screen) -> Unit = {},
) {
    Column {
        BalalaikaLogo()
        screens.forEach { group ->
            NavGroup {
                group.forEach { screen ->
                    NavButton(
                        onClick = { onNavigate(screen) },
                        icon = screen.icon,
                        title = stringResource(id = screen.titleId),
                        selected = screen == currentScreen
                    )
                }
            }
        }
    }
}

@Composable
private fun BalalaikaLogo() {
    Surface(
        elevation = 4.dp,
        contentColor = contentColorFor(MaterialTheme.colors.primary)
    ) {
        Column(
            modifier = Modifier.background(
                brush = HorizontalGradient(
                    colors = listOf(
                        MaterialTheme.colors.primaryVariant,
                        MaterialTheme.colors.primary
                    ),
                    startX = 0f,
                    endX = 500f
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .preferredHeight(100.dp)
                    .padding(end = itemPadding, bottom = itemPadding),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(itemSpacing * 2)
                ) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        style = MaterialTheme.typography.h5
                    )
                    Icon(asset = vectorResource(id = R.drawable.ic_balalaika))
                }
            }
        }
    }
}

@Composable
private fun NavGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.padding(itemPadding),
        verticalArrangement = Arrangement.spacedBy(itemSpacing),
        children = content
    )
    Divider()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun NavButton(
    onClick: () -> Unit,
    icon: VectorAsset,
    title: String,
    selected: Boolean = false
) {
    val foregroundColor = if (selected) {
        MaterialTheme.colors.primaryVariant
    } else {
        MaterialTheme.colors.onSurface.copy(alpha = .8f)
    }
    val backgroundColor = if (selected) {
        MaterialTheme.colors.primary.copy(alpha = .2f)
    } else {
        Color.Transparent
    }

    TextButton(
        onClick = onClick,
        colors = ButtonConstants.defaultTextButtonColors(
            backgroundColor = backgroundColor,
            contentColor = foregroundColor
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(itemSpacing * 2)
        ) {
            Icon(asset = icon)
            Text(
                text = title,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Preview("Navigation drawer")
@Composable
private fun NavigationDrawerPreview() {
    BalalaikaTheme {
        Surface {
            NavigationDrawer()
        }
    }
}

@Preview("Navigation drawer dark")
@Composable
private fun NavigationDrawerPreviewDark() {
    BalalaikaTheme(darkTheme = true) {
        Surface {
            NavigationDrawer()
        }
    }
}
