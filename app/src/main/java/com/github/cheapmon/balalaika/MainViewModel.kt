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

import android.content.Context
import androidx.annotation.StringRes
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.cheapmon.balalaika.data.LoadState
import com.github.cheapmon.balalaika.data.Result
import com.github.cheapmon.balalaika.data.selection.DictionaryError
import com.github.cheapmon.balalaika.data.selection.DictionaryRepository
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val repository: DictionaryRepository
) : ViewModel() {
    val currentDictionary = repository.currentDictionary.asLiveData()

    private val _operationsInProgress = MutableStateFlow(0)
    val progress = _operationsInProgress.map { it != 0 }.asLiveData()

    private val _messages = MutableStateFlow<String?>(null)
    val messages = _messages.filterNotNull().asLiveData()

    /** Activate a dictionary */
    fun activate(dictionary: Dictionary) {
        load(repository.toggleActive(dictionary), R.string.selection_success_activate)
    }

    fun deactivate(dictionary: Dictionary) {
        load(repository.toggleActive(dictionary), R.string.selection_success_deactivate)
    }

    /** Install a dictionary into the library */
    fun install(dictionary: Dictionary) {
        load(repository.addDictionary(dictionary), R.string.selection_success_install)
    }

    /** Remove a dictionary from the library */
    fun remove(dictionary: Dictionary) {
        load(repository.removeDictionary(dictionary.id), R.string.selection_success_remove)
    }

    /** Update a dictionary in the library */
    fun update(dictionary: Dictionary) {
        load(repository.updateDictionary(dictionary), R.string.selection_success_update)
    }

    private fun <T> load(
        flow: Flow<LoadState<T, DictionaryError>>,
        @StringRes msgId: Int
    ) {
        viewModelScope.launch {
            flow.collect { loadState ->
                when (loadState) {
                    is LoadState.Init ->
                        _operationsInProgress.value = _operationsInProgress.value + 1
                    is LoadState.Finished -> {
                        _operationsInProgress.value = _operationsInProgress.value - 1
                        val message = when (loadState.data) {
                            is Result.Success -> context.getString(msgId)
                            is Result.Error -> context.getString(loadState.data.cause.msgId)
                        }
                        // StateFlow ignores equal values
                        _messages.value = null
                        _messages.value = message
                    }
                }
            }
        }
    }
}
