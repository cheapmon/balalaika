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

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Property of a [dictionary entry][DictionaryEntry] based on a [data category][DataCategory]
 */
sealed class Property : Parcelable {
    /**
     * Property for audio files associated with a [dictionary entry][DictionaryEntry]
     *
     * @property name Display name of this property
     * @property fileName Name of audio file associated with this property
     */
    @Parcelize
    data class Audio(val name: String, val fileName: String) : Property()

    /**
     * Property for example sentences
     *
     * @property name Example definition
     * @property content Example sentence for the [definition][name]
     */
    @Parcelize
    data class Example(val name: String, val content: String) : Property()

    /**
     * Property for morphological information
     *
     * @property parts Morphological parts of an orthographic representation
     */
    @Parcelize
    data class Morphology(val parts: List<String>) : Property()

    /**
     * Plain-text property value
     *
     * @property value Property value
     */
    @Parcelize
    data class Plain(val value: String) : Property()

    /**
     * Property for in-dictionary reference
     *
     * @property entry Dictionary entry this property points to
     */
    @Parcelize
    data class Reference(val entry: DictionaryEntry) : Property()

    /**
     * Simple key-value property
     *
     * @property value Property value
     */
    @Parcelize
    data class Simple(val value: String) : Property()

    /**
     * Property for hyperlinks
     *
     * @property name Display name of this hyperlink
     * @property url Hyperlink
     */
    @Parcelize
    data class Url(val name: String, val url: String) : Property()

    /**
     * Property for additional information from the Wordnet project
     *
     * @property name Display name of this value
     * @property url Wordnet-URL to fetch additional information from
     */
    @Parcelize
    data class Wordnet(val name: String, val url: String) : Property()
}
