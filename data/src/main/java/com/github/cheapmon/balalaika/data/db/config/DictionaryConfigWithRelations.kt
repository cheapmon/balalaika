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
package com.github.cheapmon.balalaika.data.db.config

import androidx.room.Embedded
import androidx.room.Relation
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewWithCategories

internal data class DictionaryConfigWithRelations(
    @Embedded val historyItem: DictionaryConfig,
    @Relation(
        parentColumn = "sort_by",
        entityColumn = "id"
    ) val category: CategoryEntity?,
    @Relation(
        parentColumn = "filter_by",
        entityColumn = "id",
        entity = DictionaryViewEntity::class
    ) val view: DictionaryViewWithCategories
)
