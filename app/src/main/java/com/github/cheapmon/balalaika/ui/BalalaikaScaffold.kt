package com.github.cheapmon.balalaika.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.github.cheapmon.balalaika.theme.BalalaikaTheme
import com.github.cheapmon.balalaika.util.DarkThemeProvider

@Composable
fun BalalaikaScaffold(
    navController: NavController,
    title: String,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    actions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    bodyContent: @Composable (PaddingValues) -> Unit = {}
) {
    BalalaikaTheme {
        BalalaikaScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            title = { Text(text = title) },
            actions = actions,
            floatingActionButton = floatingActionButton,
            bodyContent = bodyContent
        )
    }
}

@Composable
fun BalalaikaScaffold(
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    bodyContent: @Composable (PaddingValues) -> Unit = {}
) {
    BalalaikaTheme {
        BalalaikaScaffold(
            scaffoldState = scaffoldState,
            screen = navController.currentDestination?.id?.let { screenFor(it) }
                ?: Screen.Dictionary,
            onNavigate = { navController.navigate(it.id()) },
            title = title,
            actions = actions,
            floatingActionButton = floatingActionButton,
            bodyContent = bodyContent
        )
    }
}

@Composable
fun BalalaikaScaffold(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    screens: List<List<Screen>> = balalaikaScreens,
    screen: Screen = Screen.Dictionary,
    onNavigate: (Screen) -> Unit = {},
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    bodyContent: @Composable (PaddingValues) -> Unit = {}
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = title,
                navigationIcon = {
                    IconButton(onClick = { scaffoldState.drawerState.open() }) {
                        Icon(asset = Icons.Default.Menu)
                    }
                },
                actions = actions
            )
        },
        drawerContent = {
            NavigationDrawer(
                screens = screens,
                currentScreen = screen,
                onNavigate = { screen ->
                    scaffoldState.drawerState.close { onNavigate(screen) }
                }
            )
        },
        floatingActionButton = floatingActionButton,
        bodyContent = bodyContent
    )
}

@Preview
@Composable
private fun BalalaikaScaffoldPreview(
    @PreviewParameter(DarkThemeProvider::class) darkTheme: Boolean
) {
    BalalaikaTheme(darkTheme = darkTheme) {
        BalalaikaScaffold(
            title = { Text(text = "Title") }
        ) {
            Text(text = "Body")
        }
    }
}
