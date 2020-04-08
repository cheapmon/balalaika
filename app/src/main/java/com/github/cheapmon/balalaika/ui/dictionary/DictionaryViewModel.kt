package com.github.cheapmon.balalaika.ui.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.cheapmon.balalaika.data.entities.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import com.github.cheapmon.balalaika.util.ComparatorUtil
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DictionaryViewModel(
    private val repository: DictionaryRepository,
    comparatorName: String?,
    dictionaryViewId: Long?
) : ViewModel() {
    val lexemes = repository.lexemes.asLiveData()
    val inProgress = repository.inProgress.asLiveData()

    init {
        repository.setOrdering(comparatorName ?: ComparatorUtil.DEFAULT_KEY)
        if (dictionaryViewId != null) repository.setDictionaryView(dictionaryViewId)
        viewModelScope.launch { repository.addComparators() }
    }

    fun setOrdering(comparatorName: String) {
        repository.setOrdering(comparatorName)
    }

    fun setDictionaryView(dictionaryViewId: Long) {
        viewModelScope.launch { repository.setDictionaryView(dictionaryViewId) }
    }

    fun toggleBookmark(lexemeId: Long) {
        viewModelScope.launch { repository.toggleBookmark(lexemeId) }
    }

    suspend fun getPositionOf(externalId: String): Int {
        return repository.lexemes.map {
            it.indexOfFirst { entry -> entry.lexeme.externalId == externalId }
        }.first()
    }

    suspend fun getComparators(): List<String> {
        return repository.comparators.first().keys.toList()
    }

    suspend fun getDictionaryViews(): List<DictionaryViewWithCategories> {
        return repository.dictionaryViews.first()
    }
}