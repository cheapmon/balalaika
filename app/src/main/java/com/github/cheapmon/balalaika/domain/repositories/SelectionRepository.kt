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

import android.content.Context
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.domain.services.DictionaryService
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

// TODO: Replace with real implementation
@ActivityScoped
class SelectionRepository @Inject constructor(
    @ApplicationContext context: Context,
    private val service: DictionaryService
) {
    private var _dictionaryList = listOf(
        Dictionary(
            1,
            externalId = "dic_a",
            version = 3,
            name = "Dictionary A",
            summary = context.getString(R.string.impsum),
            additionalInfo = context.getString(R.string.impsum),
            authors = "Simon Kaleschke",
            isActive = true,
            isInstalled = true,
            url = "https://www.example.org"
        ),
        Dictionary(
            2,
            externalId = "dic_b",
            version = 2,
            name = "Dictionary B",
            summary = "BBB",
            additionalInfo = "https://www.example.org is a very important website",
            authors = "Thomas the tank engine",
            isActive = false,
            isInstalled = true,
            url = "https://www.example.org"
        )
    )

    private val _dictionaries = ConflatedBroadcastChannel(_dictionaryList)

    val dictionaries = _dictionaries.asFlow()

    fun getRemoteDictionaries(forceRefresh: Boolean) = flow {
        emit(DictionaryService.Response.Pending)
        emit(service.getDictionariesFromRemoteSource(forceRefresh))
    }

    fun getDictionary(id: Long): Flow<Dictionary?> = _dictionaries.asFlow()
        .map { list -> list.find { item -> item.dictionaryId == id } }

    fun toggleActive(id: Long) {
    }

    fun addDictionary(dictionary: Dictionary) {
    }

    fun removeDictionary(id: Long) {
    }
}
