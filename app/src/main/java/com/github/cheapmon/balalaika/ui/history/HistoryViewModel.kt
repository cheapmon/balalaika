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
package com.github.cheapmon.balalaika.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.cheapmon.balalaika.data.entities.history.HistoryEntry
import com.github.cheapmon.balalaika.data.entities.history.HistoryEntryWithRestriction
import com.github.cheapmon.balalaika.data.repositories.HistoryRepository
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: HistoryRepository
) : ViewModel() {
    val historyEntries: LiveData<List<HistoryEntryWithRestriction>> =
        repository.historyEntries.asLiveData()

    fun removeEntry(historyEntry: HistoryEntry) {
        viewModelScope.launch { repository.removeEntry(historyEntry) }
    }

    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }
}