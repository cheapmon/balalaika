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
    actions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    bodyContent: @Composable (PaddingValues) -> Unit = {}
) {
    BalalaikaScaffold(
        navController = navController,
        title = { Text(text = title) },
        actions = actions,
        floatingActionButton = floatingActionButton,
        bodyContent = bodyContent
    )
}

@Composable
fun BalalaikaScaffold(
    navController: NavController,
    title: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    bodyContent: @Composable (PaddingValues) -> Unit = {}
) {
    val scaffoldState = rememberScaffoldState()

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
                    navController = navController,
                    drawerState = scaffoldState.drawerState,
                    destinationId = navController.currentDestination?.id
                )
            },
            floatingActionButton = floatingActionButton,
            bodyContent = bodyContent
        )
    }
}