package com.github.cheapmon.balalaika.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.view.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PreferencesViewModel(
    private val repository: DictionaryRepository
) : ViewModel() {
    fun setDictionaryView(dictionaryViewId: Long) {
        viewModelScope.launch { repository.setDictionaryViewId(dictionaryViewId) }
    }

    fun setCategory(categoryId: Long) {
        viewModelScope.launch { repository.setCategoryId(categoryId) }
    }

    suspend fun getDictionaryViews(): List<DictionaryViewWithCategories> {
        return repository.dictionaryViews.first()
    }

    suspend fun getCategories(): List<Category> {
        return repository.categories.first()
    }
}