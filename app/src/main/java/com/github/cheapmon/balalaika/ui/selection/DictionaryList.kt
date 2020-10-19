package com.github.cheapmon.balalaika.ui.selection

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.ui.tooling.preview.Preview
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.components.EmptyMessage
import com.github.cheapmon.balalaika.model.DownloadableDictionary
import com.github.cheapmon.balalaika.model.InstalledDictionary
import com.github.cheapmon.balalaika.model.SimpleDictionary
import com.github.cheapmon.balalaika.theme.BalalaikaTheme
import com.github.cheapmon.balalaika.theme.DangerTheme
import com.github.cheapmon.balalaika.theme.itemSpacing
import com.github.cheapmon.balalaika.util.exhaustive
import com.github.cheapmon.balalaika.util.sampleDictionaries

@Composable
fun DictionaryList(
    dictionaries: List<SimpleDictionary>,
    modifier: Modifier = Modifier,
    onOpen: (InstalledDictionary) -> Unit = {},
    onClose: (InstalledDictionary) -> Unit = {},
    onAdd: (DownloadableDictionary) -> Unit = {},
    onRemove: (InstalledDictionary) -> Unit = {}
) {
    if (dictionaries.isEmpty()) {
        EmptyMessage(icon = Icons.Default.LibraryBooks) {
            Text(text = stringResource(id = R.string.selection_empty))
        }
    } else {
        LazyColumnFor(items = dictionaries, modifier = modifier) { dictionary ->
            when (dictionary) {
                is InstalledDictionary -> {
                    InstalledDictionaryCard(dictionary, Modifier, onOpen, onClose, onRemove)
                }
                is DownloadableDictionary -> {
                    DownloadableDictionaryCard(dictionary, Modifier, onAdd)
                }
                else -> {
                }
            }.exhaustive
        }
    }
}

@Composable
private fun DownloadableDictionaryCard(
    dictionary: DownloadableDictionary,
    modifier: Modifier = Modifier,
    onAdd: (DownloadableDictionary) -> Unit = {}
) {
    DictionaryCard(
        dictionary = dictionary,
        modifier = modifier
    ) {
        InstallActions(
            dictionary = dictionary,
            onAdd = onAdd
        )
    }
}

@Composable
private fun InstalledDictionaryCard(
    dictionary: InstalledDictionary,
    modifier: Modifier = Modifier,
    onOpen: (InstalledDictionary) -> Unit = {},
    onClose: (InstalledDictionary) -> Unit = {},
    onRemove: (InstalledDictionary) -> Unit = {}
) {
    DictionaryCard(
        dictionary = dictionary,
        modifier = modifier
    ) {
        ReadActions(
            dictionary = dictionary,
            onOpen = onOpen,
            onClose = onClose,
            onRemove = onRemove
        )
    }
}

@OptIn(ExperimentalLayout::class)
@Composable
private fun InstallActions(
    dictionary: DownloadableDictionary,
    onAdd: (DownloadableDictionary) -> Unit = {}
) {
    FlowRow(mainAxisSpacing = itemSpacing) {
        TextButton(onClick = { onAdd(dictionary) }, enabled = !dictionary.isInLibrary) {
            Text(text = stringResource(id = R.string.selection_action_add).toUpperCase(Locale.current))
        }
    }
}

@OptIn(ExperimentalLayout::class)
@Composable
private fun ReadActions(
    dictionary: InstalledDictionary,
    onOpen: (InstalledDictionary) -> Unit = {},
    onClose: (InstalledDictionary) -> Unit = {},
    onRemove: (InstalledDictionary) -> Unit = {}
) {
    FlowRow(mainAxisSpacing = itemSpacing) {
        if (dictionary.isOpened) {
            TextButton(onClick = { onClose(dictionary) }) {
                Text(
                    text = stringResource(id = R.string.selection_action_deactivate)
                        .toUpperCase(Locale.current)
                )
            }
        } else {
            TextButton(onClick = { onOpen(dictionary) }) {
                Text(
                    text = stringResource(id = R.string.selection_action_activate)
                        .toUpperCase(Locale.current)
                )
            }
        }
        DangerTheme {
            TextButton(onClick = { onRemove(dictionary) }) {
                Text(
                    text = stringResource(id = R.string.selection_action_remove)
                        .toUpperCase(Locale.current)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DictionaryListPreview() {
    BalalaikaTheme {
        DictionaryList(dictionaries = sampleDictionaries)
    }
}