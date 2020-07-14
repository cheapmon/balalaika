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

import arrow.core.Either
import arrow.fx.IO
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.db.entities.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.util.logger
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlinx.coroutines.flow.first

@ActivityScoped
class DictionaryMediator @Inject constructor(
    private val dao: DictionaryDao,
    private val providers: Map<String, @JvmSuppressWildcards DictionaryProvider>,
    private val extractor: ZipExtractor,
    private val importer: CsvEntityImporter
) {
    suspend fun updateDictionaryList() {
        val d = dao.getAll().first()
        val p = providers.entries.flatMap { (k, v) ->
            when (val io = IO.effect { v.getDictionaryList() }.attempt().suspended()) {
                is Either.Left -> {
                    logger { error("Provider $k failed loading dictionaries with\n${io.a}") }
                    emptyList()
                }
                is Either.Right -> io.b.map { it.copy(provider = k) }
            }
        }
        p.groupBy { it.id }
            .forEach { (id, list) ->
                val newest = list.maxBy { it.version }
                    ?: throw IllegalStateException()
                val current = d.find { it.id == id }
                when {
                    current != null -> {
                        if (current.version < newest.version) {
                            dao.setUpdatable(current.id)
                        } else {
                            dao.setUnupdatable(current.id)
                        }
                    }
                    else -> {
                        val newDictionary = newest.copy(
                            isActive = false,
                            isInstalled = false,
                            isUpdatable = false
                        )
                        dao.insertAll(listOf(newDictionary))
                    }
                }
            }
    }

    fun installDictionary(dictionary: Dictionary) = IO.effect {
        val provider = providers[dictionary.provider]
        if (provider == null) {
            throw IllegalStateException("No provider specified: $dictionary")
        } else {
            val input = provider.getDictionary(dictionary.id)
            val zipFile = extractor.saveZip(dictionary.id, input)
            val contents = extractor.extract(zipFile)
            importer.import(dictionary.id, contents)
            extractor.removeZip(dictionary.id)
        }
    }
}
