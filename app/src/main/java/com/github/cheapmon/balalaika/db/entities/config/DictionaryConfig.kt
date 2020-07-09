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
package com.github.cheapmon.balalaika.db.entities.config

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.github.cheapmon.balalaika.db.entities.category.Category
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.db.entities.view.DictionaryView

@Entity(
    tableName = "config",
    foreignKeys = [
        ForeignKey(
            entity = Dictionary::class,
            parentColumns = ["id"],
            childColumns = ["id"]
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id", "dictionary_id"],
            childColumns = ["order_by", "id"]
        ),
        ForeignKey(
            entity = DictionaryView::class,
            parentColumns = ["id", "dictionary_id"],
            childColumns = ["filter_by", "id"]
        )
    ],
    indices = [
        Index("order_by", "id"),
        Index("filter_by", "id")
    ]
)
data class DictionaryConfig(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "order_by") val orderBy: String,
    @ColumnInfo(name = "filter_by") val filterBy: String
)
