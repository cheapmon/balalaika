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
package com.github.cheapmon.balalaika.data.entities.history

import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.ui.history.HistoryFragment
import com.github.cheapmon.balalaika.ui.search.SearchFragment
import java.io.Serializable

/**
 * Optional restriction of a search query
 *
 * _Note_: This class is [serializable][Serializable] and can be passed between fragments.
 *
 * @see SearchFragment
 * @see HistoryFragment
 */
sealed class SearchRestriction : Serializable {
    /** No additonal search restriction */
    object None : SearchRestriction()

    /** A search restriction consisting of a [category][Category] and a [restriction] string */
    data class Some(
        /** [Category] */
        val category: Category,
        /** Restriction */
        val restriction: String
    ) : SearchRestriction()
}