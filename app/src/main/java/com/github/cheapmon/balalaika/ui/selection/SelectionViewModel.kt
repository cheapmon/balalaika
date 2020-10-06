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
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import com.github.cheapmon.balalaika.model.SimpleDictionary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

/** View model for [SelectionFragment] */
class SelectionViewModel @ViewModelInject constructor(
    private val dictionaries: DictionaryRepository
) : ViewModel() {
    private val _refreshing = MutableStateFlow(false)

    /** List of downloadable dictionaries is refreshing */
    val refreshing: LiveData<Boolean> = _refreshing.asLiveData()

    /** All installed dictionaries */
    val installedDictionaries: LiveData<List<SimpleDictionary>> =
        dictionaries.getOpenDictionary()
            .flatMapLatest { dictionaries.getInstalledDictionaries(it) }
            .map { list -> list.sortedBy { !it.isOpened } }
            .asLiveData()

    /** All downloadable dictionaries */
    val downloadableDictionaries: LiveData<List<SimpleDictionary>> =
        _refreshing.filter { it }
            .flatMapLatest {
                val dictionaries = dictionaries.getDownloadableDictionaries()
                _refreshing.value = false
                dictionaries
            }.asLiveData()

    /** Refresh dictionary list from sources */
    fun refresh() {
        _refreshing.value = true
    }
}
