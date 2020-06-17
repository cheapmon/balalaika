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
import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.view.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.data.insert.ImportUtil
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/** View model for [DictionaryFragment] */
class DictionaryViewModel @ViewModelInject constructor(
    private val repository: DictionaryRepository,
    private val importUtil: ImportUtil
) : ViewModel() {
    /**
     * Current state of import operations
     *
     * Calls the [import utility][ImportUtil] and emits `true` when finished.
     */
    val importFlow: Flow<Boolean> = flow {
        emit(false)
        importUtil.import()
        emit(true)
    }

    /**
     * Adapt dictionary presentation to user configuration
     *
     * The view is only updated if one of these settings changed:
     * - Dictionary view
     * - Category to order by
     * - Initial entry
     */
    val dictionary = flow {
        val result = repository.dictionary.cachedIn(viewModelScope)
        importFlow.collect { done -> if (done) result.collect { value -> emit(value) } }
    }

    /** Set the dictionary view */
    fun setDictionaryView(id: Long) = repository.setDictionaryView(id)

    /** Set the dictionary ordering */
    fun setCategory(id: Long) = repository.setCategory(id)

    /** Set the first entry to display */
    fun setInitialKey(id: Long?) = repository.setInitialKey(id)

    /** Get position of lexeme in the current dictionary setup */
    suspend fun getIdOf(externalId: String?): Long? = repository.getIdOf(externalId)

    /** Toggle bookmark state for a lexeme */
    fun toggleBookmark(lexemeId: Long) {
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