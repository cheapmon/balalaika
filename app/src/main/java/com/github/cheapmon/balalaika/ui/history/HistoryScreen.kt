package com.github.cheapmon.balalaika.ui.history

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.components.EmptyMessage
import com.github.cheapmon.balalaika.model.HistoryItem
import com.github.cheapmon.balalaika.model.SearchRestriction
import com.github.cheapmon.balalaika.theme.BalalaikaTheme
import com.github.cheapmon.balalaika.util.sampleHistoryItems

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onClickItem: (HistoryItem) -> Unit = {}
) {
    val items by viewModel.items.observeAsState()
    BalalaikaTheme {
        Surface {
            items.orEmpty().let { items ->
                if (items.isEmpty()) {
                    HistoryEmptyMessage()
                } else {
                    HistoryList(
                        items = items,
                        onClickItem = onClickItem,
                        onDeleteItem = { item ->
                            viewModel.removeItem(item)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryEmptyMessage() {
    EmptyMessage(
        icon = Icons.Default.History
    ) {
        Text(
            text = stringResource(id = R.string.history_empty),
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
private fun HistoryList(
    items: List<HistoryItem>,
    modifier: Modifier = Modifier,
    onClickItem: (HistoryItem) -> Unit = {},
    onDeleteItem: (HistoryItem) -> Unit = {}
) {
    LazyColumnFor(items = items, modifier = modifier) { item ->
        Column(modifier = Modifier.clickable(onClick = { onClickItem(item) })) {
            ListItem(
                icon = {
                    Icon(
                        asset = Icons.Default.Search,
                        modifier = Modifier.preferredSize(40.dp)
                    )
                },
                secondaryText = { Text(text = item.restriction.text()) },
                trailing = {
                    IconButton(onClick = { onDeleteItem(item) }) {
                        Icon(Icons.Default.Delete)
                    }
                }
            ) {
                Text(text = item.query)
            }
            Divider()
        }
    }
}

@Composable
private fun SearchRestriction?.text(): String = if (this != null) {
    stringResource(id = R.string.search_restriction, category.name, text)
} else {
    stringResource(id = R.string.no_restriction)
}

@Preview(showBackground = true)
@Composable
private fun HistoryEmptyMessagePreview() {
    BalalaikaTheme {
        HistoryEmptyMessage()
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryListPreview() {
    BalalaikaTheme {
        HistoryList(items = sampleHistoryItems)
    }
}