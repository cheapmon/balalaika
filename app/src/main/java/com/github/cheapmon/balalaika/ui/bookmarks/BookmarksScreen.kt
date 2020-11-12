package com.github.cheapmon.balalaika.ui.bookmarks

import androidx.compose.material.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
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
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.theme.*
import com.github.cheapmon.balalaika.ui.BalalaikaScaffold
import com.github.cheapmon.balalaika.util.DarkThemeProvider
import com.github.cheapmon.balalaika.util.sampleDictionaryEntries
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookmarksScreen(
    navController: NavController,
    onClickEntry: (DictionaryEntry) -> Unit = {}
) {
    val viewModel: BookmarksViewModel = viewModel()

    val entries by viewModel.bookmarkedEntries.observeAsState()
    var showDialog by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    BalalaikaScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        title = stringResource(id = R.string.menu_bookmarks),
        actions = {
            IconButton(onClick = { showDialog = true }) {
                Icon(asset = Icons.Default.Delete)
            }
        }
    ) {
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
        if (showDialog) {
            val doneMessage = stringResource(id = R.string.bookmarks_clear_done)

            BookmarksDialog(
                onConfirm = {
                    showDialog = false
                    viewModel.clearBookmarks()
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
private fun BookmarksDialog(onConfirm: () -> Unit = {}, onDismiss: () -> Unit = {}) {
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
            Text(text = stringResource(id = R.string.bookmarks_clear_title))
        }
    )
}

@Composable
private fun BookmarksEmptyMessage() {
    EmptyMessage(
        icon = Icons.Default.Bookmark
    ) {
        Text(
            text = stringResource(id = R.string.bookmarks_empty),
            style = MaterialTypography.body2
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
                        modifier = Modifier.preferredSize(listItemIconSize),
                        tint = MaterialColors.onSurfaceLight
                    )
                },
                secondaryText = entry.base?.let {
                    { Text(text = it.representation) }
                },
                trailing = {
                    IconButton(onClick = { onDeleteEntry(entry) }) {
                        Icon(
                            asset = Icons.Default.Delete,
                            tint = MaterialColors.onSurfaceLight
                        )
                    }
                }
            ) {
                Text(text = entry.representation)
            }
            Divider()
        }
    }
}

@Preview
@Composable
private fun BookmarksEmptyMessagePreview(
    @PreviewParameter(DarkThemeProvider::class) darkTheme: Boolean
) {
    BalalaikaTheme(darkTheme = darkTheme) {
        Surface {
            BookmarksEmptyMessage()
        }
    }
}

@Preview
@Composable
private fun BookmarksListPreview(
    @PreviewParameter(DarkThemeProvider::class) darkTheme: Boolean
) {
    BalalaikaTheme(darkTheme = darkTheme) {
        Surface {
            BookmarksList(entries = sampleDictionaryEntries.filter { it.bookmark != null })
        }
    }
}
