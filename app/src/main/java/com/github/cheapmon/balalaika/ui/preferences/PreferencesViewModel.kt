package com.github.cheapmon.balalaika.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.cheapmon.balalaika.data.entities.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PreferencesViewModel(
    private val repository: DictionaryRepository
) : ViewModel() {
    fun setDictionaryView(dictionaryViewId: Long) {
        viewModelScope.launch { repository.setDictionaryView(dictionaryViewId) }
    }

    fun setOrdering(comparatorName: String) {
        viewModelScope.launch { repository.setOrdering(comparatorName) }
    }

    suspend fun getDictionaryViews(): List<DictionaryViewWithCategories> {
        return repository.dictionaryViews.first()
    }

    suspend fun getComparators(): Array<String> {
        return repository.comparators.first().keys.toTypedArray()
    }
}