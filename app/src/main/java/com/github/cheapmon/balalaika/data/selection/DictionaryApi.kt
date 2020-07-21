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

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Read dictionaries from a remote REST API
 */
interface DictionaryApi {
    /**
     * List of available dictionaries
     *
     * Dictionaries are provided as a simple JSON file, e.g.:
     * ```json
     * [
     *   {
     *     "name": "Sample dictionary",
     *     "id": "sample",
     *     "version": 1,
     *     "authors": "",
     *     "summary": "",
     *     "additionalInfo": ""
     *   }
     * ]
     * ```
     */
    @GET("dictionary")
    suspend fun listDictionaries(): List<DictionaryInfo>

    /**
     * `.zip` file with dictionary contents
     *
     * The `.zip` file contains five `.csv` files:
     * 1. `categories.csv`
     * 2. `lexemes.csv`
     * 3. `full_forms.csv`
     * 4. `properties.csv`
     * 5. `views.csv`
     *
     * @see CsvEntityImporter
     */
    @GET("dictionary/{id}")
    suspend fun getDictionary(@Path("id") id: String): ResponseBody
}
