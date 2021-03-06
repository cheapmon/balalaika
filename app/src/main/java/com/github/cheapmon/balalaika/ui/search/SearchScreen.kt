package com.github.cheapmon.balalaika.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.annotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.components.*
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.Property
import com.github.cheapmon.balalaika.model.SearchRestriction
import com.github.cheapmon.balalaika.theme.BalalaikaTheme
import com.github.cheapmon.balalaika.theme.MaterialColors
import com.github.cheapmon.balalaika.theme.itemPadding
import com.github.cheapmon.balalaika.theme.itemSpacing
import com.github.cheapmon.balalaika.ui.BalalaikaScaffold
import com.github.cheapmon.balalaika.util.DarkThemeProvider
import com.github.cheapmon.balalaika.util.wordclass
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel,
    onQuerySubmit: (String?, SearchRestriction?) -> Unit = { _, _ -> },
    onClickBase: (DictionaryEntry) -> Unit = {},
    onBookmark: (DictionaryEntry) -> Unit = {},
    onClickProperty: PropertyAction<Property> = emptyPropertyAction()
) {
    val query: String? by viewModel.query.collectAsState(initial = null)
    val restriction: SearchRestriction? by viewModel.restriction.collectAsState(initial = null)
    val wordnetParam: Property.Wordnet? by viewModel.wordnetParam.collectAsState(initial = null)

    var empty: Boolean by remember { mutableStateOf(false) }

    val entries = viewModel.entries
        .onEach { empty = it == null }
        .filterNotNull()
        .collectAsLazyPagingItems()

    BalalaikaScaffold(
        navController = navController,
        title = stringResource(id = R.string.menu_search)
    ) {
        Column {
            SearchHeader(
                query = query,
                onQueryChange = { viewModel.setQuery(it) },
                onQuerySubmit = onQuerySubmit,
                restriction = restriction,
                onClickRestriction = { viewModel.setRestriction(null) }
            )
            DictionaryEntryList(
                entries = entries,
                isEmpty = empty,
                onClickBase = onClickBase,
                onBookmark = onBookmark,
                onClickProperty = onClickProperty,
                dialog = {
                    SearchWordnetDialog(
                        viewModel = viewModel,
                        wordnetParam = wordnetParam,
                        onDismiss = { viewModel.setWordnetParam(null) }
                    )
                },
                emptyMessage = { SearchEmptyMessage() }
            )
        }
    }

    onDispose {
        onQuerySubmit(query, restriction)
    }
}

@Composable
private fun SearchHeader(
    query: String?,
    onQueryChange: (String?) -> Unit = {},
    onQuerySubmit: (String?, SearchRestriction?) -> Unit = { _, _ -> },
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
                onValueChange = { onQueryChange(it) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                onImeActionPerformed = { _, controller ->
                    controller?.hideSoftwareKeyboard()
                    onQuerySubmit(query, restriction)
                },
                label = { Text(text = stringResource(R.string.search_query)) },
                maxLines = 1,
                trailingIcon = {
                    IconButton(onClick = { onQueryChange(null) }) {
                        Icon(asset = Icons.Default.Close)
                    }
                }
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

@Composable
private fun SearchWordnetDialog(
    viewModel: SearchViewModel,
    wordnetParam: Property.Wordnet?,
    onDismiss: () -> Unit = {}
) {
    if (wordnetParam != null) {
        val payload = viewModel.getWordnetData(wordnetParam)
        WordnetDialog(
            word = wordnetParam.name,
            payload = payload,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun highlightQuery(text: String, query: String?): AnnotatedString {
    val queryLower = query?.toLowerCase(Locale.current) ?: ""
    val textLower = text.toLowerCase(Locale.current)

    return if (queryLower == "" || !textLower.contains(queryLower)) {
        AnnotatedString(text)
    } else {
        annotatedString {
            // Split before and after text
            text.split(Regex("(?<=$query)|(?=$query)", RegexOption.IGNORE_CASE)).forEach {
                if (it.toLowerCase(Locale.current) == queryLower) {
                    pushStyle(SpanStyle(color = MaterialColors.primary))
                    append(it)
                    pop()
                } else append(it)
            }
        }
    }
}

@Preview
@Composable
private fun SearchHeaderPreview(
    @PreviewParameter(DarkThemeProvider::class) darkTheme: Boolean
) {
    var restriction: SearchRestriction? by remember {
        mutableStateOf(SearchRestriction(wordclass, "Noun"))
    }

    BalalaikaTheme(darkTheme = darkTheme) {
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
private fun SearchEmptyMessagePreview(
    @PreviewParameter(DarkThemeProvider::class) darkTheme: Boolean
) {
    BalalaikaTheme(darkTheme = darkTheme) {
        Surface {
            SearchEmptyMessage()
        }
    }
}
