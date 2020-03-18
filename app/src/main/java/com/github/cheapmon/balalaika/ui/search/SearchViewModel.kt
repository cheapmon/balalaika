package com.github.cheapmon.balalaika.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.cheapmon.balalaika.data.repositories.SearchRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class SearchViewModel(
    private val repository: SearchRepository
) : ViewModel() {

    val lexemes = repository.lexemes.asLiveData()

    fun setQuery(query: String) {
        repository.setQuery(query)
    }

    fun setRestriction(categoryId: Long, restriction: String) {
        repository.setRestriction(categoryId, restriction)
    }

}