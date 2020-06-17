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
package com.github.cheapmon.balalaika.data.repositories

import androidx.paging.PagingSource
import com.github.cheapmon.balalaika.data.entities.cache.CacheEntryDao
import com.github.cheapmon.balalaika.data.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.data.entities.lexeme.LexemeDao
import com.github.cheapmon.balalaika.data.entities.property.PropertyDao
import com.github.cheapmon.balalaika.ui.search.SearchFragment
import com.github.cheapmon.balalaika.util.Constants

/**
 * Paging source for the dictionary search interface
 *
 * On every configuration change, the whole dictionary is filtered and the results
 * are cached. Using this indirection, we enable paging for dictionary entries, only loading
 * small portions of the dictionary at the same time.
 *
 * @see SearchFragment
 */
class SearchPagingSource(
    private val constants: Constants,
    private val cacheEntryDao: CacheEntryDao,
    private val lexemeDao: LexemeDao,
    private val propertyDao: PropertyDao
) : PagingSource<Long, DictionaryEntry>() {
    private val startId = constants.PAGING_START_INDEX

    /** Load next page */
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, DictionaryEntry> {
        val pos = params.key ?: startId
        val data = cacheEntryDao.getFromSearchCache(pos, params.loadSize).mapNotNull { id ->
            val lexeme = lexemeDao.getLexemeById(id) ?: return@mapNotNull null
            val properties = propertyDao.getProperties(id, constants.DEFAULT_DICTIONARY_VIEW_ID)
            DictionaryEntry(lexeme, properties)
        }
        return LoadResult.Page(
            data,
            prevKey = if (pos == startId) null else maxOf(startId, pos - params.loadSize),
            nextKey = if (data.isEmpty()) null else pos + params.loadSize
        )
    }
}