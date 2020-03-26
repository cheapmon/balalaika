package com.github.cheapmon.balalaika.ui.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.cheapmon.balalaika.data.entities.DictionaryView
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@FlowPreview
class DictionaryViewModel(
    private val repository: DictionaryRepository
) : ViewModel() {
    val lexemes = repository.lexemes.asLiveData()

    init {
        viewModelScope.launch { repository.addComparators() }
    }

    fun setOrdering(comparatorName: String) {
        repository.setOrdering(comparatorName)
    }

    fun setDictionaryView(dictionaryViewId: Long) {
        viewModelScope.launch { repository.setDictionaryView(dictionaryViewId) }
    }

    suspend fun getComparators(): List<String> {
        return repository.comparators.first().keys.toList()
    }

    suspend fun getDictionaryViews(): List<DictionaryView> {
        return repository.dictionaryViews.first()
    }
}