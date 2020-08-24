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
import com.github.cheapmon.balalaika.data.di.Local
import com.github.cheapmon.balalaika.data.di.Remote
import com.github.cheapmon.balalaika.data.mappers.DictionaryEntityToDictionary
import com.github.cheapmon.balalaika.data.prefs.PreferenceStorage
import com.github.cheapmon.balalaika.data.repositories.dictionary.DictionaryDataSource
import com.github.cheapmon.balalaika.data.result.LoadState
import com.github.cheapmon.balalaika.data.result.tryLoad
import com.github.cheapmon.balalaika.model.Dictionary
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@Suppress("EXPERIMENTAL_API_USAGE")
@Singleton
class DictionaryRepository @Inject internal constructor(
    private val storage: PreferenceStorage,
    private val dao: DictionaryDao,
    @Local private val localDataSource: DictionaryDataSource,
    @Remote private val remoteDataSource: DictionaryDataSource,
    private val mapper: DictionaryEntityToDictionary
) {
    fun getOpenDictionary(): Flow<Dictionary?> =
        storage.observableOpenDictionary
            .flatMapLatest { dao.findById(it) }
            .map { it?.let { mapper(it) } }

    fun getInstalledDictionaries(): Flow<List<Dictionary>> =
        dao.getAll().map { list -> list.map { mapper(it) } }

    fun getLocalDictionaries(): Flow<List<Dictionary>> = flow {
        emit(localDataSource.getDictionaryList())
    }

    fun getRemoteDictionaries(): Flow<LoadState<List<Dictionary>, Throwable>> = tryLoad {
        remoteDataSource.getDictionaryList()
    }
}
