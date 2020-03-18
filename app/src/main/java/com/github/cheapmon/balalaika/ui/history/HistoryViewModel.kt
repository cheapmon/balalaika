package com.github.cheapmon.balalaika.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.cheapmon.balalaika.data.repositories.HistoryRepository
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: HistoryRepository
) : ViewModel() {
    val historyEntries = repository.historyEntries.asLiveData()

    fun addEntry(query: String, categoryId: Long, restriction: String) {
        viewModelScope.launch { repository.addEntry(query, categoryId, restriction) }
    }

    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }
}