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
import com.github.cheapmon.balalaika.data.repositories.dictionary.install.DictionaryInstaller
import com.github.cheapmon.balalaika.data.result.Result
import com.github.cheapmon.balalaika.data.result.tryRun
import com.github.cheapmon.balalaika.model.Dictionary
import com.github.cheapmon.balalaika.model.DownloadableDictionary
import com.github.cheapmon.balalaika.model.InstalledDictionary
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Suppress("EXPERIMENTAL_API_USAGE")
@Singleton
public class DictionaryRepository @Inject internal constructor(
    private val storage: PreferenceStorage,
    private val dictionaryDao: DictionaryDao,
    private val dataSources: Map<String, @JvmSuppressWildcards DictionaryDataSource>,
    private val installer: DictionaryInstaller,
    private val toDictionary: DictionaryEntityToDictionary
) : DictionaryInstaller by installer {
    public fun getOpenDictionary(): Flow<Dictionary?> =
        storage.openDictionary
            .flatMapLatest { it?.let { dictionaryDao.findById(it) } ?: flowOf(null) }
            .map { it?.let { toDictionary(it) } }

    public fun getInstalledDictionaries(opened: Dictionary?): Flow<List<InstalledDictionary>> =
        dictionaryDao.getAll().map { list ->
            list.map { InstalledDictionary(toDictionary(it), it.id == opened?.id) }
        }

    public fun getDownloadableDictionaries(): Flow<List<DownloadableDictionary>> =
        dictionaryDao.getAll().map { list ->
            val currentList = list.map { toDictionary(it) }
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
        newList: List<Dictionary>
    ): List<DownloadableDictionary> = newList.map { dictionary ->
        val current = currentList.find { it.id == dictionary.id }
        if (current == null) {
            DownloadableDictionary(dictionary)
        } else {
            val isInLibrary = dictionary.version == current.version
            DownloadableDictionary(dictionary, isInLibrary)
        }
    }

    public suspend fun openDictionary(dictionary: InstalledDictionary) {
        storage.setOpenDictionary(dictionary.id)
    }

    public suspend fun closeDictionary() {
        storage.setOpenDictionary(null)
    }
}
