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
package com.github.cheapmon.balalaika.data.repositories

import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.data.mappers.DictionaryEntityToDictionary
import com.github.cheapmon.balalaika.data.prefs.PreferenceStorage
import com.github.cheapmon.balalaika.data.repositories.dictionary.DictionaryDataSource
import com.github.cheapmon.balalaika.data.result.Result
import com.github.cheapmon.balalaika.data.result.tryRun
import com.github.cheapmon.balalaika.model.Dictionary
import com.github.cheapmon.balalaika.model.DownloadableDictionary
import com.github.cheapmon.balalaika.model.InstalledDictionary
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("EXPERIMENTAL_API_USAGE")
@Singleton
class DictionaryRepository @Inject internal constructor(
    private val storage: PreferenceStorage,
    private val dao: DictionaryDao,
    private val dataSources: Map<String, @JvmSuppressWildcards DictionaryDataSource>,
    private val mapper: DictionaryEntityToDictionary
) {
    fun getOpenDictionary(): Flow<Dictionary?> =
        storage.openDictionary
            .flatMapLatest { it?.let { dao.findById(it) } ?: flowOf(null) }
            .map { it?.let { mapper(it) } }

    fun getInstalledDictionaries(): Flow<List<InstalledDictionary>> =
        combine(getOpenDictionary(), dao.getAll()) { opened, list ->
            list.map { InstalledDictionary(mapper(it), it.id == opened?.id) }
        }

    fun getDownloadableDictionaries(): Flow<List<DownloadableDictionary>> =
        dao.getAll().map { list ->
            val currentList = list.map { mapper(it) }
            val newList = fetchDictionariesFromDataSources()
            compareDictionaryLists(currentList, newList)
        }

    private suspend fun fetchDictionariesFromDataSources(): List<Dictionary> {
        return dataSources.flatMap { (_, v) ->
            when (val result = tryRun { v.getDictionaryList() }) {
                is Result.Success -> result.data
                is Result.Error -> emptyList()
            }
        }
    }

    private fun compareDictionaryLists(
        currentList: List<Dictionary>,
        newList: List<Dictionary>,
    ): List<DownloadableDictionary> = newList.map { dictionary ->
        val current = currentList.find { it.id == dictionary.id }
        if (current == null) {
            DownloadableDictionary(dictionary)
        } else {
            val isInLibrary = dictionary.version == current.version
            DownloadableDictionary(dictionary, isInLibrary)
        }
    }
}
