package com.github.cheapmon.balalaika.theme

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview

@Composable
fun BalalaikaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkColors else lightColors

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun BalalaikaThemePreview() {
    BalalaikaTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Balalaika")
                    },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.Menu)
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(asset = Icons.Default.MoreVert)
                        }
                    },
                    elevation = 8.dp
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    Icon(asset = Icons.Default.Book)
                }
            },
            drawerContent = { ShapesPreview() }
        ) {
            TypographyPreview()
        }
    }
}