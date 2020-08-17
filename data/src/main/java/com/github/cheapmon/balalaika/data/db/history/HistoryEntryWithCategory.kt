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
package com.github.cheapmon.balalaika.data.db.history

import androidx.room.Embedded
import androidx.room.Relation
import com.github.cheapmon.balalaika.data.db.DatabaseEntity
import com.github.cheapmon.balalaika.data.db.category.Category

/**
 * [History entry][HistoryEntry] optionally linked with a [category][Category] if the search has
 * been restricted
 *
 * _Note_: Instances of this class can be auto-generated by Room.
 */
internal data class HistoryEntryWithCategory(
    /** [History entry][HistoryEntry] */
    @Embedded val historyEntry: HistoryEntry,
    /** Optional [category][Category] if the search has been restricted */
    @Relation(parentColumn = "category_id", entityColumn = "id") val category: Category?
) : DatabaseEntity
