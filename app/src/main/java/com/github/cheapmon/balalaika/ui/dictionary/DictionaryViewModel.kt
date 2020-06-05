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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.toLiveData
import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.data.entities.view.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DictionaryViewModel(
    private val repository: DictionaryRepository,
    categoryId: Long?,
    dictionaryViewId: Long?
) : ViewModel() {
    val lexemes = repository.lexemes.asLiveData().switchMap {
        it.toLiveData(10)
    }
    val inProgress = repository.inProgress.asLiveData()

    init {
        if (categoryId != null) repository.setCategoryId(categoryId)
        if (dictionaryViewId != null) repository.setDictionaryViewId(dictionaryViewId)
    }

    fun setCategory(categoryId: Long) {
        repository.setCategoryId(categoryId)
    }

    fun setDictionaryView(dictionaryViewId: Long) {
        repository.setDictionaryViewId(dictionaryViewId)
    }

    fun toggleBookmark(lexemeId: Long) {
        viewModelScope.launch { repository.toggleBookmark(lexemeId) }
    }

    suspend fun getPositionOf(externalId: String): Int {
        return repository.positions.first().indexOfFirst { it == externalId }
    }

    suspend fun getCategories(): List<Category> {
        return repository.categories.first()
    }

    suspend fun getDictionaryViews(): List<DictionaryViewWithCategories> {
        return repository.dictionaryViews.first()
    }

    suspend fun getDictionaryEntriesFor(lexemeId: Long): List<DictionaryEntry> {
        return repository.getDictionaryEntriesFor(lexemeId)
    }
}