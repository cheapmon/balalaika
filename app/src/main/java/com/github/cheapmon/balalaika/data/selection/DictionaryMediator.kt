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

import com.github.cheapmon.balalaika.db.entities.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.core.InstallState
import com.github.cheapmon.balalaika.core.Response
import com.github.cheapmon.balalaika.core.orEmpty
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest

@ActivityScoped
class DictionaryMediator @Inject constructor(
    dao: DictionaryDao,
    providers: Set<@JvmSuppressWildcards DictionaryProvider>
) {
    private val state = MutableStateFlow(false)

    val dictionaries = state.flatMapLatest {
        val flows = providers.map { it.get() }
        val combined = combine(flows) { f -> f.asList() }
        dao.getAll().combine(combined) { d, p -> Pair(d, p) }
    }.mapLatest { (d, list) ->
        when {
            list.any { it.isPending() } -> Response.Pending
            list.all { it.isFailure() } -> Response.Success(d.map { InstallState.Installed(it) })
            else -> {
                val result = (d + list.flatMap { it.orEmpty() })
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
    }

    fun refresh() {
        state.value = !state.value
    }
}
