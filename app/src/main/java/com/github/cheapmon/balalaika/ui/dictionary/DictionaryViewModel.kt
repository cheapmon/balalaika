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

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.cheapmon.balalaika.data.repositories.*
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.DictionaryView
import com.github.cheapmon.balalaika.ui.DefaultWordnetViewModel
import com.github.cheapmon.balalaika.ui.WordnetViewModel
import com.github.cheapmon.balalaika.util.navArgs
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/** View model for [DictionaryFragment] */
class DictionaryViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val dictionaries: DictionaryRepository,
    private val entries: DictionaryEntryRepository,
    private val bookmarks: BookmarkRepository,
    private val config: ConfigRepository,
    wordnet: WordnetRepository
) : ViewModel(), WordnetViewModel by DefaultWordnetViewModel(wordnet) {
    private val navArgs: DictionaryFragmentArgs by navArgs(savedStateHandle)

    private val _initialEntry: MutableStateFlow<DictionaryEntry?> = MutableStateFlow(navArgs.entry)

    /** Current selected dictionary view */
    val dictionaryView: Flow<DictionaryView?> = dictionaries.getOpenDictionary()
        .flatMapLatest { dictionary ->
            dictionary?.let { config.getDictionaryView(it) } ?: flowOf(null)
        }

    /** Current selected category to order by */
    val category: Flow<DataCategory?> = dictionaries.getOpenDictionary()
        .flatMapLatest { dictionary ->
            dictionary?.let { config.getSortCategory(it) } ?: flowOf(null)
        }

    private val _refresh = MutableStateFlow(false)

    fun refresh() {
        _refresh.value = !_refresh.value
    }

    /**
     * Adapt dictionary presentation to user configuration
     *
     * The view is only updated if one of these settings changed:
     * - Dictionary view
     * - Category to order by
     * - Initial entry
     */
    val dictionaryEntries =
        combine(dictionaries.getOpenDictionary(), _initialEntry) { d, i ->
            if (d == null) {
                flowOf(null)
            } else {
                combine(dictionaryView, category, _refresh) { v, c, _ ->
                    if (v == null || c == null) {
                        flowOf(null)
                    } else {
                        entries.getDictionaryEntries(d, v, c, i).cachedIn(viewModelScope)
                    }
                }.flatMapLatest { it }
            }
        }.flatMapLatest { it }

    /** Set the dictionary view */
    fun setDictionaryView(dictionaryView: DictionaryView) {
        viewModelScope.launch {
            val dictionary = dictionaries.getOpenDictionary().first() ?: return@launch
            val category = category.first() ?: return@launch

            config.updateConfig(dictionary, category, dictionaryView)
        }
    }

    /** Set the dictionary ordering */
    fun setCategory(category: DataCategory) {
        viewModelScope.launch {
            val dictionary = dictionaries.getOpenDictionary().first() ?: return@launch
            val view = dictionaryView.first() ?: return@launch

            config.updateConfig(dictionary, category, view)
        }
    }

    /** Set the first entry to display */
    fun setInitialEntry(dictionaryEntry: DictionaryEntry?) = viewModelScope.launch {
        _initialEntry.value = dictionaryEntry
    }

    /** Toggle bookmark state for a lexeme */
    fun toggleBookmark(dictionaryEntry: DictionaryEntry) = viewModelScope.launch {
        val dictionary = dictionaries.getOpenDictionary().first() ?: return@launch
        if (dictionaryEntry.bookmark == null) {
            bookmarks.addBookmark(dictionary, dictionaryEntry)
        } else {
            bookmarks.removeBookmark(dictionary, dictionaryEntry)
        }
    }

    /** All available sortable categories */
    fun getCategories(): Flow<List<DataCategory>?> =
        dictionaries.getOpenDictionary().flatMapLatest {
            it?.let { config.getSortCategories(it) } ?: flowOf(null)
        }

    /** All available dictionary views */
    fun getDictionaryViews(): Flow<List<DictionaryView>?> =
        dictionaries.getOpenDictionary().flatMapLatest {
            it?.let { config.getDictionaryViews(it) } ?: flowOf(null)
        }
}
