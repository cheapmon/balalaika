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

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.cheapmon.balalaika.data.history.HistoryRepository
import com.github.cheapmon.balalaika.data.search.SearchRepository
import com.github.cheapmon.balalaika.db.entities.history.HistoryEntry
import com.github.cheapmon.balalaika.db.entities.history.SearchRestriction
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** View model for [SearchFragment] */
class SearchViewModel @ViewModelInject constructor(
    private val searchRepository: SearchRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    /** Current search query */
    val query = searchRepository.query

    /** Current search restriction */
    val restriction = searchRepository.restriction

    /** Current dictionary, depending on the user configuration */
    val dictionary = searchRepository.dictionary.cachedIn(viewModelScope)

    /** Set the search query */
    fun setQuery(query: String) =
        searchRepository.setQuery(query)

    /** Set the search restriction */
    fun setRestriction(restriction: SearchRestriction) =
        searchRepository.setRestriction(restriction)

    /** Add search to history */
    fun addToHistory() {
        viewModelScope.launch {
            val query = this@SearchViewModel.query.first()
            val restriction = this@SearchViewModel.restriction.first()
            if (query.isBlank()) return@launch
            val entry = when (restriction) {
                is SearchRestriction.None ->
                    HistoryEntry(query = query)
                is SearchRestriction.Some ->
                    HistoryEntry(
                        query = query,
                        categoryId = restriction.category.id,
                        restriction = restriction.restriction
                    )
            }
            historyRepository.removeSimilarEntries(entry)
            historyRepository.addEntry(entry)
        }
    }
}
