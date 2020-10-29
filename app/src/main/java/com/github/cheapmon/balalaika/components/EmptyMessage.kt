package com.github.cheapmon.balalaika.components

import androidx.compose.material.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.ui.tooling.preview.Preview
import com.github.cheapmon.balalaika.theme.BalalaikaTheme
import com.github.cheapmon.balalaika.theme.IconColor
import com.github.cheapmon.balalaika.theme.bigIconSize
import com.github.cheapmon.balalaika.theme.itemSpacing

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
            tint = IconColor()
        )
        message()
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyMessagePreview() {
    BalalaikaTheme {
        EmptyMessage(icon = Icons.Default.Book) {
            Text(text = "You have no books", style = MaterialTheme.typography.body2)
            Button(onClick = {}) { Text(text = "GET BOOKS") }
        }
    }
}