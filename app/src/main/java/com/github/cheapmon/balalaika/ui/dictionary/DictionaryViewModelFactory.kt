package com.github.cheapmon.balalaika.ui.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository

class DictionaryViewModelFactory(
    private val repository: DictionaryRepository,
    private val categoryId: Long?,
    private val dictionaryViewId: Long?
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DictionaryViewModel(
            repository,
            categoryId,
            dictionaryViewId
        ) as T
    }
}