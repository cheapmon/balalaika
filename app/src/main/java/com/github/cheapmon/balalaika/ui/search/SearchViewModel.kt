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
import com.github.cheapmon.balalaika.data.entities.history.HistoryEntry
import com.github.cheapmon.balalaika.data.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.data.insert.ImportUtil
import com.github.cheapmon.balalaika.data.repositories.HistoryRepository
import com.github.cheapmon.balalaika.data.repositories.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/** View model for [SearchFragment] */
class SearchViewModel @ViewModelInject constructor(
    private val searchRepository: SearchRepository,
    private val historyRepository: HistoryRepository,
    private val importUtil: ImportUtil
) : ViewModel() {
    /**
     * Current state of import operations
     *
     * Calls the [import utility][ImportUtil] and emits `true` when finished.
     */
    val importFlow: Flow<Boolean> = flow {
        emit(false)
        importUtil.import()
        emit(true)
    }

    /** Current search query */
    val query = searchRepository.query

    /** Current search restriction */
    val restriction = searchRepository.restriction

    /**
     * Current dictionary, depending on the user configuration
     *
     * _Note_: This ensures that the dictionary is up-to-date by checking that any import
     * operations have been finished.
     */
    val dictionary = flow {
        val result = searchRepository.dictionary.cachedIn(viewModelScope)
        importFlow.collect { done -> if (done) result.collect { value -> emit(value) } }
    }

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
                        categoryId = restriction.category.categoryId,
                        restriction = restriction.restriction
                    )
            }
            historyRepository.removeSimilarEntries(entry)
            historyRepository.addEntry(entry)
        }
    }
}