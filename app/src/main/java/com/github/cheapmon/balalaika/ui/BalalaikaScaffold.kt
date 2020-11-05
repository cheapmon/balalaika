package com.github.cheapmon.balalaika.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.github.cheapmon.balalaika.theme.BalalaikaTheme

@Composable
fun BalalaikaScaffold(
    navController: NavController,
    title: String,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    actions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    bodyContent: @Composable (PaddingValues) -> Unit = {}
) {
    BalalaikaScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        title = { Text(text = title) },
        actions = actions,
        floatingActionButton = floatingActionButton,
        bodyContent = bodyContent
    )
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
                    currentScreen = navController.currentDestination?.id
                        ?.let { id -> screenFor(id) } ?: Screen.Dictionary,
                    onNavigate = { screen ->
                        scaffoldState.drawerState.close { navController.navigate(screen.id()) }
                    }
                )
            },
            floatingActionButton = floatingActionButton,
            bodyContent = bodyContent
        )
    }
}
