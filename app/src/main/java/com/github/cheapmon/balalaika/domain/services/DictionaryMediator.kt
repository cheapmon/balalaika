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
package com.github.cheapmon.balalaika.domain.services

import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.db.entities.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.domain.InstallState
import com.github.cheapmon.balalaika.domain.Response
import com.github.cheapmon.balalaika.domain.orEmpty
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@ActivityScoped
class DictionaryMediator @Inject constructor(
    dao: DictionaryDao,
    service: DictionaryService,
    files: FilesService
) {
    private val state = MutableStateFlow(false)

    private val all = state.flatMapLatest {
        val d = dao.getAll()
        val s = service.getDictionariesFromRemoteSource()
        val f = files.getLocalDictionaries()
        combine(d, s, f) { a, b, c -> Triple(a, b, c) }
    }

    val local: Flow<List<InstallState<Dictionary>>> = all.mapLatest { (d, s, f) ->
        if ((s is Response.Failure) && (f is Response.Failure)) {
            d.map { InstallState.Installed(it) }
        } else {
            val list = s.orEmpty() + f.orEmpty()
            d.map {
                if (list.contains(it)) {
                    InstallState.Updatable(it)
                } else {
                    InstallState.Installed(it)
                }
            }
        }
    }

    val download: Flow<List<InstallState<Dictionary>>> = all.mapLatest { (d, s, f) ->
        if ((s is Response.Failure) && (f is Response.Failure)) {
            d.map { InstallState.Installed(it) }
        } else {
            (d + s.orEmpty() + f.orEmpty())
                .groupBy { it.externalId }
                .map { (id, list) ->
                    val newest = list.maxBy { it.version } ?: throw IllegalStateException()
                    when {
                        d.contains(newest) -> InstallState.Installed(newest)
                        d.any { it.externalId == id } -> InstallState.Updatable(newest)
                        else -> InstallState.Installable(newest)
                    }
                }
        }
    }

    fun refresh() {
        state.value = !state.value
    }
}
