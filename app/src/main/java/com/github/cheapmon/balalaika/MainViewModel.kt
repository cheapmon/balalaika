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
import com.github.cheapmon.balalaika.data.repositories.dictionary.install.InstallationMessage
import com.github.cheapmon.balalaika.data.result.ProgressState
import com.github.cheapmon.balalaika.model.DownloadableDictionary
import com.github.cheapmon.balalaika.model.HistoryItem
import com.github.cheapmon.balalaika.model.InstalledDictionary
import com.github.cheapmon.balalaika.model.SearchRestriction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    /** Activate a dictionary */
    fun openDictionary(dictionary: InstalledDictionary) {
        viewModelScope.launch {
            dictionaryRepository.openDictionary(dictionary)
        }
    }

    /** Deactivate a dictionary */
    fun closeDictionary() {
        viewModelScope.launch {
            dictionaryRepository.closeDictionary()
        }
    }

    /** Install a dictionary into the library */
    fun installDictionary(dictionary: DownloadableDictionary): Flow<ProgressState<Unit, InstallationMessage, Throwable>> {
        return dictionaryRepository.addDictionaryToLibrary(dictionary)
    }

    /** Remove a dictionary from the library */
    fun removeDictionary(dictionary: InstalledDictionary): Flow<ProgressState<Unit, InstallationMessage, Throwable>> {
        return dictionaryRepository.removeDictionaryFromLibrary(dictionary)
    }

    /** Update a dictionary in the library */
    fun updateDictionary(dictionary: InstalledDictionary): Flow<ProgressState<Unit, InstallationMessage, Throwable>> {
        return dictionaryRepository.updateDictionary(dictionary)
    }

    /** Add search to history */
    fun addToHistory(query: String, restriction: SearchRestriction?) {
        viewModelScope.launch {
            val dictionary = dictionaryRepository.getOpenDictionary().first() ?: return@launch
            if (query.isBlank()) return@launch
            val item = HistoryItem(query = query, restriction = restriction)
            historyRepository.addToHistory(dictionary, item)
        }
    }
}
