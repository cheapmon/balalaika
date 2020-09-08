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

import android.content.Context
import com.github.cheapmon.balalaika.data.di.IoDispatcher
import com.github.cheapmon.balalaika.data.util.Constants
import com.github.cheapmon.balalaika.model.Dictionary
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.FileNotFoundException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Singleton
internal class LocalDictionaryDataSource @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    moshi: Moshi
) : DictionaryDataSource {
    private val fileNames = context.assets.list("")
        .orEmpty()
        .filter { it.endsWith(".zip") }

    private val dictionaryList by lazy {
        context.assets.open(Constants.DICTIONARY_LIST_FILE)
            .bufferedReader()
            .readText()
            .let { json ->
                val type = Types.newParameterizedType(List::class.java, DictionaryJson::class.java)
                val adapter: JsonAdapter<List<DictionaryJson>> = moshi.adapter(type)
                adapter.fromJson(json)
            }.orEmpty()
            .map {
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

    override suspend fun getDictionaryList(): List<Dictionary> =
        withContext(dispatcher) { dictionaryList }

    override suspend fun getDictionaryContents(id: String): ByteArray {
        val fileName = fileNames.find { it.startsWith(id) }
            ?: throw FileNotFoundException()
        return withContext(dispatcher) { context.assets.open(fileName).use { it.readBytes() } }
    }
}
