package com.github.cheapmon.balalaika.ui.bookmarks

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.components.EmptyMessage
import com.github.cheapmon.balalaika.model.Bookmark
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.theme.BalalaikaTheme
import java.util.*

@Composable
fun BookmarksScreen(
    viewModel: BookmarksViewModel,
    onClickEntry: (DictionaryEntry) -> Unit = {}
) {
    val entries by viewModel.bookmarkedEntries.observeAsState()

    BalalaikaTheme {
        Surface {
            entries.orEmpty().let { entries ->
                if (entries.isEmpty()) {
                    BookmarksEmptyMessage()
                } else {
                    BookmarksList(
                        entries = entries,
                        onClickEntry = onClickEntry,
                        onDeleteEntry = { viewModel.removeBookmark(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BookmarksEmptyMessage() {
    EmptyMessage(
        icon = Icons.Default.Bookmark
    ) {
        Text(
            text = stringResource(id = R.string.bookmarks_empty),
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
private fun BookmarksList(
    entries: List<DictionaryEntry>,
    modifier: Modifier = Modifier,
    onClickEntry: (DictionaryEntry) -> Unit = {},
    onDeleteEntry: (DictionaryEntry) -> Unit = {}
) {
    LazyColumnFor(items = entries, modifier = modifier) { entry ->
        Column(modifier = Modifier.clickable(onClick = { onClickEntry(entry) })) {
            ListItem(
                icon = {
                    Icon(
                        asset = Icons.Default.Bookmark,
                        modifier = Modifier.preferredSize(40.dp)
                    )
                },
                secondaryText = entry.base?.let {
                    { Text(text = it.representation) }
                },
                trailing = {
                    IconButton(onClick = { onDeleteEntry(entry) }) {
                        Icon(Icons.Default.Delete)
                    }
                }
            ) {
                Text(text = entry.representation)
            }
            Divider()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarksEmptyMessagePreview() {
    BalalaikaTheme {
        BookmarksEmptyMessage()
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarksListPreview() {
    BalalaikaTheme {
        BookmarksList(
            entries = listOf(
                DictionaryEntry(
                    id = "entry",
                    representation = "Dictionary entry",
                    base = DictionaryEntry(
                        id = "base",
                        representation = "Base",
                        base = null,
                        properties = TreeMap(emptyMap()),
                        bookmark = null
                    ),
                    properties = TreeMap(emptyMap()),
                    bookmark = Bookmark()
                ),
                DictionaryEntry(
                    id = "entry2",
                    representation = "Second dictionary entry",
                    base = null,
                    properties = TreeMap(emptyMap()),
                    bookmark = Bookmark()
                )
            )
        )
    }
}