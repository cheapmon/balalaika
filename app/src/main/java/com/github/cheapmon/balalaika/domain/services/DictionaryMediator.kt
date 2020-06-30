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

import com.github.cheapmon.balalaika.db.entities.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.domain.InstallState
import com.github.cheapmon.balalaika.domain.Response
import com.github.cheapmon.balalaika.domain.orEmpty
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest

@ActivityScoped
class DictionaryMediator @Inject constructor(
    dao: DictionaryDao,
    service: DictionaryService,
    files: FilesService
) {
    private val state = MutableStateFlow(false)

    val dictionaries = state.flatMapLatest {
        val d = dao.getAll()
        val s = service.getDictionariesFromRemoteSource()
        val f = files.getLocalDictionaries()
        combine(d, s, f) { a, b, c -> Triple(a, b, c) }
    }.mapLatest { (d, s, f) ->
        if (s.isPending() || f.isPending()) {
            Response.Pending
        } else if (s.isFailure() && f.isFailure()) {
            Response.Success(d.map { InstallState.Installed(it) })
        } else {
            val result = (d + s.orEmpty() + f.orEmpty())
                .groupBy { it.externalId }
                .map { (id, list) ->
                    val newest = list.maxBy { it.version } ?: throw IllegalStateException()
                    val current = d.find { it.externalId == id }
                    when {
                        d.contains(newest) -> InstallState.Installed(newest)
                        current != null -> InstallState.Updatable(current)
                        else -> InstallState.Installable(newest)
                    }
                }
            Response.Success(result)
        }
    }

    fun refresh() {
        state.value = !state.value
    }
}
