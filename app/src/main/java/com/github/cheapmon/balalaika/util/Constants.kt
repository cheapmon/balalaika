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
package com.github.cheapmon.balalaika.util

import javax.inject.Inject
import javax.inject.Singleton

/** Application-wide constants */
@Suppress("PropertyName")
@Singleton
class Constants @Inject constructor() {
    /** Assets file with dictionary list */
    val DICTIONARY_FILE: String = "dictionaries.json"

    /** Default dictionary order */
    val DEFAULT_CATEGORY_ID = "default"

    /** Default dictionary view */
    val DEFAULT_DICTIONARY_VIEW_ID = "all"

    /** Paging start index */
    val PAGING_START_INDEX = 1L

    /** Number of elements loaded per page */
    val PAGE_SIZE = 15

    /** Timeout (ms) for loading from a remote source */
    val REMOTE_TIMEOUT: Long = 2500

    /** Timeout (ms) for loading from the filesystem */
    val LOCAL_TIMEOUT: Long = 1000

    /** Base URL of dictionary API */
    // TODO: Setup
    val SERVER_URL: String = "https://example.org/"
}
