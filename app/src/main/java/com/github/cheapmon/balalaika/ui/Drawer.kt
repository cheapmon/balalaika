package com.github.cheapmon.balalaika.ui

import androidx.annotation.IdRes
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.platform.AnimationClockAmbient
import androidx.compose.ui.platform.ContextAmbient
import androidx.navigation.NavController
import androidx.ui.tooling.preview.Preview
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.theme.BalalaikaTheme
import com.github.cheapmon.balalaika.theme.itemPadding
import com.github.cheapmon.balalaika.theme.itemSpacing

@Composable
fun NavigationDrawer(
    navController: NavController,
    drawerState: DrawerState
) {
    Column(
        modifier = Modifier.padding(itemPadding),
        verticalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        fun navigate(@IdRes destinationId: Int) =
            drawerState.close { navController.navigate(destinationId) }

        NavButton(
            onClick = { navigate(R.id.nav_home) },
            icon = Icons.Default.Home,
            title = "HOME"
        )
        NavButton(
            onClick = { navigate(R.id.nav_search) },
            icon = Icons.Default.Search,
            title = "SEARCH"
        )
        NavButton(
            onClick = { navigate(R.id.nav_history) },
            icon = Icons.Default.History,
            title = "HISTORY"
        )
        NavButton(
            onClick = { navigate(R.id.nav_bookmarks) },
            icon = Icons.Default.Bookmark,
            title = "BOOKMARKS"
        )
        NavButton(
            onClick = { navigate(R.id.nav_selection) },
            icon = Icons.Default.LibraryBooks,
            title = "LIBRARY"
        )
        NavButton(
            onClick = { navigate(R.id.nav_preferences) },
            icon = Icons.Default.Info,
            title = "ABOUT"
        )
    }
}

@Composable
private fun NavButton(
    onClick: () -> Unit,
    icon: VectorAsset,
    title: String
) {
    Button(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(itemSpacing * 2)
        ) {
            Icon(asset = icon)
            Text(text = title)
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
                drawerState = DrawerState(DrawerValue.Closed, AnimationClockAmbient.current)
            )
        }
    }
}
