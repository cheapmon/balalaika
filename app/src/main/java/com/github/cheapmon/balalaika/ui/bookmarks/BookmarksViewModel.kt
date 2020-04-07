package com.github.cheapmon.balalaika.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import kotlinx.coroutines.launch

class BookmarksViewModel(
    private val repository: DictionaryRepository
) : ViewModel() {
    val lexemes = repository.bookmarks.asLiveData()

    fun removeBookmark(lexemeId: Long) {
        viewModelScope.launch { repository.toggleBookmark(lexemeId) }
    }

    fun clearBookmarks() {
        viewModelScope.launch { repository.clearBookmarks() }
    }
}