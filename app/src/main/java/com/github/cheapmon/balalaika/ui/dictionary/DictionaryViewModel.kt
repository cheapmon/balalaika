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
import com.github.cheapmon.balalaika.data.repositories.BookmarkRepository
import com.github.cheapmon.balalaika.data.repositories.ConfigRepository
import com.github.cheapmon.balalaika.data.repositories.DictionaryEntryRepository
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import com.github.cheapmon.balalaika.data.repositories.WordnetRepository
import com.github.cheapmon.balalaika.data.result.LoadState
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.DictionaryView
import com.github.cheapmon.balalaika.model.Property
import com.github.cheapmon.balalaika.model.WordnetInfo
import com.github.cheapmon.balalaika.util.navArgs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/** View model for [DictionaryFragment] */
class DictionaryViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val dictionaries: DictionaryRepository,
    private val entries: DictionaryEntryRepository,
    private val bookmarks: BookmarkRepository,
    private val config: ConfigRepository,
    private val wordnet: WordnetRepository
) : ViewModel() {
    private val navArgs: DictionaryFragmentArgs by navArgs(savedStateHandle)

    private val _dictionaryView: MutableStateFlow<DictionaryView?> = MutableStateFlow(null)
    private val _category: MutableStateFlow<DataCategory?> = MutableStateFlow(null)

    private val _initialEntry: MutableStateFlow<DictionaryEntry?> = MutableStateFlow(navArgs.entry)

    /** Current selected dictionary view */
    val dictionaryView: Flow<DictionaryView?> = _dictionaryView

    /** Current selected category to order by */
    val category: Flow<DataCategory?> = _category

    /**
     * Adapt dictionary presentation to user configuration
     *
     * The view is only updated if one of these settings changed:
     * - Dictionary view
     * - Category to order by
     * - Initial entry
     */
    val dictionaryEntries =
        combine(
            dictionaries.getOpenDictionary(),
            dictionaryView,
            category,
            _initialEntry
        ) { d, v, c, i ->
            if (d == null || v == null || c == null) {
                flowOf(null)
            } else {
                entries.getDictionaryEntries(d, v, c, i).cachedIn(viewModelScope)
            }
        }.flatMapLatest { it }

    /** Set the dictionary view */
    fun setDictionaryView(dictionaryView: DictionaryView) = viewModelScope.launch {
        _dictionaryView.value = dictionaryView
    }

    /** Set the dictionary ordering */
    fun setCategory(category: DataCategory) = viewModelScope.launch {
        _category.value = category
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
    suspend fun getCategories(): List<DataCategory>? =
        dictionaries.getOpenDictionary().first()?.let {
            config.getSortCategories(it).first()
        }

    /** All available dictionary views */
    suspend fun getDictionaryViews(): List<DictionaryView>? =
        dictionaries.getOpenDictionary().first()?.let {
            config.getDictionaryViews(it).first()
        }

    /** Load Wordnet information for a word */
    fun getWordnetData(property: Property.Wordnet): Flow<LoadState<WordnetInfo, Throwable>> =
        wordnet.getWordnetData(property.url)
}
