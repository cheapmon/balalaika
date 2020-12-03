package com.github.cheapmon.balalaika.ui.bookmarks

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.viewinterop.viewModel
import androidx.navigation.NavController
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.components.*
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.Property
import com.github.cheapmon.balalaika.theme.BalalaikaTheme
import com.github.cheapmon.balalaika.theme.MaterialTypography
import com.github.cheapmon.balalaika.ui.BalalaikaScaffold
import com.github.cheapmon.balalaika.util.DarkThemeProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookmarksScreen(
    navController: NavController,
    onClickBase: (DictionaryEntry) -> Unit = {},
    onClickProperty: PropertyAction<Property> = emptyPropertyAction()
) {
    val viewModel: DefaultBookmarksViewModel = viewModel()

    val entries by viewModel.bookmarkedEntries.collectAsState(initial = emptyList())
    val wordnetParam: Property.Wordnet? by viewModel.wordnetParam.collectAsState(initial = null)
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
        DictionaryEntryList(
            entries = entries,
            onClickBase = onClickBase,
            onBookmark = { viewModel.toggleBookmark(it) },
            onClickProperty = onClickProperty,
            dialog = { BookmarksWordnetDialog(viewModel, wordnetParam) },
            emptyMessage = { BookmarksEmptyMessage() }
        )
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
private fun BookmarksWordnetDialog(
    viewModel: DefaultBookmarksViewModel,
    wordnetParam: Property.Wordnet?
) {
    if (wordnetParam != null) {
        val payload = viewModel.getWordnetData(wordnetParam)
        WordnetDialog(
            word = wordnetParam.name,
            payload = payload,
            onDismiss = { viewModel.setWordnetParam(null) }
        )
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
