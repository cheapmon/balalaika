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
package com.github.cheapmon.balalaika.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.cheapmon.balalaika.db.entities.cache.CacheEntry
import com.github.cheapmon.balalaika.db.entities.cache.CacheEntryDao

/**
 * Application-wide in-memory database
 *
 * For more information about the application architecture, please refer to the
 * [Architecture documentation][com.github.cheapmon.balalaika.db].
 */
@Database(
    entities = [
        CacheEntry::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CacheDatabase : RoomDatabase() {
    /** Database link for cache entries */
    abstract fun cacheEntries(): CacheEntryDao
}
