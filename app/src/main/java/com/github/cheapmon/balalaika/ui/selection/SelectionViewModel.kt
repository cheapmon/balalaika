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
import androidx.lifecycle.viewModelScope
import com.github.cheapmon.balalaika.data.selection.DictionaryRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map

/** View model for [SelectionFragment] */
class SelectionViewModel @ViewModelInject constructor(
    private val repository: DictionaryRepository
) : ViewModel() {
    init {
        refresh()
    }

    /** All available dictionaries */
    val dictionaries = repository.dictionaries.map { list -> list.sortedBy { !it.isActive } }

    /** Refresh dictionary list from sources */
    fun refresh() {
        repository.refresh().launchIn(viewModelScope)
    }
}
