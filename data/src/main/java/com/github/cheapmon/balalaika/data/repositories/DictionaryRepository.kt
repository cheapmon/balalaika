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

import com.github.cheapmon.balalaika.data.db.category.CategoryDao
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfigDao
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfigWithRelations
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewDao
import com.github.cheapmon.balalaika.data.mappers.CategoryEntityToDataCategory
import com.github.cheapmon.balalaika.data.mappers.DictionaryEntityToDictionary
import com.github.cheapmon.balalaika.data.mappers.DictionaryViewWithCategoriesToDictionaryView
import com.github.cheapmon.balalaika.data.prefs.PreferenceStorage
import com.github.cheapmon.balalaika.data.repositories.dictionary.DictionaryDataSource
import com.github.cheapmon.balalaika.data.repositories.dictionary.install.DictionaryInstaller
import com.github.cheapmon.balalaika.data.result.Result
import com.github.cheapmon.balalaika.data.result.tryRun
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.Dictionary
import com.github.cheapmon.balalaika.model.DictionaryView
import com.github.cheapmon.balalaika.model.DownloadableDictionary
import com.github.cheapmon.balalaika.model.InstalledDictionary
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Suppress("EXPERIMENTAL_API_USAGE")
@Singleton
class DictionaryRepository @Inject internal constructor(
    private val storage: PreferenceStorage,
    private val dictionaryDao: DictionaryDao,
    private val configDao: DictionaryConfigDao,
    private val categoryDao: CategoryDao,
    private val dictionaryViewDao: DictionaryViewDao,
    private val dataSources: Map<String, @JvmSuppressWildcards DictionaryDataSource>,
    private val installer: DictionaryInstaller,
    private val toDictionary: DictionaryEntityToDictionary,
    private val toDataCategory: CategoryEntityToDataCategory,
    private val toDictionaryView: DictionaryViewWithCategoriesToDictionaryView
) : DictionaryInstaller by installer {
    fun getOpenDictionary(): Flow<Dictionary?> =
        storage.openDictionary
            .flatMapLatest { it?.let { dictionaryDao.findById(it) } ?: flowOf(null) }
            .map { it?.let { toDictionary(it) } }

    private fun getConfig(): Flow<DictionaryConfigWithRelations?> =
        storage.openDictionary
            .flatMapLatest { it?.let { configDao.getConfigFor(it) } ?: flowOf(null) }

    fun getSortCategory(): Flow<DataCategory?> =
        getConfig().map { config -> config?.category?.let { toDataCategory(it) } }

    fun getSortCategories(): Flow<List<DataCategory>?> =
        storage.openDictionary
            .flatMapLatest { id -> id?.let { categoryDao.getSortable(it) } ?: flowOf(null) }
            .map { list -> list?.map { toDataCategory(it) } }

    fun getDictionaryView(): Flow<DictionaryView?> =
        getConfig().map { config -> config?.view?.let { toDictionaryView(it) } }

    fun getDictionaryViews(): Flow<List<DictionaryView>?> =
        storage.openDictionary
            .flatMapLatest { id -> id?.let { dictionaryViewDao.getAll(it) } ?: flowOf(null) }
            .map { list -> list?.map { toDictionaryView(it) } }

    fun getInstalledDictionaries(): Flow<List<InstalledDictionary>> =
        combine(getOpenDictionary(), dictionaryDao.getAll()) { opened, list ->
            list.map { InstalledDictionary(toDictionary(it), it.id == opened?.id) }
        }

    fun getDownloadableDictionaries(): Flow<List<DownloadableDictionary>> =
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

    suspend fun openDictionary(dictionary: InstalledDictionary) {
        storage.setOpenDictionary(dictionary.id)
    }

    suspend fun closeDictionary() {
        storage.setOpenDictionary(null)
    }
}
