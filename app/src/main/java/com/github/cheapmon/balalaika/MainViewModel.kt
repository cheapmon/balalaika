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
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import com.github.cheapmon.balalaika.data.repositories.HistoryRepository
import com.github.cheapmon.balalaika.model.DownloadableDictionary
import com.github.cheapmon.balalaika.model.HistoryItem
import com.github.cheapmon.balalaika.model.InstalledDictionary
import com.github.cheapmon.balalaika.model.SearchRestriction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * View model for the main activity and its fragments
 *
 * Used for operations that need to survive fragment transactions, including:
 * - Adding, removing, updating, activating and deactivating dictionaries
 * - Adding search queries to the history
 */
class MainViewModel @ViewModelInject constructor(
    private val dictionaryRepository: DictionaryRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    /** Currently active dictionary */
    val currentDictionary = dictionaryRepository.getOpenDictionary().asLiveData()

    private val _operationsInProgress = MutableStateFlow(0)

    /** Progress of any running operations */
    val progress = _operationsInProgress.map { it != 0 }.asLiveData()

    private val _messages = MutableStateFlow<String?>(null)

    /** Messages about operations to be displayed in the UI */
    val messages = _messages.filterNotNull().asLiveData()

    private val _showSearch = MutableStateFlow(false)

    /** Whether to show the search field in the app bar */
    val showSearch = _showSearch.asLiveData()

    /** Toggle the search field */
    fun toggleSearch() {
        _showSearch.value = !_showSearch.value
    }

    /** Activate a dictionary */
    fun openDictionary(dictionary: InstalledDictionary) = viewModelScope.launch {
        dictionaryRepository.openDictionary(dictionary)
    }

    /** Deactivate a dictionary */
    fun closeDictionary() = viewModelScope.launch {
        dictionaryRepository.closeDictionary()
    }

    /** Install a dictionary into the library */
    fun installDictionary(dictionary: DownloadableDictionary) = viewModelScope.launch {
        dictionaryRepository.addDictionaryToLibrary(dictionary).collect()
    }

    /** Remove a dictionary from the library */
    fun removeDictionary(dictionary: InstalledDictionary) = viewModelScope.launch {
        dictionaryRepository.removeDictionaryFromLibrary(dictionary)
    }

    /** Update a dictionary in the library */
    fun updateDictionary(dictionary: InstalledDictionary) {
        dictionaryRepository.updateDictionary(dictionary)
    }

    /** Add search to history */
    fun addToHistory(query: String, restriction: SearchRestriction?) {
        viewModelScope.launch {
            val dictionary = dictionaryRepository.getOpenDictionary().first() ?: return@launch
            if (query.isBlank()) return@launch
            val item = HistoryItem(query, restriction)
            historyRepository.addToHistory(dictionary, item)
        }
    }
}
