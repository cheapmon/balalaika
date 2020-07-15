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
package com.github.cheapmon.balalaika.ui.dictionary

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.github.cheapmon.balalaika.data.dictionary.DictionaryEntryRepository
import com.github.cheapmon.balalaika.db.entities.category.Category
import com.github.cheapmon.balalaika.db.entities.view.DictionaryViewWithCategories
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** View model for [DictionaryFragment] */
class DictionaryViewModel @ViewModelInject constructor(
    private val repository: DictionaryEntryRepository
) : ViewModel() {
    /**
     * Adapt dictionary presentation to user configuration
     *
     * The view is only updated if one of these settings changed:
     * - Dictionary view
     * - Category to order by
     * - Initial entry
     */
    val dictionary = repository.dictionary.cachedIn(viewModelScope)
    val currentDictionary = repository.currentDictionary

    val dictionaryView = repository.dictionaryView
    val category = repository.category

    /** Set the dictionary view */
    fun setDictionaryView(id: String) = repository.setDictionaryView(id)

    /** Set the dictionary ordering */
    fun setCategory(id: String) = repository.setCategory(id)

    /** Set the first entry to display */
    fun setInitialKey(id: Long?) = repository.setInitialKey(id)

    /** Get position of lexeme in the current dictionary setup */
    suspend fun getIdOf(id: String?): Long? = repository.getIdOf(id)

    /** Toggle bookmark state for a lexeme */
    fun toggleBookmark(lexemeId: String) {
        viewModelScope.launch { repository.toggleBookmark(lexemeId) }
    }

    /** All available sortable categories */
    suspend fun getCategories(): List<Category> {
        return repository.categories.first()
    }

    /** All available dictionary views */
    suspend fun getDictionaryViews(): List<DictionaryViewWithCategories> {
        return repository.dictionaryViews.first()
    }
}
