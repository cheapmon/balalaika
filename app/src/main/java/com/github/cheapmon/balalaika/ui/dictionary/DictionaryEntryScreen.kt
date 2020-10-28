package com.github.cheapmon.balalaika.ui.dictionary

import androidx.compose.foundation.Text
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.navigation.NavController
import androidx.ui.tooling.preview.Preview
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.components.DictionaryEntryCard
import com.github.cheapmon.balalaika.components.EmptyMessage
import com.github.cheapmon.balalaika.components.PropertyAction
import com.github.cheapmon.balalaika.components.emptyPropertyAction
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.Property
import com.github.cheapmon.balalaika.theme.BalalaikaTheme
import com.github.cheapmon.balalaika.ui.BalalaikaScaffold
import com.github.cheapmon.balalaika.util.LazyPagingItems
import com.github.cheapmon.balalaika.util.collectAsLazyPagingItems
import com.github.cheapmon.balalaika.util.items
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach

@Composable
fun DictionaryEntryScreen(
    viewModel: DictionaryViewModel,
    navController: NavController,
    onClickBase: (DictionaryEntry) -> Unit = {},
    onBookmark: (DictionaryEntry) -> Unit = {},
    onClickProperty: PropertyAction<Property> = emptyPropertyAction(),
    onOpenDictionaries: () -> Unit = {}
) {
    var empty by remember { mutableStateOf(true) }
    val entries = viewModel.dictionaryEntries
        .onEach { empty = it == null }
        .filterNotNull()
        .collectAsLazyPagingItems()

    val wordnetParam: Property.Wordnet? by viewModel.wordnetParam.collectAsState(initial = null)

    BalalaikaScaffold(
        navController = navController,
        title = stringResource(id = R.string.menu_dictionary)
    ) {
        if (!empty) {
            DictionaryEntryList(
                entries = entries,
                onClickBase = onClickBase,
                onBookmark = onBookmark,
                onClickProperty = onClickProperty
            )
        } else {
            DictionaryEntryEmptyMessage(onOpenDictionaries)
        }
        DictionaryEntryDialog(
            viewModel = viewModel,
            wordnetParam = wordnetParam,
            onDismiss = { viewModel.setWordnetParam(null) }
        )
    }
}

@OptIn(ExperimentalLazyDsl::class)
@Composable
private fun DictionaryEntryList(
    entries: LazyPagingItems<DictionaryEntry>,
    modifier: Modifier = Modifier,
    onClickBase: (DictionaryEntry) -> Unit = {},
    onBookmark: (DictionaryEntry) -> Unit = {},
    onClickProperty: PropertyAction<Property> = emptyPropertyAction()
) {
    LazyColumn(modifier = modifier) {
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
private fun DictionaryEntryEmptyMessage(
    onOpenDictionaries: () -> Unit = {}
) {
    EmptyMessage(icon = Icons.Default.MenuBook) {
        Text(text = stringResource(id = R.string.dictionary_empty))
        TextButton(onClick = onOpenDictionaries) {
            Text(
                text = stringResource(id = R.string.dictionary_select_dictionary)
                    .toUpperCase(Locale.current)
            )
        }
    }
}

@Composable
private fun DictionaryEntryDialog(
    viewModel: DictionaryViewModel,
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

@Preview
@Composable
private fun DictionaryEntryEmptyMessagePreview() {
    BalalaikaTheme {
        Surface {
            DictionaryEntryEmptyMessage()
        }
    }
}
