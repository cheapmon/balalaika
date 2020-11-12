package com.github.cheapmon.balalaika.ui.selection

import androidx.compose.material.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.components.CollapsibleCard
import com.github.cheapmon.balalaika.model.SimpleDictionary
import com.github.cheapmon.balalaika.theme.*
import com.github.cheapmon.balalaika.util.DarkThemeProvider
import com.github.cheapmon.balalaika.util.dictCC

@Composable
fun DictionaryCard(
    dictionary: SimpleDictionary,
    modifier: Modifier = Modifier,
    actions: @Composable (SimpleDictionary) -> Unit = {}
) {
    CollapsibleCard(
        id = dictionary.id,
        modifier = modifier,
        header = { DictionaryCardHeader(dictionary = dictionary) }
    ) {
        DictionaryCardBody(
            dictionary = dictionary,
            actions = actions
        )
    }
}

@Composable
private fun DictionaryCardHeader(
    dictionary: SimpleDictionary,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = dictionary.name,
                style = MaterialTypography.h6
            )
            Text(
                text = dictionary.authors,
                style = MaterialTypography.subtitle1,
                color = MaterialColors.onSurfaceLight
            )
        }
    }
}

@Composable
private fun DictionaryCardBody(
    dictionary: SimpleDictionary,
    modifier: Modifier = Modifier,
    actions: @Composable (SimpleDictionary) -> Unit = {}
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(paragraphSpacing)) {
        InfoItem(
            title = stringResource(id = R.string.selection_summary_title),
            body = dictionary.summary
        )
        InfoItem(
            title = stringResource(id = R.string.selection_info_title),
            body = dictionary.additionalInfo
        )
        InfoItem(
            title = stringResource(id = R.string.selection_version_title),
            body = dictionary.version.toString()
        )
        actions(dictionary)
    }
}

@Composable
private fun InfoItem(
    title: String,
    body: String
) {
    Column {
        Text(
            text = title.toUpperCase(Locale.current),
            style = MaterialTypography.overline,
            color = MaterialColors.onSurfaceLight
        )
        Text(
            text = body,
            style = MaterialTypography.body2,
            textAlign = TextAlign.Justify
        )
    }
}

@Preview
@Composable
private fun DictionaryCardPreview(
    @PreviewParameter(DarkThemeProvider::class) darkTheme: Boolean
) {
    BalalaikaTheme(darkTheme = darkTheme) {
        DictionaryCard(dictionary = dictCC)
    }
}
