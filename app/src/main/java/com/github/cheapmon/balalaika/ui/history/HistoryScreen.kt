package com.github.cheapmon.balalaika.ui.history

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
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.NavController
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.components.EmptyMessage
import com.github.cheapmon.balalaika.model.HistoryItem
import com.github.cheapmon.balalaika.model.SearchRestriction
import com.github.cheapmon.balalaika.theme.BalalaikaTheme
import com.github.cheapmon.balalaika.theme.IconColor
import com.github.cheapmon.balalaika.theme.listItemIconSize
import com.github.cheapmon.balalaika.ui.BalalaikaScaffold
import com.github.cheapmon.balalaika.util.DarkThemeProvider
import com.github.cheapmon.balalaika.util.sampleHistoryItems
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    onClickItem: (HistoryItem) -> Unit = {}
) {
    val viewModel: HistoryViewModel = viewModel()

    val items by viewModel.items.observeAsState()
    var showDialog by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    BalalaikaScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        title = stringResource(id = R.string.menu_history),
        actions = {
            IconButton(onClick = { showDialog = true }) {
                Icon(asset = Icons.Default.Delete)
            }
        }
    ) {
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

        val doneMessage = stringResource(id = R.string.history_clear_done)
        if (showDialog) {
            HistoryDialog(
                onConfirm = {
                    showDialog = false
                    viewModel.clearHistory()
                    scope.launch {
                        with(scaffoldState.snackbarHostState) {
                            currentSnackbarData?.dismiss()
                            showSnackbar(
                                message = doneMessage,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}

@Composable
private fun HistoryDialog(
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.affirm).toUpperCase(Locale.current))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.cancel).toUpperCase(Locale.current))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.history_clear_title))
        }
    )
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
                        modifier = Modifier.preferredSize(listItemIconSize),
                        tint = IconColor()
                    )
                },
                secondaryText = { Text(text = item.restriction.text()) },
                trailing = {
                    IconButton(onClick = { onDeleteItem(item) }) {
                        Icon(
                            asset = Icons.Default.Delete,
                            tint = IconColor()
                        )
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

@Preview
@Composable
private fun HistoryEmptyMessagePreview(
    @PreviewParameter(DarkThemeProvider::class) darkTheme: Boolean
) {
    BalalaikaTheme(darkTheme = darkTheme) {
        Surface {
            HistoryEmptyMessage()
        }
    }
}

@Preview
@Composable
private fun HistoryListPreview(
    @PreviewParameter(DarkThemeProvider::class) darkTheme: Boolean
) {
    BalalaikaTheme(darkTheme = darkTheme) {
        Surface {
            HistoryList(items = sampleHistoryItems)
        }
    }
}
