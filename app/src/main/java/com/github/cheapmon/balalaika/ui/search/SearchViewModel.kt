package com.github.cheapmon.balalaika.ui.search

import androidx.lifecycle.*
import androidx.paging.toLiveData
import com.github.cheapmon.balalaika.data.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.data.entities.history.HistoryEntry
import com.github.cheapmon.balalaika.data.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.data.repositories.HistoryRepository
import com.github.cheapmon.balalaika.data.repositories.SearchRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val entries = searchRepository.lexemes.asLiveData().switchMap {
        it.toLiveData(10)
    }
    val query = searchRepository.query.asLiveData()
    val restriction = searchRepository.restriction.asLiveData()
    val inProgress = searchRepository.inProgress.asLiveData()

    fun setQuery(query: String) {
        searchRepository.setQuery(query)
    }

    fun setRestriction(restriction: SearchRestriction) {
        searchRepository.setRestriction(restriction)
    }

    suspend fun getDictionaryEntriesFor(lexemeId: Long): List<DictionaryEntry> {
        return searchRepository.getDictionaryEntriesFor(lexemeId)
    }

    fun clearRestriction() {
        searchRepository.clearRestriction()
    }

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