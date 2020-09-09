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
package com.github.cheapmon.balalaika.data.repositories.dictionary

import com.github.cheapmon.balalaika.data.di.IoDispatcher
import com.github.cheapmon.balalaika.model.Dictionary
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Singleton
internal class RemoteDictionaryDataSource @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val api: DictionaryApi
) : DictionaryDataSource {
    override suspend fun getDictionaryList(): List<Dictionary> = withContext(dispatcher) {
        api.getDictionaryList().map {
            Dictionary(
                id = it.id,
                version = it.version,
                name = it.name,
                summary = it.summary,
                authors = it.authors,
                additionalInfo = it.additionalInfo,
            )
        }
    }

    override suspend fun hasDictionary(id: String, version: Int): Boolean =
        withContext(dispatcher) { api.hasDictionary(id, version) }

    override suspend fun getDictionaryContents(id: String, version: Int): ByteArray = withContext(dispatcher) {
        api.getDictionaryContents(id, version).bytes()
    }
}
