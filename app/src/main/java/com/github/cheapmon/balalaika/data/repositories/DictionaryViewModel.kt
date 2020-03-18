package com.github.cheapmon.balalaika.data.repositories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@FlowPreview
class DictionaryViewModel(
    private val repository: DictionaryRepository
) : ViewModel() {

    val lexemes = repository.lexemes.asLiveData()
    val comparators = repository.comparators.map { it.keys.sorted() }.asLiveData()
    val dictionaryViews = repository.dictionaryViews.asLiveData()

    init {
        viewModelScope.launch { repository.addComparators() }
    }

    fun setOrdering(comparatorName: String) {
        repository.setOrdering(comparatorName)
    }

    fun setDictionaryView(dictionaryViewId: Long) {
        viewModelScope.launch { repository.setDictionaryView(dictionaryViewId) }
    }

}