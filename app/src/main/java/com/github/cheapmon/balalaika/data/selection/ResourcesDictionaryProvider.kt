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

import arrow.core.left
import arrow.core.right
import com.github.cheapmon.balalaika.core.resources.ResourceProvider
import com.github.cheapmon.balalaika.di.DictionaryProviderType
import com.github.cheapmon.balalaika.util.Constants
import dagger.hilt.android.scopes.ActivityScoped
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull

@ActivityScoped
class ResourcesDictionaryProvider @Inject constructor(
    private val constants: Constants,
    private val resources: ResourceProvider,
    private val parser: YamlDictionaryParser
) : DictionaryProvider {
    override suspend fun getDictionaryList() = flow {
        val response = withTimeoutOrNull(constants.LOCAL_TIMEOUT) {
            parser.parse(resources.dictionaryList, DictionaryProviderType.RESOURCES)
        } ?: IOException("Could not read dictionaries").left()
        emit(response)
    }

    override suspend fun getDictionary(externalId: String) = flow {
        try {
            val file = resources.getDictionaryZip(externalId)
                ?: throw IOException("Could not find .zip file $externalId")
            emit(file.right())
        } catch (ex: IOException) {
            emit(ex.left())
        }
    }
}
