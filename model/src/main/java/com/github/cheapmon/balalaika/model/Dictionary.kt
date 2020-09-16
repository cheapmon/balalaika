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
package com.github.cheapmon.balalaika.model

private interface SimpleDictionary {
    val id: String
    val version: Int
    val name: String
    val summary: String
    val authors: String
    val additionalInfo: String
}

data class Dictionary(
    override val id: String,
    override val version: Int,
    override val name: String,
    override val summary: String,
    override val authors: String,
    override val additionalInfo: String
) : SimpleDictionary

data class InstalledDictionary(
    private val dictionary: Dictionary,
    val isOpened: Boolean = false
) : SimpleDictionary by dictionary

data class DownloadableDictionary(
    private val dictionary: Dictionary,
    val isInLibrary: Boolean = false
) : SimpleDictionary by dictionary
