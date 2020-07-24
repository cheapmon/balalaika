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

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.cheapmon.balalaika.data.selection.DictionaryRepository
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.util.navArgs
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/** View model for [SelectionDetailFragment] */
class SelectionDetailViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val repository: DictionaryRepository
) : ViewModel() {
    private val navArgs: SelectionDetailFragmentArgs by navArgs(savedStateHandle)

    private var _dictionary: Dictionary? = null

    /** Single dictionary for detailed view */
    val dictionary = repository.getDictionary(navArgs.id).onEach { _dictionary = it }

    /** (De)activate a dictionary */
    fun toggleActive() {
        viewModelScope.launch { _dictionary?.let { repository.toggleActive(it) } }
    }

    /** Install a dictionary into the library */
    fun install() {
        viewModelScope.launch { _dictionary?.let { repository.addDictionary(it) } }
    }

    /** Remove a dictionary from the library */
    fun remove() {
        viewModelScope.launch { _dictionary?.let { repository.removeDictionary(it.id) } }
    }

    /** Update a dictionary in the library */
    fun update() {
        viewModelScope.launch { _dictionary?.let { repository.updateDictionary(it) } }
    }
}
