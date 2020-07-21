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

/**
 * Wrapper for sending `.csv` contents
 */
data class DictionaryContents(
    /** `categories.csv` file contents */
    val categories: String,
    /** `lexemes.csv` file contents */
    val lexemes: String,
    /** `full_forms.csv` file contents */
    val fullForms: String,
    /** `properties.csv` file contents */
    val properties: String,
    /** `views.csv` file contents */
    val views: String
)
