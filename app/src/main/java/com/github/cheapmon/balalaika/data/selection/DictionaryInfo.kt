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

import androidx.annotation.StringRes
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.squareup.moshi.JsonClass
import java.util.SortedSet
import java.util.TreeSet

/**
 * Partial [dictionary][Dictionary] information stripped of app-specific data
 *
 * Instances of this are created by the [API][DictionaryApi]. We use this indirection to ensure
 * that no app-specific fields like [Dictionary.isActive] can be set by a remote source.
 */
@JsonClass(generateAdapter = true)
data class DictionaryInfo(
    /** Unique identifier of this dictionary */
    val id: String,
    /** Version number of this dictionary */
    val version: Int,
    /** Name of this dictionary */
    val name: String,
    /** Summary text of this dictionary */
    val summary: String,
    /** Authors of this dictionary */
    val authors: String,
    /** Additional information for this dictionary */
    val additionalInfo: String,
    /** Additional tags */
    @Transient val tags: SortedSet<Tag> = TreeSet()
) {
    /** Convert this [DictionaryInfo] to a [Dictionary] */
    fun toDictionary() =
        Dictionary(
            id = id,
            version = version,
            name = name,
            summary = summary,
            authors = authors,
            additionalInfo = additionalInfo
        )

    /** Add tags to this [DictionaryInfo] */
    fun addTag(tag: Tag) {
        tags.add(tag)
    }

    /** Additional tags for [DictionaryInfo] */
    enum class Tag(@StringRes msgId: Int) {
        /** Dictionary is already in the library */
        Library(R.string.selection_is_installed),

        /** Dictionary is outdated */
        Outdated(R.string.selection_is_outdated),

        /** Dictionary is up-to-date */
        UpToDate(R.string.selection_is_uptodate),

        /** Dictionary can be updated */
        Updates(R.string.selection_is_updatable),

        /** Dictionary is opened */
        Opened(R.string.affirm)
    }

    companion object {
        fun fromDictionary(dictionary: Dictionary) = DictionaryInfo(
            id = dictionary.id,
            version = dictionary.version,
            name = dictionary.name,
            summary = dictionary.summary,
            authors = dictionary.authors,
            additionalInfo = dictionary.additionalInfo
        )
    }
}
