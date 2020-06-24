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
package com.github.cheapmon.balalaika.ui.preferences

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.github.cheapmon.balalaika.db.entities.category.Category
import com.github.cheapmon.balalaika.db.entities.view.DictionaryViewWithCategories
import com.github.cheapmon.balalaika.domain.repositories.DictionaryEntryRepository
import com.github.cheapmon.balalaika.domain.storage.Storage
import com.github.cheapmon.balalaika.util.Constants
import kotlinx.coroutines.flow.first

/** View model for [PreferencesFragment] */
class PreferencesViewModel @ViewModelInject constructor(
    private val repository: DictionaryEntryRepository,
    private val storage: Storage,
    private val constants: Constants
) : ViewModel() {
    /** Select a view for the current dictionary */
    fun setDictionaryView(dictionaryViewId: Long) {
        val key = constants.VIEW_KEY
        storage.putString(key, dictionaryViewId.toString())
    }

    /** Select category to order lexemes by */
    fun setCategory(categoryId: Long) {
        val key = constants.ORDER_KEY
        storage.putString(key, categoryId.toString())
    }

    /** All available dictionary views */
    suspend fun getDictionaryViews(): List<DictionaryViewWithCategories> {
        return repository.dictionaryViews.first()
    }

    /** All available sortable categories */
    suspend fun getCategories(): List<Category> {
        return repository.categories.first()
    }
}
