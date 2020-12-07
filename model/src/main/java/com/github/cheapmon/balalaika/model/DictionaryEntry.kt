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
import java.util.*

/**
 * Full entry in a dictionary, grouped with its base, properties and bookmarks
 *
 * @property id Unique identifier of this dictionary entry
 * @property representation Orthographic representation of this dictionary entry
 * @property base (Optional) base of this dictionary entry
 * @property properties Properties associated with this dictionary entry, sorted by the data
 *                      category they belong to
 * @property bookmark (Optional) bookmark
 */
@Parcelize
data class DictionaryEntry(
    val id: String,
    val representation: String,
    val base: DictionaryEntry?,
    val properties: SortedMap<DataCategory, List<Property>>,
    val bookmark: Bookmark?
) : Parcelable
