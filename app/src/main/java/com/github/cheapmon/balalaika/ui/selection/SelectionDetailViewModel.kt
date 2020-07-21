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
package com.github.cheapmon.balalaika.ui.selection

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.github.cheapmon.balalaika.data.selection.DictionaryRepository
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary

/** View model for [SelectionDetailFragment] */
class SelectionDetailViewModel @ViewModelInject constructor(
    private val repository: DictionaryRepository
) : ViewModel() {
    /** Get a single dictionary */
    fun getDictionary(id: String) = repository.getDictionary(id)

    /** (De)activate a dictionary */
    fun toggleActive(dictionary: Dictionary) = repository.toggleActive(dictionary)

    /** Install a dictionary into the library */
    fun addDictionary(dictionary: Dictionary) = repository.addDictionary(dictionary)

    /** Remove a dictionary from the library */
    fun removeDictionary(id: String) = repository.removeDictionary(id)

    /** Update a dictionary in the library */
    fun updateDictionary(dictionary: Dictionary) = repository.updateDictionary(dictionary)
}
