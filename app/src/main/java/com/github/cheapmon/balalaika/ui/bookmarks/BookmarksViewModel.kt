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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.cheapmon.balalaika.data.repositories.BookmarkRepository
import com.github.cheapmon.balalaika.data.repositories.DictionaryEntryRepository
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import com.github.cheapmon.balalaika.data.repositories.WordnetRepository
import com.github.cheapmon.balalaika.model.DictionaryEntry
import com.github.cheapmon.balalaika.ui.DefaultWordnetViewModel
import com.github.cheapmon.balalaika.ui.WordnetViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

interface BookmarksViewModel {
    val bookmarkedEntries: Flow<List<DictionaryEntry>>

    fun toggleBookmark(dictionaryEntry: DictionaryEntry)
    fun clearBookmarks()
}

/**
 * View model for [BookmarksFragment]
 *
 * @see DictionaryEntryRepository
 */
class DefaultBookmarksViewModel @ViewModelInject constructor(
    private val dictionaries: DictionaryRepository,
    private val bookmarks: BookmarkRepository,
    private val wordnet: WordnetRepository
) : ViewModel(), WordnetViewModel by DefaultWordnetViewModel(wordnet), BookmarksViewModel {
    /** All lexemes that are currently bookmarked */
    override val bookmarkedEntries: Flow<List<DictionaryEntry>> = dictionaries.getOpenDictionary()
        .flatMapLatest { dictionary ->
            if (dictionary != null) {
                bookmarks.getBookmarkedEntries(dictionary)
            } else {
                flowOf(emptyList())
            }
        }

    override fun toggleBookmark(dictionaryEntry: DictionaryEntry) {
        viewModelScope.launch {
            val dictionary = dictionaries.getOpenDictionary().first() ?: return@launch
            if (dictionaryEntry.bookmark == null) {
                bookmarks.addBookmark(dictionary, dictionaryEntry)
            } else {
                bookmarks.removeBookmark(dictionary, dictionaryEntry)
            }
        }
    }

    /** Remove all bookmarks */
    override fun clearBookmarks() {
        viewModelScope.launch {
            val openedDictionary = dictionaries.getOpenDictionary().first()
            if (openedDictionary != null) bookmarks.clearBookmarks(openedDictionary)
        }
    }
}
