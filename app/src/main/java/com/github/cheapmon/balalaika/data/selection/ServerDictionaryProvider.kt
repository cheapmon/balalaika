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

import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.util.Constants
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlinx.coroutines.withTimeout

/** [Dictionary provider][DictionaryProvider] for remote files */
@ActivityScoped
class ServerDictionaryProvider @Inject constructor(
    private val constants: Constants,
    private val api: DictionaryApi
) : DictionaryProvider {
    override suspend fun getDictionaryList(): List<Dictionary> =
        withTimeout(constants.REMOTE_TIMEOUT) {
            api.listDictionaries()
        }.map { it.toDictionary() }

    override suspend fun getDictionary(id: String): ByteArray {
        return withTimeout(constants.REMOTE_TIMEOUT) { api.getDictionary(id) }.bytes()
    }
}
