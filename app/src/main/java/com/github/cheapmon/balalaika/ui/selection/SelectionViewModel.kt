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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

/** View model for [SelectionFragment] */
class SelectionViewModel @ViewModelInject constructor(
    private val dictionaries: DictionaryRepository
) : ViewModel() {
    private val refresh = MutableStateFlow(false)

    /** All installed dictionaries */
    val installedDictionaries: LiveData<List<SimpleDictionary>> =
        combine(refresh, dictionaries.getOpenDictionary()) { _, d -> d }
            .flatMapLatest { dictionaries.getInstalledDictionaries(it) }
            .map { list -> list.sortedBy { !it.isOpened } }
            .asLiveData()

    /** All downloadable dictionaries */
    val downloadableDictionaries: LiveData<List<SimpleDictionary>> =
        refresh.flatMapLatest { dictionaries.getDownloadableDictionaries() }
            .asLiveData()

    fun refresh() {
        refresh.value = !refresh.value
    }
}
