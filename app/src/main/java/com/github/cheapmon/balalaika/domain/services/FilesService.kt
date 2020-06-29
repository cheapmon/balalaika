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
import com.github.cheapmon.balalaika.domain.Response
import com.github.cheapmon.balalaika.util.Constants
import dagger.hilt.android.scopes.ActivityScoped
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

// TODO: Replace with real implementation
@ActivityScoped
class FilesService @Inject constructor(
    private val constants: Constants
) {
    fun getLocalDictionaries(): Flow<Response<Dictionary>> = flow {
        emit(Response.Pending)
        emit(fakeDictionaries())
    }

    private val _dictionaryList = listOf(
        Dictionary(
            1,
            externalId = "dic_a",
            version = 2,
            name = "Dictionary A",
            summary = "",
            additionalInfo = "",
            authors = "Simon Kaleschke",
            isActive = false,
            url = "https://www.example.org"
        ),
        Dictionary(
            2,
            externalId = "dic_b",
            version = 3,
            name = "Dictionary B",
            summary = "CCC",
            additionalInfo = "https://www.example.org is a very important website",
            authors = "Senf",
            isActive = false,
            url = "https://www.example.org"
        )
    )

    private suspend fun fakeDictionaries(): Response<Dictionary> {
        var result: Response<Dictionary>?
        withTimeout(constants.LOCAL_TIMEOUT) {
            delay((10..1200).random().toLong())
            result = Response.Success(_dictionaryList)
        }
        return result ?: Response.Failure(IOException("Could not read dictionaries from files"))
    }
}
