package com.github.cheapmon.balalaika.ui.search

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.ui.tooling.preview.Preview
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.components.DictionaryEntryCard
import com.github.cheapmon.balalaika.components.EmptyMessage
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.SearchRestriction
import com.github.cheapmon.balalaika.theme.BalalaikaTheme
import com.github.cheapmon.balalaika.theme.itemPadding
import com.github.cheapmon.balalaika.theme.itemSpacing
import com.github.cheapmon.balalaika.util.LazyPagingItems
import com.github.cheapmon.balalaika.util.collectAsLazyPagingItems
import com.github.cheapmon.balalaika.util.items
import com.github.cheapmon.balalaika.util.wordclass
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    modifier: Modifier = Modifier,
    onQueryChange: (String?, SearchRestriction?) -> Unit = { _, _ -> },
    onClickEntry: (DictionaryEntry, String?, SearchRestriction?) -> Unit = { _, _, _ -> }
) {
    val query: String? by viewModel.query.collectAsState(initial = null)
    val restriction: SearchRestriction? by viewModel.restriction.collectAsState(initial = null)

    var empty: Boolean by remember { mutableStateOf(false) }

    val entries = viewModel.entries
        .onEach { empty = it == null }
        .filterNotNull()
        .collectAsLazyPagingItems()

    BalalaikaTheme {
        Surface(modifier = modifier) {
            Column {
                SearchHeader(
                    query = query,
                    onQueryChange = { q, r ->
                        viewModel.setQuery(q)
                        onQueryChange(q, r)
                    },
                    restriction = restriction,
                    onClickRestriction = { viewModel.setRestriction(null) }
                )
                if (empty) {
                    SearchEmptyMessage()
                } else {
                    SearchList(entries, onClickEntry = { onClickEntry(it, query, restriction) })
                }
            }
        }
    }

    onDispose {
        onQueryChange(query, restriction)
    }
}

@Composable
private fun SearchHeader(
    query: String?,
    onQueryChange: (String?, SearchRestriction?) -> Unit = { _, _ -> },
    restriction: SearchRestriction?,
    onClickRestriction: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.padding(vertical = itemSpacing)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(itemPadding),
            verticalArrangement = Arrangement.spacedBy(itemSpacing)
        ) {
            TextField(
                value = query ?: "",
                onValueChange = { onQueryChange(it, restriction) },
                modifier = Modifier.fillMaxWidth(),
                imeAction = ImeAction.Search,
                onImeActionPerformed = { _, controller -> controller?.hideSoftwareKeyboard() },
                label = { Text(text = stringResource(R.string.search_query)) }
            )
            if (restriction != null) {
                TextButton(onClick = onClickRestriction) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(
                                id = R.string.search_restriction,
                                restriction.category.name,
                                restriction.text
                            )
                        )
                        Icon(asset = Icons.Default.Close)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchEmptyMessage() {
    EmptyMessage(icon = Icons.Default.Search) {
        Text(text = stringResource(id = R.string.search_no_results))
    }
}

@OptIn(ExperimentalLazyDsl::class)
@Composable
private fun SearchList(
    entries: LazyPagingItems<DictionaryEntry>,
    modifier: Modifier = Modifier,
    onClickEntry: (DictionaryEntry) -> Unit = {}
) {
    LazyColumn(modifier = modifier) {
        items(entries) { entry ->
            if (entry != null) {
                DictionaryEntryCard(
                    dictionaryEntry = entry,
                    enabled = false,
                    modifier = Modifier.clickable(onClick = { onClickEntry(entry) })
                )
            }
        }
    }
}

@Preview
@Composable
private fun SearchHeaderPreview() {
    var restriction: SearchRestriction? by remember {
        mutableStateOf(SearchRestriction(wordclass, "Noun"))
    }

    BalalaikaTheme {
        Surface {
            SearchHeader(
                query = "Query",
                restriction = restriction,
                onClickRestriction = { restriction = null }
            )
        }
    }
}

@Preview
@Composable
private fun SearchEmptyMessagePreview() {
    BalalaikaTheme {
        Surface {
            SearchEmptyMessage()
        }
    }
}
