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

import android.content.Context
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.di.DictionaryProviderType
import com.github.cheapmon.balalaika.di.IoDispatcher
import com.github.cheapmon.balalaika.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

@ActivityScoped
class ResourcesDictionaryProvider @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val constants: Constants,
    private val parser: YamlDictionaryParser
) : DictionaryProvider {
    private val dictionaryList = context.assets.open(constants.DICTIONARY_FILE)
    private val zipFiles = context.assets.list("")
        .orEmpty()
        .filter { it.endsWith(".zip") }

    override suspend fun getDictionaryList(): Either<Throwable, List<Dictionary>> {
        return withTimeoutOrNull(constants.LOCAL_TIMEOUT) {
            parser.parse(dictionaryList, DictionaryProviderType.RESOURCES)
        } ?: IOException("Could not read dictionaries").left()
    }

    override suspend fun getDictionary(externalId: String): Either<Throwable, InputStream> {
        return try {
            withTimeout(constants.LOCAL_TIMEOUT) {
                val fileName = zipFiles.find { it.startsWith(externalId) }
                    ?: throw FileNotFoundException()
                withContext(dispatcher) { context.assets.open(fileName).right() }
            }
        } catch (ex: Exception) {
            ex.left()
        }
    }
}
