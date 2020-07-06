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
import arrow.core.left
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.di.DictionaryProviderType
import com.github.cheapmon.balalaika.util.Constants
import dagger.hilt.android.scopes.ActivityScoped
import java.io.IOException
import java.util.zip.ZipFile
import javax.inject.Inject
import kotlinx.coroutines.withTimeoutOrNull

@ActivityScoped
class ServerDictionaryProvider @Inject constructor(
    private val constants: Constants,
    private val parser: YamlDictionaryParser
) : DictionaryProvider {
    override suspend fun getDictionaryList(): Either<Throwable, List<Dictionary>> {
        return withTimeoutOrNull(constants.REMOTE_TIMEOUT) {
            parser.parse("dictionaries: []".byteInputStream(), DictionaryProviderType.SERVER)
        } ?: IOException("Could not download dictionaries").left()
    }

    override suspend fun getDictionary(externalId: String): Either<Throwable, ZipFile> {
        return NotImplementedError().left()
    }
}
