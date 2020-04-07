package com.github.cheapmon.balalaika.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.cheapmon.balalaika.data.repositories.HistoryRepository

class HistoryViewModelFactory(
    private val repository: HistoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HistoryViewModel(
            repository
        ) as T
    }
}