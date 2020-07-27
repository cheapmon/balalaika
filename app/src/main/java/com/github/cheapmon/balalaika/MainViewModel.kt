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
package com.github.cheapmon.balalaika

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.cheapmon.balalaika.data.LoadState
import com.github.cheapmon.balalaika.data.selection.DictionaryRepository
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    private val repository: DictionaryRepository
) : ViewModel() {
    val currentDictionary = repository.currentDictionary.asLiveData()

    private val _messages = MutableStateFlow<String?>(null)
    val messages = _messages.filterNotNull().asLiveData()

    private val _progress = MutableStateFlow(false)
    val progress = _progress.asLiveData()

    /** (De)activate a dictionary */
    fun toggleActive(dictionary: Dictionary) {
        viewModelScope.launch { repository.toggleActive(dictionary) }
    }

    /** Install a dictionary into the library */
    fun install(dictionary: Dictionary) {
        viewModelScope.launch {
            repository.addDictionary(dictionary).collect { loadState ->
                _progress.value = when (loadState) {
                    is LoadState.Init -> true
                    is LoadState.Loading -> true
                    is LoadState.Finished -> false
                }
            }
        }
    }

    /** Remove a dictionary from the library */
    fun remove(dictionary: Dictionary) {
        viewModelScope.launch { repository.removeDictionary(dictionary.id) }
    }

    /** Update a dictionary in the library */
    fun update(dictionary: Dictionary) {
        repository.updateDictionary(dictionary).launchIn(viewModelScope)
    }
}
