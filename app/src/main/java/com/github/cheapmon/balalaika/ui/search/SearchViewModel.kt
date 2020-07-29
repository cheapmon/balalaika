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
import androidx.paging.cachedIn
import com.github.cheapmon.balalaika.data.dictionary.DictionaryEntryRepository
import com.github.cheapmon.balalaika.data.search.SearchRepository
import com.github.cheapmon.balalaika.db.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.util.navArgs

/** View model for [SearchFragment] */
class SearchViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val searchRepository: SearchRepository,
    dictionaryEntryRepository: DictionaryEntryRepository
) : ViewModel() {
    private val navArgs: SearchFragmentArgs by navArgs(savedStateHandle)

    init {
        navArgs.query?.let { setQuery(it) }
        navArgs.restriction?.let { setRestriction(it) }
    }

    /** Current search query */
    val query = searchRepository.query

    /** Current search restriction */
    val restriction = searchRepository.restriction

    /** Current dictionary, depending on the user configuration */
    val dictionary = searchRepository.dictionary.cachedIn(viewModelScope)

    /** Current active dictionary */
    val currentDictionary = dictionaryEntryRepository.currentDictionary

    /** Set the search query */
    fun setQuery(query: String) =
        searchRepository.setQuery(query)

    /** Set the search restriction */
    fun setRestriction(restriction: SearchRestriction) =
        searchRepository.setRestriction(restriction)
}
