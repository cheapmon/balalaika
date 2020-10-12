package com.github.cheapmon.balalaika.theme

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.ui.tooling.preview.Preview

val lightBlue700 = Color(0xff0288d1)
val lightBlue800 = Color(0xff0277bd)
val amber700 = Color(0xffff8f00)
val amber800 = Color(0xffff6f00)
val grey100 = Color(0xfff5f5f5)
val red700 = Color(0xffd32f2f)

val lightBlue200 = Color(0xff81d4fa)
val lightBlue300 = Color(0xff4fc3f7)
val amber200 = Color(0xffffcc80)
val amber300 = Color(0xffffb74d)
val blueGrey900 = Color(0xff263238)
val red200 = Color(0xffef9a9a)

val lightColors = Colors(
    primary = lightBlue700,
    primaryVariant = lightBlue800,
    secondary = amber700,
    secondaryVariant = amber800,
    background = grey100,
    surface = grey100,
    error = red700,
    onPrimary = grey100,
    onSecondary = grey100,
    onBackground = blueGrey900,
    onSurface = blueGrey900,
    onError = grey100,
    isLight = true
)

val darkColors = Colors(
    primary = lightBlue200,
    primaryVariant = lightBlue300,
    secondary = amber200,
    secondaryVariant = amber300,
    background = blueGrey900,
    surface = blueGrey900,
    error = red200,
    onPrimary = blueGrey900,
    onSecondary = blueGrey900,
    onBackground = grey100,
    onSurface = grey100,
    onError = blueGrey900,
    isLight = false
)

@Composable
fun IconColor() = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)

@Composable
private fun ColorsPreview() {
    Column {
        TopAppBar(
            backgroundColor = MaterialTheme.colors.primary,
            title = { Text(text = "Primary", color = MaterialTheme.colors.onPrimary) }
        )
        TopAppBar(
            backgroundColor = MaterialTheme.colors.primaryVariant,
            title = { Text(text = "Primary Variant", color = MaterialTheme.colors.onPrimary) }
        )
        TopAppBar(
            backgroundColor = MaterialTheme.colors.secondary,
            title = { Text(text = "Secondary", color = MaterialTheme.colors.onSecondary) }
        )
        TopAppBar(
            backgroundColor = MaterialTheme.colors.secondaryVariant,
            title = { Text(text = "Secondary variant", color = MaterialTheme.colors.onSecondary) }
        )
        TopAppBar(
            backgroundColor = MaterialTheme.colors.background,
            title = { Text(text = "Background", color = MaterialTheme.colors.onBackground) }
        )
        TopAppBar(
            backgroundColor = MaterialTheme.colors.surface,
            title = { Text(text = "Surface", color = MaterialTheme.colors.onSurface) }
        )
        TopAppBar(
            backgroundColor = MaterialTheme.colors.error,
            title = { Text(text = "Error", color = MaterialTheme.colors.onError) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LightColorsPreview() {
    BalalaikaTheme {
        ColorsPreview()
    }
}

@Preview(showBackground = true)
@Composable
private fun DarkColorsPreview() {
    BalalaikaTheme(darkTheme = true) {
        ColorsPreview()
    }
}
