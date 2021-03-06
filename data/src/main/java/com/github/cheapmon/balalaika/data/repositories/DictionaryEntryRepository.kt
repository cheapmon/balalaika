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

import androidx.paging.*
import com.github.cheapmon.balalaika.data.db.cache.CacheEntry
import com.github.cheapmon.balalaika.data.db.cache.CacheEntryDao
import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewDao
import com.github.cheapmon.balalaika.data.mappers.DictionaryEntryEntityToDictionaryEntryMapper
import com.github.cheapmon.balalaika.data.util.Constants
import com.github.cheapmon.balalaika.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class DictionaryEntryRepository @Inject internal constructor(
    private val dictionaryEntryDao: DictionaryEntryDao,
    private val cacheEntryDao: CacheEntryDao,
    private val dictionaryViewDao: DictionaryViewDao,
    private val toDictionaryEntry: DictionaryEntryEntityToDictionaryEntryMapper
) {
    public suspend fun getDictionaryEntries(
        dictionary: InstalledDictionary,
        dictionaryView: DictionaryView,
        category: DataCategory,
        initialEntry: DictionaryEntry?
    ): Flow<PagingData<DictionaryEntry>> {
        refreshCache(dictionary, dictionaryView, category)
        val initialKey = initialEntry?.id?.let { cacheEntryDao.findEntry(it) }
        return Pager(
            config = PagingConfig(pageSize = Constants.PAGE_SIZE),
            initialKey = initialKey,
            pagingSourceFactory = { DictionaryEntryPagingSource(dictionary, dictionaryView) }
        ).flow
    }

    public suspend fun queryDictionaryEntries(
        dictionary: InstalledDictionary,
        query: String,
        searchRestriction: SearchRestriction? = null
    ): Flow<PagingData<DictionaryEntry>> {
        val view = dictionaryViewDao.findById(dictionary.id, Constants.DEFAULT_DICTIONARY_VIEW_ID)
            ?: throw IllegalArgumentException("Dictionary view does not exist")
        return Pager(
            config = PagingConfig(pageSize = Constants.PAGE_SIZE),
            pagingSourceFactory = {
                if (searchRestriction == null) {
                    dictionaryEntryDao.findLexemes(dictionary.id, query)
                } else {
                    dictionaryEntryDao.findLexemes(
                        dictionary.id,
                        query,
                        searchRestriction.category.id,
                        searchRestriction.text
                    )
                }
            }
        ).flow.map { pagingData -> pagingData.map { toDictionaryEntry(it, view) } }
    }

    private suspend fun refreshCache(
        dictionary: InstalledDictionary,
        dictionaryView: DictionaryView,
        category: DataCategory
    ) {
        val entries = if (category.id == Constants.DEFAULT_CATEGORY_ID) {
            dictionaryEntryDao.getLexemes(dictionary.id, dictionaryView.id)
        } else {
            dictionaryEntryDao.getLexemes(dictionary.id, dictionaryView.id, category.id)
        }.mapIndexed { idx, id -> CacheEntry(idx + 1L, id) }
        cacheEntryDao.clear()
        cacheEntryDao.insertAll(entries)
    }

    private inner class DictionaryEntryPagingSource(
        private val dictionary: InstalledDictionary,
        private val dictionaryView: DictionaryView
    ) : PagingSource<Long, DictionaryEntry>() {
        private val startId = Constants.PAGING_START_INDEX

        override suspend fun load(params: LoadParams<Long>): LoadResult<Long, DictionaryEntry> {
            val pos = params.key ?: startId
            val view = dictionaryViewDao.findById(dictionary.id, dictionaryView.id)
                ?: throw IllegalArgumentException("Dictionary view does not exist")
            val data = cacheEntryDao.getPage(params.loadSize, pos)
                .mapNotNull { id -> dictionaryEntryDao.findEntryById(dictionary.id, id) }
                .map { toDictionaryEntry(it, view) }
            return LoadResult.Page(
                data,
                prevKey = if (pos == startId) null else maxOf(startId, pos - params.loadSize),
                nextKey = if (data.isEmpty()) null else pos + params.loadSize
            )
        }
    }
}
