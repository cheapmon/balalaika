package com.github.cheapmon.balalaika.components

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.ui.tooling.preview.Preview
import com.github.cheapmon.balalaika.model.Bookmark
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.Property
import com.github.cheapmon.balalaika.theme.BalalaikaTheme
import com.github.cheapmon.balalaika.theme.SubtitleColor
import com.github.cheapmon.balalaika.util.happier
import java.util.*

@Composable
fun DictionaryEntryCard(
    dictionaryEntry: DictionaryEntry,
    modifier: Modifier = Modifier,
    onClickBase: (DictionaryEntry) -> Unit = {},
    onBookmark: (DictionaryEntry) -> Unit = {},
    onClickProperty: PropertyAction<Property> = emptyPropertyAction()
) {
    val base = dictionaryEntry.base
    val bookmark = dictionaryEntry.bookmark

    CollapsibleCard(
        id = dictionaryEntry.id,
        initialState = CollapsibleCardState.EXPANDED,
        modifier = modifier,
        header = {
            Column {
                Text(text = dictionaryEntry.representation, style = MaterialTheme.typography.h6)
                if (base != null) {
                    Text(
                        text = base.representation,
                        style = MaterialTheme.typography.subtitle1,
                        color = SubtitleColor(),
                        modifier = Modifier.clickable(onClick = { onClickBase(base) })
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { onBookmark(dictionaryEntry) }) {
                val asset = if (bookmark == null) {
                    Icons.Default.BookmarkBorder
                } else {
                    Icons.Default.Bookmark
                }
                Icon(asset = asset)
            }
        }
    ) {
        PropertyList(
            properties = dictionaryEntry.properties,
            onClickProperty = onClickProperty
        )
    }
}

@Composable
private fun PropertyList(
    properties: SortedMap<DataCategory, List<Property>>,
    modifier: Modifier = Modifier,
    onClickProperty: PropertyAction<Property> = emptyPropertyAction()
) {
    Column(modifier = modifier) {
        properties.forEach { (category, list) ->
            WidgetFor(
                category = category,
                properties = list,
                onEvent = onClickProperty
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DictionaryEntryCardPreview() {
    var entry by remember { mutableStateOf(happier) }

    BalalaikaTheme {
        DictionaryEntryCard(
            dictionaryEntry = entry,
            onBookmark = {
                entry = entry.copy(bookmark = if (entry.bookmark == null) Bookmark() else null)
            }
        )
    }
}