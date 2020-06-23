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
package com.github.cheapmon.balalaika.ui.selection

import android.content.Context
import com.github.cheapmon.balalaika.R
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.delay
import java.io.IOException
import javax.inject.Inject

// TODO: Replace with real implementation
@ActivityScoped
class DictionaryService @Inject constructor(
    @ApplicationContext context: Context
) {
    sealed class Response {
        object Pending : Response()
        data class Success(val dictionaries: List<Dictionary>) : Response()
        data class Failed(val cause: Throwable) : Response()
    }

    private var currentResult: Response? = null

    suspend fun getDictionariesFromRemoteSource(forceRefresh: Boolean = false): Response {
        val lastResult = currentResult
        return if (lastResult is Response.Success && !forceRefresh) {
            lastResult
        } else {
            val newResult = fakeDictionaries()
            currentResult = newResult
            newResult
        }
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
            active = true
        ),
        Dictionary(
            2,
            externalId = "dic_b",
            version = 2,
            name = "Dictionary B",
            summary = "BBB",
            additionalInfo = "https://www.example.org is a very important website",
            authors = "Thomas the tank engine",
            active = false
        )
    )

    private suspend fun fakeDictionaries(): Response {
        delay((1000..2500).random().toLong())
        return if ((0..9).random() > 5) Response.Success(_dictionaryList)
        else Response.Failed(IOException("Loading dictionaries failed"))
    }
}
