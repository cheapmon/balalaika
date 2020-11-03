package com.github.cheapmon.balalaika.ui

import androidx.annotation.IdRes
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.HorizontalGradient
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.platform.AnimationClockAmbient
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.ui.tooling.preview.Preview
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.theme.BalalaikaTheme
import com.github.cheapmon.balalaika.theme.itemPadding
import com.github.cheapmon.balalaika.theme.itemSpacing

@Composable
fun NavigationDrawer(
    navController: NavController,
    drawerState: DrawerState,
    @IdRes destinationId: Int? = null
) {
    fun navigate(@IdRes destinationId: Int) =
        drawerState.close { navController.navigate(destinationId) }

    Column {
        BalalaikaLogo()
        NavGroup {
            NavButton(
                onClick = { navigate(R.id.nav_home) },
                icon = Icons.Default.Home,
                title = "Home",
                selected = destinationId == R.id.nav_home
            )
            NavButton(
                onClick = { navigate(R.id.nav_search) },
                icon = Icons.Default.Search,
                title = "Search",
                selected = destinationId == R.id.nav_search
            )
            NavButton(
                onClick = { navigate(R.id.nav_history) },
                icon = Icons.Default.History,
                title = "History",
                selected = destinationId == R.id.nav_history
            )
            NavButton(
                onClick = { navigate(R.id.nav_bookmarks) },
                icon = Icons.Default.Bookmark,
                title = "Bookmarks",
                selected = destinationId == R.id.nav_bookmarks
            )
        }
        NavGroup {
            NavButton(
                onClick = { navigate(R.id.nav_selection) },
                icon = Icons.Default.LibraryBooks,
                title = "Library",
                selected = destinationId == R.id.nav_selection
            )
        }
        NavGroup {
            NavButton(
                onClick = { navigate(R.id.nav_preferences) },
                icon = Icons.Default.Info,
                title = "About",
                selected = destinationId == R.id.nav_preferences
            )
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
            NavigationDrawer(
                navController = NavController(ContextAmbient.current),
                drawerState = DrawerState(DrawerValue.Closed, AnimationClockAmbient.current),
                destinationId = R.id.nav_search
            )
        }
    }
}

@Preview("Navigation drawer dark")
@Composable
private fun NavigationDrawerPreviewDark() {
    BalalaikaTheme(darkTheme = true) {
        Surface {
            NavigationDrawer(
                navController = NavController(ContextAmbient.current),
                drawerState = DrawerState(DrawerValue.Closed, AnimationClockAmbient.current),
                destinationId = R.id.nav_selection
            )
        }
    }
}
