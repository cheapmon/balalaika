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
package com.github.cheapmon.balalaika.domain.services

import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.domain.misc.ListResponse
import com.github.cheapmon.balalaika.domain.misc.Response
import com.github.cheapmon.balalaika.util.Constants
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException
import javax.inject.Inject

@ActivityScoped
class ServerDictionaryProvider @Inject constructor(
    private val constants: Constants
) : DictionaryProvider() {
    override suspend fun getFromSource(): ListResponse<Dictionary> {
        var response: ListResponse<Dictionary>? = null
        withTimeoutOrNull(constants.REMOTE_TIMEOUT) {
            // TODO: Get remote dictionaries
            response = Response.Success(listOf())
        }
        return response ?: Response.Failure(IOException("Could not download dictionaries"))
    }
}
