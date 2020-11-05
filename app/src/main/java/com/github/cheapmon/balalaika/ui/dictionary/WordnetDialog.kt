/*
 * Copyright 2020 Simon Kaleschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cheapmon.balalaika.ui.dictionary

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.ExperimentalLazyDsl
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.result.LoadState
import com.github.cheapmon.balalaika.data.result.Result
import com.github.cheapmon.balalaika.model.WordnetInfo
import com.github.cheapmon.balalaika.theme.*
import com.github.cheapmon.balalaika.util.DarkThemeProvider
import com.github.cheapmon.balalaika.util.exhaustive
import com.github.cheapmon.balalaika.util.sampleWordnetInfo
import kotlinx.coroutines.flow.Flow

@Composable
fun WordnetDialog(
    word: String,
    payload: Flow<LoadState<WordnetInfo, Throwable>>,
    onDismiss: () -> Unit = {}
) {
    val loadState by payload.collectAsState(initial = LoadState.Init())

    WordnetDialog(word, loadState, onDismiss = onDismiss)
}

@Composable
private fun WordnetDialog(
    word: String,
    loadState: LoadState<WordnetInfo, Throwable>,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.affirm))
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.dictionary_wordnet_title, word),
                style = MaterialTheme.typography.h5
            )
        },
        text = {
            Box(modifier = Modifier.preferredHeightIn(max = dialogMaxHeight)) {
                when (loadState) {
                    is LoadState.Finished -> {
                        when (val data = loadState.data) {
                            is Result.Error -> {
                                Text(text = stringResource(id = R.string.dictionary_wordnet_empty))
                            }
                            is Result.Success -> {
                                WordnetInfo(data.data)
                            }
                        }.exhaustive
                    }
                    else -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }.exhaustive
            }
        }
    )
}

@OptIn(ExperimentalLazyDsl::class)
@Composable
private fun WordnetInfo(
    data: WordnetInfo,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        if (data.entries.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(id = R.string.dictionary_wordnet_words),
                    style = MaterialTheme.typography.h6
                )
            }
            data.entries.forEach { entry ->
                item {
                    Row(
                        modifier = Modifier.fillParentMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(itemSpacing)
                    ) {
                        Text(text = entry.representation, style = MaterialTheme.typography.body2)
                        Text(
                            text = entry.partOfSpeech,
                            style = MaterialTheme.typography.caption,
                            color = SubtitleColor()
                        )
                    }
                }
            }
        }
        if (data.entries.isNotEmpty() && data.definitions.isNotEmpty()) {
            item { Spacer(modifier = Modifier.preferredHeight(itemPadding)) }
        }
        if (data.definitions.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(id = R.string.dictionary_wordnet_definitions),
                    style = MaterialTheme.typography.h6
                )
            }
            data.definitions.forEach { definition ->
                item {
                    Text(
                        text = "\uA78F ${definition.explanation}",
                        style = MaterialTheme.typography.body2
                    )
                }
                definition.examples.forEach {
                    item {
                        Text(
                            text = "\u25CB $it",
                            style = MaterialTheme.typography.caption,
                            color = SubtitleColor(),
                            modifier = Modifier.padding(start = itemPadding)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun WordnetInfoPreview(
    @PreviewParameter(DarkThemeProvider::class) darkTheme: Boolean
) {
    BalalaikaTheme(darkTheme = darkTheme) {
        Surface {
            WordnetInfo(data = sampleWordnetInfo)
        }
    }
}
