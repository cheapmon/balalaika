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

import com.github.cheapmon.balalaika.di.IoDispatcher
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Parse a dictionary list from JSON
 *
 * Uses Moshi for deserialization. This is specifically meant for local files, since Retrofit
 * is used for remote sources.
 *
 * @see DictionaryApi
 */
@ActivityScoped
class JsonDictionaryParser @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val moshi: Moshi
) {
    /** Parse dictionary list */
    suspend fun parse(contents: String): List<DictionaryInfo> {
        return withContext(dispatcher) {
            val type = Types.newParameterizedType(List::class.java, DictionaryInfo::class.java)
            val adapter: JsonAdapter<List<DictionaryInfo>> = moshi.adapter(type)
            adapter.fromJson(contents).orEmpty()
        }
    }
}
