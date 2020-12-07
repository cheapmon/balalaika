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
package com.github.cheapmon.balalaika.data.mappers

import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.model.DataCategory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Map from a [CategoryEntity] to a [DataCategory]
 */
@Singleton
internal class CategoryEntityToDataCategory @Inject constructor() :
    Mapper<CategoryEntity, DataCategory> {
    /** @suppress */
    override suspend fun invoke(from: CategoryEntity): DataCategory = DataCategory(
        id = from.id,
        name = from.name,
        iconName = from.iconName,
        sequence = from.sequence
    )
}
