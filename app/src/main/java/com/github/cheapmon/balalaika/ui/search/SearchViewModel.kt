package com.github.cheapmon.balalaika.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.cheapmon.balalaika.data.entities.HistoryEntry
import com.github.cheapmon.balalaika.data.entities.SearchRestriction
import com.github.cheapmon.balalaika.data.repositories.HistoryRepository
import com.github.cheapmon.balalaika.data.repositories.SearchRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val lexemes = searchRepository.lexemes.asLiveData()
    val query = searchRepository.query.asLiveData()
    val restriction = searchRepository.restriction.asLiveData()
    val inProgress = searchRepository.inProgress.asLiveData()

    fun setQuery(query: String) {
        searchRepository.setQuery(query)
    }

    fun setRestriction(restriction: SearchRestriction) {
        searchRepository.setRestriction(restriction)
    }

    fun clearRestriction() {
        searchRepository.clearRestriction()
    }

    fun addToHistory() {
        viewModelScope.launch {
            val query = searchRepository.query.first()
            if (query.isBlank()) return@launch
            val entry = when (val restriction = searchRepository.restriction.first()) {
                is SearchRestriction.None -> HistoryEntry(query = query)
                is SearchRestriction.Some -> HistoryEntry(
                    categoryId = restriction.category.categoryId,
                    restriction = restriction.restriction,
                    query = query
                )
            }
            historyRepository.removeSimilarEntries(entry)
            historyRepository.addEntry(entry)
        }
    }

}