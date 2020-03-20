package com.github.cheapmon.balalaika.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.cheapmon.balalaika.data.entities.HistoryEntry
import com.github.cheapmon.balalaika.data.repositories.HistoryRepository
import com.github.cheapmon.balalaika.data.repositories.SearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@FlowPreview
class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val lexemes = searchRepository.lexemes.asLiveData()

    fun getQuery(): String? {
        return searchRepository.getQuery()
    }

    fun setQuery(query: String) {
        searchRepository.setQuery(query)
    }

    fun getRestriction(): Pair<Long?, String?>? {
        return searchRepository.getRestriction()
    }

    fun setRestriction(categoryId: Long, restriction: String) {
        searchRepository.setRestriction(categoryId, restriction)
    }

    fun clearRestriction() {
        searchRepository.clearRestriction()
    }

    fun addToHistory(query: String) {
        viewModelScope.launch {
            historyRepository.addEntry(HistoryEntry(0, null, "", query))
        }
    }

    fun addToHistory(query: String, categoryId: Long?, restriction: String) {
        viewModelScope.launch {
            historyRepository.addEntry(HistoryEntry(0, categoryId, restriction, query))
        }
    }

}