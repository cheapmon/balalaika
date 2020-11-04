package com.github.cheapmon.balalaika.components

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.RadioButton
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.theme.itemPadding
import com.github.cheapmon.balalaika.theme.itemSpacing
import com.github.cheapmon.balalaika.theme.simpleShape

@Composable
fun SimpleDialog(
    title: String,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
    text: @Composable () -> Unit = {}
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
        title = { Text(text = title) },
        text = text
    )
}

@Composable
fun <T> ChoiceDialog(
    title: String,
    items: List<T>,
    selectedItem: T?,
    itemName: (T) -> String = { it.toString() },
    onItemSelected: (T) -> Unit = {},
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
    emptyMessage: @Composable () -> Unit = {}
) {
    SimpleDialog(
        title = title,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(itemSpacing)) {
            if (items.isEmpty()) {
                emptyMessage()
            } else {
                items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(simpleShape)
                            .clickable(onClick = { onItemSelected(item) })
                            .padding(itemPadding / 4),
                        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = item == selectedItem,
                            onClick = { onItemSelected(item) }
                        )
                        Text(text = itemName(item))
                    }
                }
            }
        }
    }
}