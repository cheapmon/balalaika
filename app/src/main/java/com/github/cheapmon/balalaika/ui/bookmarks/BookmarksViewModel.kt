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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import kotlinx.coroutines.launch

/**
 * View model for [BookmarksFragment]
 *
 * @see DictionaryRepository
 */
class BookmarksViewModel(
    private val repository: DictionaryRepository
) : ViewModel() {
    /** All lexemes that are currently bookmarked */
    val lexemes = repository.bookmarks.asLiveData()

    /** Remove single bookmark */
    fun removeBookmark(lexemeId: Long) {
        viewModelScope.launch { repository.toggleBookmark(lexemeId) }
    }

    /** Remove all bookmarks */
    fun clearBookmarks() {
        viewModelScope.launch { repository.clearBookmarks() }
    }
}