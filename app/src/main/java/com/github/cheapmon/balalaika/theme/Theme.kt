package com.github.cheapmon.balalaika.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.github.cheapmon.balalaika.util.DarkThemeProvider

@Composable
val MaterialColors
    get() = MaterialTheme.colors

@Composable
val MaterialTypography
    get() = MaterialTheme.typography

@Composable
val MaterialShapes
    get() = MaterialTheme.shapes

@Composable
fun BalalaikaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colors: Colors? = null,
    content: @Composable () -> Unit
) {
    val finalColors = if (isSecretActive) {
        secretColors
    } else {
        colors ?: if (darkTheme) darkColors else lightColors
    }

    MaterialTheme(
        colors = finalColors,
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

@Composable
fun HighlightTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColors.copy(
            primary = darkColors.surface,
            surface = darkColors.primary,
            onPrimary = darkColors.onSurface,
            onSurface = darkColors.onPrimary
        )
    } else {
        lightColors.copy(
            primary = lightColors.surface,
            surface = lightColors.primary,
            onPrimary = lightColors.onSurface,
            onSurface = lightColors.onPrimary
        )
    }

    BalalaikaTheme(darkTheme = darkTheme, colors = colors, content = content)
}

val secretColors = Colors(
    primary = Color(0xfffa5788),
    primaryVariant = Color(0xff8c0032),
    secondary = Color(0xffae52d4),
    secondaryVariant = Color(0xff4a0072),
    background = Color(0xfffafafa),
    surface = Color(0xfffafafa),
    error = Color(0xffd32f2f),
    onPrimary = Color(0xfffafafa),
    onSecondary = Color(0xfffafafa),
    onBackground = Color(0xff263238),
    onSurface = Color(0xff263238),
    onError = Color(0xfffafafa),
    isLight = true
)

var isSecretActive = false

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
