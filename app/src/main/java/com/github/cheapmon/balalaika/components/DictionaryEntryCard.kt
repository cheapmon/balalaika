package com.github.cheapmon.balalaika.components

import androidx.compose.material.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.github.cheapmon.balalaika.model.Bookmark
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.Property
import com.github.cheapmon.balalaika.theme.BalalaikaTheme
import com.github.cheapmon.balalaika.theme.MaterialColors
import com.github.cheapmon.balalaika.theme.MaterialTypography
import com.github.cheapmon.balalaika.theme.onSurfaceLight
import com.github.cheapmon.balalaika.util.DarkThemeProvider
import com.github.cheapmon.balalaika.util.happier
import java.util.*

@Composable
fun DictionaryEntryCard(
    dictionaryEntry: DictionaryEntry,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    transformText: @Composable (String) -> AnnotatedString = { AnnotatedString(it) },
    onClickBase: (DictionaryEntry) -> Unit = {},
    onBookmark: (DictionaryEntry) -> Unit = {},
    onClickProperty: PropertyAction<Property> = emptyPropertyAction()
) {
    val base = dictionaryEntry.base
    val bookmark = dictionaryEntry.bookmark

    CollapsibleCard(
        id = dictionaryEntry.id,
        initialState = CollapsibleCardState.EXPANDED,
        modifier = modifier,
        header = {
            Column {
                Text(
                    text = transformText(dictionaryEntry.representation),
                    style = MaterialTypography.h6
                )
                if (base != null) {
                    Text(
                        text = transformText(base.representation),
                        style = MaterialTypography.subtitle1,
                        color = MaterialColors.onSurfaceLight,
                        modifier = Modifier.clickable(
                            onClick = { onClickBase(base) },
                            enabled = enabled
                        )
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { onBookmark(dictionaryEntry) }, enabled = enabled) {
                val asset = if (bookmark == null) {
                    Icons.Default.BookmarkBorder
                } else {
                    Icons.Default.Bookmark
                }
                Icon(asset = asset)
            }
        }
    ) {
        PropertyList(
            properties = dictionaryEntry.properties,
            onClickProperty = onClickProperty,
            transformText = transformText
        )
    }
}

@Composable
private fun PropertyList(
    properties: SortedMap<DataCategory, List<Property>>,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    transformText: @Composable (String) -> AnnotatedString = { AnnotatedString(it) },
    onClickProperty: PropertyAction<Property> = emptyPropertyAction()
) {
    Column(modifier = modifier) {
        properties.forEach { (category, list) ->
            WidgetFor(
                category = category,
                properties = list,
                enabled = enabled,
                transformText = transformText,
                onEvent = onClickProperty
            )
        }
    }
}

@Preview
@Composable
private fun DictionaryEntryCardPreview(
    @PreviewParameter(DarkThemeProvider::class) darkTheme: Boolean
) {
    var entry by remember { mutableStateOf(happier) }

    BalalaikaTheme(darkTheme = darkTheme) {
        DictionaryEntryCard(
            dictionaryEntry = entry,
            onBookmark = {
                entry = entry.copy(bookmark = if (entry.bookmark == null) Bookmark() else null)
            }
        )
    }
}
