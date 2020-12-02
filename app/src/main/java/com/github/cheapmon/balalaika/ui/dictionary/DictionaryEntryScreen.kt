package com.github.cheapmon.balalaika.ui.dictionary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ViewArray
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.github.cheapmon.balalaika.MainViewModel
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.components.*
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.DictionaryView
import com.github.cheapmon.balalaika.model.Property
import com.github.cheapmon.balalaika.theme.BalalaikaTheme
import com.github.cheapmon.balalaika.theme.MaterialTypography
import com.github.cheapmon.balalaika.ui.BalalaikaScaffold
import com.github.cheapmon.balalaika.util.DarkThemeProvider
import com.github.cheapmon.balalaika.util.exhaustive
import kotlinx.coroutines.flow.filterNotNull

private enum class DictionaryDialogState {
    NONE,
    ORDER,
    VIEW
}

@Composable
fun DictionaryEntryScreen(
    activityViewModel: MainViewModel,
    navController: NavController,
    onNavigateToSearch: () -> Unit = {},
    onClickBase: (DictionaryEntry) -> Unit = {},
    onBookmark: (DictionaryEntry) -> Unit = {},
    onClickProperty: PropertyAction<Property> = emptyPropertyAction(),
    onOpenDictionaries: () -> Unit = {}
) {
    val viewModel: DictionaryViewModel = viewModel()

    val entries = viewModel.dictionaryEntries
        .filterNotNull()
        .collectAsLazyPagingItems()

    val (dialogState, onChangeDialogState) = remember { mutableStateOf(DictionaryDialogState.NONE) }
    val wordnetParam: Property.Wordnet? by viewModel.wordnetParam.collectAsState(initial = null)

    val dictionary by activityViewModel.currentDictionary.observeAsState()

    BalalaikaScaffold(
        navController = navController,
        title = {
            Column(modifier = Modifier.clickable(onClick = { navController.navigate(R.id.nav_selection) })) {
                when (val d = dictionary) {
                    null -> {
                        Text(text = stringResource(id = R.string.menu_dictionary))
                    }
                    else -> {
                        Text(
                            text = stringResource(id = R.string.menu_dictionary),
                            style = MaterialTypography.body2
                        )
                        Text(
                            text = d.name,
                            style = MaterialTypography.caption,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        },
        actions = {
            IconButton(onClick = { onChangeDialogState(DictionaryDialogState.ORDER) }) {
                Icon(asset = Icons.Default.Sort)
            }
            IconButton(onClick = { onChangeDialogState(DictionaryDialogState.VIEW) }) {
                Icon(asset = Icons.Default.ViewArray)
            }
            IconButton(onClick = onNavigateToSearch) {
                Icon(asset = Icons.Default.Search)
            }
        }
    ) {
        DictionaryEntryList(
            entries = entries,
            onClickBase = onClickBase,
            onBookmark = onBookmark,
            onClickProperty = onClickProperty,
            dialog = {
                DictionaryEntryDialog(
                    viewModel = viewModel,
                    wordnetParam = wordnetParam,
                    onDismiss = { viewModel.setWordnetParam(null) }
                )
            },
            emptyMessage = { DictionaryEntryEmptyMessage(onOpenDictionaries) }
        )
        DictionaryDialog(viewModel, dialogState, onChangeDialogState)
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

@Composable
private fun DictionaryDialog(
    viewModel: DictionaryViewModel,
    state: DictionaryDialogState = DictionaryDialogState.NONE,
    onChangeState: (DictionaryDialogState) -> Unit = {}
) {
    val categories: List<DataCategory>? by viewModel.getCategories().collectAsState(initial = null)
    val category: DataCategory? by viewModel.category.collectAsState(initial = null)
    val onChangeCategory: (DataCategory) -> Unit = { viewModel.setCategory(it) }

    val views: List<DictionaryView>? by viewModel.getDictionaryViews()
        .collectAsState(initial = null)
    val view: DictionaryView? by viewModel.dictionaryView.collectAsState(initial = null)
    val onChangeView: (DictionaryView) -> Unit = { viewModel.setDictionaryView(it) }

    val dismiss = { onChangeState(DictionaryDialogState.NONE) }

    BalalaikaTheme {
        Surface {
            when (state) {
                DictionaryDialogState.NONE -> {
                }
                DictionaryDialogState.ORDER -> {
                    ChoiceDialog(
                        title = stringResource(id = R.string.menu_order_by),
                        items = categories.orEmpty(),
                        selectedItem = category,
                        itemName = { it.name },
                        onItemSelected = onChangeCategory,
                        onConfirm = dismiss,
                        onDismiss = dismiss
                    ) { Text(text = stringResource(id = R.string.dictionary_empty)) }
                }
                DictionaryDialogState.VIEW -> {
                    ChoiceDialog(
                        title = stringResource(id = R.string.menu_setup_view),
                        items = views.orEmpty(),
                        selectedItem = view,
                        itemName = { it.name },
                        onItemSelected = onChangeView,
                        onConfirm = dismiss,
                        onDismiss = dismiss
                    ) { Text(text = stringResource(id = R.string.dictionary_empty)) }
                }
            }.exhaustive
        }
    }
}

@Preview
@Composable
private fun DictionaryEntryEmptyMessagePreview(
    @PreviewParameter(DarkThemeProvider::class) darkTheme: Boolean
) {
    BalalaikaTheme(darkTheme = darkTheme) {
        Surface {
            DictionaryEntryEmptyMessage()
        }
    }
}
