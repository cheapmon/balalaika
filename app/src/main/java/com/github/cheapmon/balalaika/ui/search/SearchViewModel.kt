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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.toLiveData
import com.github.cheapmon.balalaika.data.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.data.entities.history.HistoryEntry
import com.github.cheapmon.balalaika.data.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.data.repositories.HistoryRepository
import com.github.cheapmon.balalaika.data.repositories.SearchRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** View model for [SearchFragment] */
class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    /** Lexemes matching the user's query */
    val entries = searchRepository.lexemes.asLiveData().switchMap {
        it.toLiveData(10)
    }

    /** Current query */
    val query = searchRepository.query.asLiveData()

    /** Current search restriction */
    val restriction = searchRepository.restriction.asLiveData()

    /** Current state of computation */
    val inProgress = searchRepository.inProgress.asLiveData()

    /** Set query */
    fun setQuery(query: String) {
        searchRepository.setQuery(query)
    }

    /** Set restriction */
    fun setRestriction(restriction: SearchRestriction) {
        searchRepository.setRestriction(restriction)
    }

    /** Get all dictionary entries associated with a lexeme */
    suspend fun getDictionaryEntriesFor(lexemeId: Long): List<DictionaryEntry> {
        return searchRepository.getDictionaryEntriesFor(lexemeId)
    }

    /** Remove search restriction */
    fun clearRestriction() {
        searchRepository.clearRestriction()
    }

    /** Add search to history */
    fun addToHistory() {
        viewModelScope.launch {
            val query = searchRepository.query.first()
            if (query.isBlank()) return@launch
            val entry = when (val r = searchRepository.restriction.first()) {
                is SearchRestriction.None ->
                    HistoryEntry(query = query)
                is SearchRestriction.Some ->
                    HistoryEntry(
                        query = query,
                        categoryId = r.category.categoryId,
                        restriction = r.restriction
                    )
            }
            historyRepository.removeSimilarEntries(entry)
            historyRepository.addEntry(entry)
        }
    }

}