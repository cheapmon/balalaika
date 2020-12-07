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

/**
 * Description and metadata for a dictionary
 *
 * Encapsulates information about a dictionary from any source in the user interface.
 * Dictionaries are identified by their [id], which must be unique across all sources, local or
 * remote. Using this approach, we enable dictionaries to be included in the application bundle
 * and to be updated from a remote source.
 *
 * Invalidation of dictionaries is achieved via the [version] number. There is currently no way
 * implemented to compare the contents of two dictionaries with the same [id].
 *
 * @property id Unique identifier of this dictionary
 * @property version Version number of this dictionary
 * @property name Display name of this dictionary
 * @property summary Summary text for this dictionary and its contents
 * @property authors List of authors of this dictionary
 * @property additionalInfo Additional information for this dictionary (e.g. its license)
 */
interface SimpleDictionary {
    val id: String
    val version: Int
    val name: String
    val summary: String
    val authors: String
    val additionalInfo: String
}

/**
 * Return `true` if two dictionaries are equal
 *
 * _Note_: Since [SimpleDictionary] is an interface, we can't override [equals]. Instead, we use
 * a simple infix function.
 */
infix fun SimpleDictionary.sameAs(other: SimpleDictionary): Boolean =
    if (this is Dictionary && other is Dictionary) {
        this == other
    } else if (this is InstalledDictionary && other is InstalledDictionary) {
        this == other
    } else if (this is DownloadableDictionary && other is DownloadableDictionary) {
        this == other
    } else {
        false
    }

/**
 * Base implementation of [SimpleDictionary]
 *
 * @see SimpleDictionary
 */
data class Dictionary(
    override val id: String,
    override val version: Int,
    override val name: String,
    override val summary: String,
    override val authors: String,
    override val additionalInfo: String
) : SimpleDictionary

/**
 * A [dictionary][Dictionary] that has been installed by the user
 *
 * @property dictionary Dictionary and associated metadata
 * @property isOpened `true` if the dictionary is currently open
 */
data class InstalledDictionary(
    private val dictionary: Dictionary,
    val isOpened: Boolean = false
) : SimpleDictionary by dictionary

/**
 * A [dictionary][Dictionary] that can be downloaded by the user
 *
 * @property Dictionary and associated metadata
 * @property isInLibrary `true` if the dictionary has been added to the user's library
 */
data class DownloadableDictionary(
    private val dictionary: Dictionary,
    val isInLibrary: Boolean = false
) : SimpleDictionary by dictionary
