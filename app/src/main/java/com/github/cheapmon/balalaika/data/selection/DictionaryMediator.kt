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
import arrow.fx.IO
import arrow.fx.extensions.fx
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.db.entities.dictionary.DictionaryDao
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class DictionaryMediator @Inject constructor(
    private val dao: DictionaryDao,
    private val providers: Map<String, @JvmSuppressWildcards DictionaryProvider>,
    private val extractor: ZipExtractor,
    private val importer: CsvEntityImporter
) {
    suspend fun updateDictionaryList() {
        val d = dao.getAllInstalled()
        val p = providers.entries.flatMap { (k, v) ->
            v.getDictionaryList().attempt().suspended().getOrElse { emptyList() }.map { it.copy(provider = k) }
        }
        val dictionaries = p.groupBy { it.externalId }
            .map { (id, list) ->
                val newest = list.maxBy { it.version }
                    ?: throw IllegalStateException()
                val current = d.find { it.externalId == id }
                when {
                    current != null -> {
                        if (current.version < newest.version) {
                            current.copy(isUpdatable = true)
                        } else {
                            current
                        }
                    }
                    else -> newest.copy(isActive = false, isInstalled = false, isUpdatable = false)
                }
            }
        dao.insertAll(dictionaries)
    }

    fun installDictionary(dictionary: Dictionary): IO<Unit> = IO.fx {
        val provider = providers[dictionary.provider]
        if (provider == null) {
            throw IllegalStateException("No provider specified")
        } else {
            val input = !provider.getDictionary(dictionary.externalId)
            val zipFile = !extractor.saveZip(dictionary.externalId, input)
            val contents = !extractor.extract(zipFile)
            !importer.import(contents)
        }
    }
}
