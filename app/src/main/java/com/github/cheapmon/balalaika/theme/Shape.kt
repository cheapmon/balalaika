package com.github.cheapmon.balalaika.theme

import androidx.compose.material.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.github.cheapmon.balalaika.util.DarkThemeProvider

val simpleShape = RoundedCornerShape(4.dp)

val shapes = Shapes(
    small = simpleShape,
    medium = simpleShape,
    large = simpleShape
)

@Preview
@Composable
private fun ShapesPreview(
    @PreviewParameter(DarkThemeProvider::class) darkTheme: Boolean
) {
    BalalaikaTheme(darkTheme = darkTheme) {
        Card(modifier = Modifier.padding(12.dp), elevation = 8.dp) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {}) {
                    Icon(asset = Icons.Default.Add)
                    Text(text = "BUTTON")
                }
                IconButton(onClick = {}) {
                    Icon(asset = Icons.Default.Delete)
                }
                FloatingActionButton(onClick = {}) {
                    Icon(asset = Icons.Default.Book)
                }
            }
        }
    }
}
