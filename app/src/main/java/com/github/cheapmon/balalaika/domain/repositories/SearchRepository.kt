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
package com.github.cheapmon.balalaika.domain.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.cheapmon.balalaika.db.entities.cache.CacheEntryDao
import com.github.cheapmon.balalaika.db.entities.cache.SearchCacheEntry
import com.github.cheapmon.balalaika.db.entities.entry.DictionaryDao
import com.github.cheapmon.balalaika.db.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.db.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.db.entities.lexeme.LexemeDao
import com.github.cheapmon.balalaika.db.entities.property.PropertyDao
import com.github.cheapmon.balalaika.domain.insert.ImportUtil
import com.github.cheapmon.balalaika.ui.search.SearchFragment
import com.github.cheapmon.balalaika.util.Constants
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest

/**
 * Database searching and data handling
 *
 * @see SearchFragment
 */
@ActivityScoped
@Suppress("EXPERIMENTAL_API_USAGE")
class SearchRepository @Inject constructor(
    private val constants: Constants,
    private val importUtil: ImportUtil,
    private val cacheEntryDao: CacheEntryDao,
    private val dictionaryDao: DictionaryDao,
    private val lexemeDao: LexemeDao,
    private val propertyDao: PropertyDao
) {
    private val _query = ConflatedBroadcastChannel("")
    private val _restriction = ConflatedBroadcastChannel<SearchRestriction>(SearchRestriction.None)

    /** Current search query */
    val query = _query.asFlow().distinctUntilChanged().debounce(300)

    /** Current search restriction */
    val restriction = _restriction.asFlow().distinctUntilChanged()

    /** Current dictionary, depending on the user configuration */
    val dictionary = query.combine(restriction) { query, restriction -> Pair(query, restriction) }
        .flatMapLatest { (query, restriction) -> getDictionary(query, restriction) }

    /** Set the search query */
    fun setQuery(query: String) = _query.offer(query)

    /** Set the search restriction */
    fun setRestriction(restriction: SearchRestriction) = _restriction.offer(restriction)

    private suspend fun getDictionary(
        query: String,
        searchRestriction: SearchRestriction
    ): Flow<PagingData<DictionaryEntry>> {
        importUtil.import()
        refreshCache(query, searchRestriction)
        return Pager(
            config = PagingConfig(pageSize = constants.PAGE_SIZE),
            pagingSourceFactory = {
                SearchPagingSource(
                    constants,
                    cacheEntryDao,
                    lexemeDao,
                    propertyDao
                )
            }
        ).flow
    }

    private suspend fun refreshCache(
        query: String,
        searchRestriction: SearchRestriction
    ) {
        val entries = when (searchRestriction) {
            is SearchRestriction.None ->
                dictionaryDao.findLexemes(query)
            is SearchRestriction.Some ->
                dictionaryDao.findLexemes(
                    query,
                    searchRestriction.category.categoryId,
                    searchRestriction.restriction
                )
        }.mapIndexed { idx, id -> SearchCacheEntry(idx + 1L, id) }
        cacheEntryDao.clearSearchCache()
        cacheEntryDao.insertIntoSearchCache(entries)
    }
}
