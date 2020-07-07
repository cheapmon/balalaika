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

import arrow.core.getOrElse
import arrow.core.right
import arrow.fx.IO
import arrow.fx.extensions.fx
import com.github.cheapmon.balalaika.core.InstallState
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.db.entities.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.di.DictionaryProviderType
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest

@ActivityScoped
class DictionaryMediator @Inject constructor(
    dao: DictionaryDao,
    private val providers: Map<DictionaryProviderType, @JvmSuppressWildcards DictionaryProvider>,
    private val extractor: ZipExtractor,
    private val importer: CsvEntityImporter
) {
    private val state = MutableStateFlow(false)

    val dictionaries = state.flatMapLatest { dao.getAll() }.mapLatest { d ->
        val list = providers.map { (_, v) -> v.getDictionaryList().attempt().suspended() }
        when {
            list.all { it.isLeft() } -> d.map { InstallState.Installed(it) }.right()
            else -> {
                val result = (d + list.flatMap { it.getOrElse { emptyList() } })
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
                result.right()
            }
        }
    }

    suspend fun installDictionary(dictionary: Dictionary): IO<Unit> = IO.fx {
        val provider = providers[dictionary.providerKey]
        if (provider == null) {
            throw IllegalStateException("No provider specified")
        } else {
            val input = !provider.getDictionary(dictionary.externalId)
            val zipFile = !extractor.saveZip(dictionary.externalId, input)
            val contents = !extractor.extract(zipFile)
            !importer.import(contents)
        }
    }

    fun refresh() {
        state.value = !state.value
    }
}
