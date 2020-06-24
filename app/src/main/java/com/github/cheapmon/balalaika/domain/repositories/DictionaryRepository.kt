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
package com.github.cheapmon.balalaika.domain.repositories

import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.db.entities.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.domain.Response
import com.github.cheapmon.balalaika.domain.services.DictionaryService
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

// TODO: Replace with real implementation
@ActivityScoped
class DictionaryRepository @Inject constructor(
    private val dictionaryDao: DictionaryDao,
    private val service: DictionaryService
) {
    val installedDictionaries: Flow<List<Dictionary>> = dictionaryDao.getInstalled()

    fun getRemoteDictionaries(): Flow<Response<Dictionary>> =
        service.getDictionariesFromRemoteSource()

    fun getDictionary(id: Long): Flow<Dictionary?> = dictionaryDao.getById(id)

    fun toggleActive(id: Long) {
    }

    fun addDictionary(dictionary: Dictionary) {
    }

    fun removeDictionary(id: Long) {
    }
}
