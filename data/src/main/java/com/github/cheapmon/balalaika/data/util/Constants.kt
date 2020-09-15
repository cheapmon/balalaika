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
package com.github.cheapmon.balalaika.data.util

internal object Constants {
    /** Default dictionary order */
    const val DEFAULT_CATEGORY_ID = "default"

    /** Default dictionary view */
    const val DEFAULT_DICTIONARY_VIEW_ID = "all"

    /** Number of elements loaded per page */
    const val PAGE_SIZE = 15

    /** Paging start index */
    const val PAGING_START_INDEX = 1L

    /** Assets file with dictionary list */
    const val DICTIONARY_LIST_FILE = "dictionaries.json"

    /** Base URL of dictionary API */
    // TODO: Setup
    const val SERVER_URL = "https://www.example.org/"

    /** Base URL of Wordnet API */
    const val WORDNET_URL: String = "http://wordnet-rdf.princeton.edu/rdf/"
}
