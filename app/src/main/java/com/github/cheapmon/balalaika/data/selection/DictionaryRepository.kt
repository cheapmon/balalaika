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
package com.github.cheapmon.balalaika.data.selection

import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.db.entities.dictionary.DictionaryDao
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

// TODO: Replace with real implementation
@ActivityScoped
class DictionaryRepository @Inject constructor(
    private val dictionaryDao: DictionaryDao,
    private val mediator: DictionaryMediator
) {
    val dictionaries = mediator.dictionaries

    suspend fun toggleActive(dictionary: Dictionary) {
        if (dictionary.isActive) {
            dictionaryDao.setInactive(dictionary.externalId)
        } else {
            dictionaryDao.setActive(dictionary.externalId)
        }
    }

    suspend fun addDictionary(dictionary: Dictionary) {
        dictionaryDao.insertAll(listOf(dictionary))
        // TODO: Import everything else
    }

    suspend fun removeDictionary(externalId: String) {
        dictionaryDao.remove(externalId)
        // TODO: Remove everything else
    }

    fun refresh() {
        mediator.refresh()
    }
}
