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
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import com.github.cheapmon.balalaika.model.SimpleDictionary
import kotlinx.coroutines.flow.*

/** View model for [SelectionFragment] */
class SelectionViewModel @ViewModelInject constructor(
    private val dictionaries: DictionaryRepository
) : ViewModel() {
    private val refresh = MutableStateFlow(false)

    /** All installed dictionaries */
    val installedDictionaries: Flow<List<SimpleDictionary>> =
        combine(refresh, dictionaries.getOpenDictionary()) { _, d -> d }
            .flatMapLatest { dictionaries.getInstalledDictionaries(it) }
            .map { list -> list.sortedBy { !it.isOpened } }

    /** All downloadable dictionaries */
    val downloadableDictionaries: Flow<List<SimpleDictionary>> =
        refresh.flatMapLatest { dictionaries.getDownloadableDictionaries() }

    fun refresh() {
        refresh.value = !refresh.value
    }
}
