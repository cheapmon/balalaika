/*
 * Copyright 2020 Simon Kaleschke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.cheapmon.balalaika.ui.bookmarks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.cheapmon.balalaika.data.repositories.BookmarkRepository
import com.github.cheapmon.balalaika.data.repositories.DictionaryEntryRepository
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import com.github.cheapmon.balalaika.model.DictionaryEntry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/**
 * View model for [BookmarksFragment]
 *
 * @see DictionaryEntryRepository
 */
class BookmarksViewModel @ViewModelInject constructor(
    private val dictionaries: DictionaryRepository,
    private val bookmarks: BookmarkRepository
) : ViewModel() {
    /** All lexemes that are currently bookmarked */
    val bookmarkedEntries: LiveData<List<DictionaryEntry>> = dictionaries.getOpenDictionary()
        .flatMapLatest { dictionary ->
            if (dictionary != null) {
                bookmarks.getBookmarkedEntries(dictionary)
            } else {
                flowOf(emptyList())
            }
        }.asLiveData()

    /** Remove single bookmark */
    fun removeBookmark(dictionaryEntry: DictionaryEntry) = viewModelScope.launch {
        val openedDictionary = dictionaries.getOpenDictionary().first()
        if (openedDictionary != null) bookmarks.removeBookmark(openedDictionary, dictionaryEntry)
    }

    /** Remove all bookmarks */
    fun clearBookmarks() = viewModelScope.launch {
        val openedDictionary = dictionaries.getOpenDictionary().first()
        if (openedDictionary != null) bookmarks.clearBookmarks(openedDictionary)
    }
}
