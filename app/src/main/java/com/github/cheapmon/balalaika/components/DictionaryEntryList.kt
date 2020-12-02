package com.github.cheapmon.balalaika.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.Property

@Composable
fun DictionaryEntryList(
    entries: LazyPagingItems<DictionaryEntry>,
    modifier: Modifier = Modifier,
    onClickBase: (DictionaryEntry) -> Unit = {},
    onBookmark: (DictionaryEntry) -> Unit = {},
    onClickProperty: PropertyAction<Property> = emptyPropertyAction(),
    dialog: @Composable () -> Unit = {},
    emptyMessage: @Composable () -> Unit = {}
) {
    DictionaryEntryList(
        modifier = modifier,
        isEmpty = entries.itemCount == 0,
        dialog = dialog,
        emptyMessage = emptyMessage
    ) {
        items(entries) { entry ->
            if (entry != null) {
                DictionaryEntryCard(
                    dictionaryEntry = entry,
                    onClickBase = onClickBase,
                    onBookmark = onBookmark,
                    onClickProperty = onClickProperty
                )
            }
        }
    }
}

@Composable
fun DictionaryEntryList(
    entries: List<DictionaryEntry>,
    modifier: Modifier = Modifier,
    onClickBase: (DictionaryEntry) -> Unit = {},
    onBookmark: (DictionaryEntry) -> Unit = {},
    onClickProperty: PropertyAction<Property> = emptyPropertyAction(),
    dialog: @Composable () -> Unit = {},
    emptyMessage: @Composable () -> Unit = {}
) {
    DictionaryEntryList(
        modifier = modifier,
        isEmpty = entries.isEmpty(),
        dialog = dialog,
        emptyMessage = emptyMessage
    ) {
        items(entries) { entry ->
            DictionaryEntryCard(
                dictionaryEntry = entry,
                onClickBase = onClickBase,
                onBookmark = onBookmark,
                onClickProperty = onClickProperty
            )
        }
    }
}

@Composable
private fun DictionaryEntryList(
    modifier: Modifier = Modifier,
    isEmpty: Boolean = false,
    dialog: @Composable () -> Unit = {},
    emptyMessage: @Composable () -> Unit = {},
    list: LazyListScope.() -> Unit = {}
) {
    if (isEmpty) {
        emptyMessage()
    } else {
        LazyColumn(modifier = modifier) { this.list() }
    }
    dialog()
}
