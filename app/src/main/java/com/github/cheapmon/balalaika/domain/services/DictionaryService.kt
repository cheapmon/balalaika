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

import android.content.Context
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.domain.Response
import com.github.cheapmon.balalaika.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull

// TODO: Replace with real implementation
@ActivityScoped
class DictionaryService @Inject constructor(
    @ApplicationContext context: Context,
    private val constants: Constants
) {
    fun getDictionariesFromRemoteSource(): Flow<Response<Dictionary>> = flow {
        emit(Response.Pending)
        emit(fakeDictionaries())
    }

    private val _dictionaryList = listOf(
        Dictionary(
            1,
            externalId = "dic_a",
            version = 3,
            name = "Dictionary A",
            summary = context.getString(R.string.impsum),
            additionalInfo = context.getString(R.string.impsum),
            authors = "Simon Kaleschke",
            isActive = false,
            url = "https://www.example.org"
        ),
        Dictionary(
            2,
            externalId = "dic_b",
            version = 2,
            name = "Dictionary B",
            summary = "BBB",
            additionalInfo = "https://www.example.org is a very important website",
            authors = "Thomas the tank engine",
            isActive = false,
            url = "https://www.example.org"
        )
    )

    private suspend fun fakeDictionaries(): Response<Dictionary> {
        var response: Response<Dictionary>? = null
        withTimeoutOrNull(constants.REMOTE_TIMEOUT) {
            delay((100..3000).random().toLong())
            response = Response.Success(_dictionaryList)
        }
        return response ?: Response.Failure(IOException("Could not load dictionaries"))
    }
}
