package com.github.cheapmon.balalaika.components

import androidx.compose.material.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.github.cheapmon.balalaika.theme.*
import com.github.cheapmon.balalaika.util.DarkThemeProvider

@Composable
fun EmptyMessage(
    modifier: Modifier = Modifier,
    icon: VectorAsset = Icons.Default.Close,
    message: @Composable () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(itemSpacing, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            asset = icon.copy(defaultWidth = bigIconSize, defaultHeight = bigIconSize),
            tint = MaterialColors.onSurfaceLight
        )
        message()
    }
}

@Preview
@Composable
private fun EmptyMessagePreview(
    @PreviewParameter(DarkThemeProvider::class) darkTheme: Boolean
) {
    BalalaikaTheme(darkTheme = darkTheme) {
        Surface {
            EmptyMessage(icon = Icons.Default.Book) {
                Text(text = "You have no books", style = MaterialTypography.body2)
                Button(onClick = {}) { Text(text = "GET BOOKS") }
            }
        }
    }
}
