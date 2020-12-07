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
 * Bookmark metadata for a [dictionary entry][DictionaryEntry]
 *
 * _Note_: Since this might be expanded in the future, we define bookmarks in a separate class.
 */
@Parcelize
class Bookmark : Parcelable {
    /**
     * Return `true` if this bookmark is equal to `other`
     *
     * _Note_: Since bookmark has no associated properties, equality is trivial.
     */
    override fun equals(other: Any?): Boolean = other is Bookmark

    /**
     * Hashcode of this bookmark
     */
    override fun hashCode(): Int = javaClass.hashCode()
}
