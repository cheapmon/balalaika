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
package com.github.cheapmon.balalaika.db.entities.category

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** Database link for [data categories][Category] */
@Dao
interface CategoryDao {
    /** Get all data categories that can be used to order dictionary entries */
    @Query("SELECT * FROM category WHERE order_by = 1")
    fun getSortable(): Flow<List<Category>>

    /** Insert all data categories into the database */
    @Insert
    suspend fun insertAll(categories: List<Category>)
}
