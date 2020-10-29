package com.github.cheapmon.balalaika.ui

import androidx.compose.material.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
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
    val scaffoldState = rememberScaffoldState()

    BalalaikaTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { Text(text = title) },
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
                    drawerState = scaffoldState.drawerState
                )
            },
            floatingActionButton = floatingActionButton,
            bodyContent = bodyContent
        )
    }
}