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
package com.github.cheapmon.balalaika.ui.search

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.cheapmon.balalaika.data.repositories.BookmarkRepository
import com.github.cheapmon.balalaika.data.repositories.DictionaryEntryRepository
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import com.github.cheapmon.balalaika.data.repositories.WordnetRepository
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.model.InstalledDictionary
import com.github.cheapmon.balalaika.model.SearchRestriction
import com.github.cheapmon.balalaika.ui.DefaultWordnetViewModel
import com.github.cheapmon.balalaika.ui.WordnetViewModel
import com.github.cheapmon.balalaika.ui.bookmarks.BookmarksViewModel
import com.github.cheapmon.balalaika.ui.bookmarks.DefaultBookmarksViewModel
import com.github.cheapmon.balalaika.util.navArgs
import kotlinx.coroutines.flow.*

/** View model for [SearchFragment] */
class SearchViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    dictionaries: DictionaryRepository,
    entries: DictionaryEntryRepository,
    bookmarks: BookmarkRepository,
    wordnet: WordnetRepository
) : ViewModel(),
    WordnetViewModel by DefaultWordnetViewModel(wordnet),
    BookmarksViewModel by DefaultBookmarksViewModel(dictionaries, bookmarks, wordnet) {
    private val navArgs: SearchFragmentArgs by navArgs(savedStateHandle)

    private val _query: MutableStateFlow<String?> =
        MutableStateFlow(navArgs.query)
    private val _restriction: MutableStateFlow<SearchRestriction?> =
        MutableStateFlow(navArgs.restriction)

    private val _refresh = MutableStateFlow(false)

    /** Current search query */
    val query: Flow<String?> = _query

    /** Current search restriction */
    val restriction: Flow<SearchRestriction?> = _restriction

    /** Current active dictionary */
    private val openedDictionary: Flow<InstalledDictionary?> =
        dictionaries.getOpenDictionary()

    /** Current dictionary, depending on the user configuration */
    val entries: Flow<PagingData<DictionaryEntry>?> =
        combine(openedDictionary, query, restriction, _refresh) { d, q, r, _ ->
            if (d == null) {
                flowOf(null)
            } else {
                _refresh.flatMapLatest {
                    entries.queryDictionaryEntries(d, q ?: "", r).cachedIn(viewModelScope)
                }
            }
        }.flatMapLatest { it }

    /** Set the search query */
    fun setQuery(query: String?) {
        _query.value = query
    }

    /** Set the search restriction */
    fun setRestriction(restriction: SearchRestriction?) {
        _restriction.value = restriction
    }

    fun refresh() {
        _refresh.value = !_refresh.value
    }
}
