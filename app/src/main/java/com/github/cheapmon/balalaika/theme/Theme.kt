package com.github.cheapmon.balalaika.theme

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
import androidx.ui.tooling.preview.PreviewParameter
import com.github.cheapmon.balalaika.util.DarkThemeProvider

@Composable
fun BalalaikaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colors: Colors? = null,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = colors ?: if (darkTheme) darkColors else lightColors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

@Composable
fun DangerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColors.copy(primary = red200)
    } else {
        lightColors.copy(primary = red700)
    }

    BalalaikaTheme(darkTheme = darkTheme, colors = colors, content = content)
}

@Preview
@Composable
private fun BalalaikaThemePreview(
    @PreviewParameter(DarkThemeProvider::class) darkTheme: Boolean
) {
    BalalaikaTheme(darkTheme = darkTheme) {
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
            }
        ) {
            TypographyPreview(darkTheme = darkTheme)
        }
    }
}
