package com.github.cheapmon.balalaika.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.cheapmon.balalaika.data.entities.history.HistoryEntry
import com.github.cheapmon.balalaika.data.entities.history.HistoryEntryWithRestriction
import com.github.cheapmon.balalaika.data.repositories.HistoryRepository
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: HistoryRepository
) : ViewModel() {
    val historyEntries: LiveData<List<HistoryEntryWithRestriction>> =
        repository.historyEntries.asLiveData()

    fun removeEntry(historyEntry: HistoryEntry) {
        viewModelScope.launch { repository.removeEntry(historyEntry) }
    }

    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }
}