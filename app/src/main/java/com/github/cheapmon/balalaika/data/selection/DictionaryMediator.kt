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

import com.github.cheapmon.balalaika.data.Result
import com.github.cheapmon.balalaika.data.tryRun
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.db.entities.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.util.logger
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * Mediator to merge [dictionaries][Dictionary] from different [sources][DictionaryProvider]
 *
 * All available providers are queried for a dictionary list, and all dictionaries are collected
 * into a single list. The resolution strategy is as follows:
 * - If a dictionary hasn't been seen before, it is added into the database without any changes.
 * - If there are multiple instances of the same dictionary, only the newest is kept.
 *
 * Any errors encountered while querying the providers are logged to the console and ignored
 * otherwise.
 *
 * _Note_: We annotate each dictionary with the provider that produced it so that we know which
 * provider to use for installation of the respective dictionary.
 *
 * @see DictionaryApi
 */
@ActivityScoped
class DictionaryMediator @Inject constructor(
    private val dao: DictionaryDao,
    private val providers: Map<String, @JvmSuppressWildcards DictionaryProvider>,
    private val extractor: ZipExtractor,
    private val importer: CsvEntityImporter
) {
    /** Update [dictionary list][Dictionary] */
    suspend fun updateDictionaryList() {
        val d = dao.getAll().first()
        val p = providers.entries.flatMap { (k, v) ->
            when (val result = tryRun { v.getDictionaryList() }) {
                is Result.Error -> {
                    logger { error("Provider $k failed loading dictionaries with\n${result.cause}") }
                    emptyList()
                }
                is Result.Success -> result.data.map { it.copy(provider = k) }
            }
        }
        p.groupBy { it.id }
            .forEach { (id, list) ->
                val newest = list.maxBy { it.version }
                    ?: throw IllegalStateException()
                val current = d.find { it.id == id }
                when {
                    current != null -> {
                        if (current.isInstalled && current.version < newest.version) {
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

    /**
     * Install a [dictionary][Dictionary] into the application using its
     * [provider][DictionaryApi]
     *
     * Installation consists of the following steps:
     * 1. Download of a `.zip` file from the local or remote source
     * 2. Extraction of `.csv` file contents from the `.zip` file
     * 3. Parsing and import of entities into the database
     *
     * Errors encountered while installing the dictionary are logged to the console and ignored
     * otherwise.
     *
     * @see ZipExtractor
     * @see CsvEntityImporter
     */
    suspend fun installDictionary(dictionary: Dictionary): Result<Unit, Throwable> = tryRun {
        val provider = providers[dictionary.provider]
        if (provider == null) {
            throw IllegalStateException("No provider specified: $dictionary")
        } else {
            val input = provider.getDictionary(dictionary.id)
            val zipFile = extractor.saveZip(dictionary.id, input)
            val contents = extractor.extract(zipFile)
            importer.import(dictionary.id, contents)
            extractor.removeZip(dictionary.id)
            Unit
        }
    }
}
