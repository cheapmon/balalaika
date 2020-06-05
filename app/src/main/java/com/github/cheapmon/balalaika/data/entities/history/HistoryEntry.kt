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

import androidx.room.*
import com.github.cheapmon.balalaika.data.entities.category.Category

@Entity(
    tableName = "search_history",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"]
        )
    ],
    indices = [Index(value = ["category_id"])]
)
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val historyEntryId: Long = 0,
    @ColumnInfo(name = "category_id") val categoryId: Long? = null,
    @ColumnInfo(name = "restriction") val restriction: String? = null,
    @ColumnInfo(name = "query") val query: String
)
