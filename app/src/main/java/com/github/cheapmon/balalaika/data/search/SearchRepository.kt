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
package com.github.cheapmon.balalaika.data.search

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.db.entities.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.db.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.db.entities.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.db.entities.history.SearchRestriction
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
import kotlinx.coroutines.flow.first
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
    private val dictionaryDao: DictionaryDao,
    private val dictionaryEntryDao: DictionaryEntryDao
) {
    private val _query = ConflatedBroadcastChannel("")
    private val _restriction = ConflatedBroadcastChannel<SearchRestriction>(SearchRestriction.None)

    /** Current search query */
    val query = _query.asFlow().distinctUntilChanged().debounce(300)

    /** Current search restriction */
    val restriction = _restriction.asFlow().distinctUntilChanged()

    /** Current dictionary, depending on the user configuration */
    val dictionary = query.combine(restriction) { q, r -> Pair(q, r) }
        .flatMapLatest { (q, r) -> getDictionary(q, r) }

    /** Set the search query */
    fun setQuery(query: String) = _query.offer(query)

    /** Set the search restriction */
    fun setRestriction(restriction: SearchRestriction) = _restriction.offer(restriction)

    private fun getDictionary(
        query: String,
        searchRestriction: SearchRestriction
    ): Flow<PagingData<DictionaryEntry>> {
        return Pager(
            config = PagingConfig(pageSize = constants.PAGE_SIZE),
            pagingSourceFactory = {
                when (searchRestriction) {
                    is SearchRestriction.Some -> dictionaryEntryDao.findLexemes(
                        query,
                        searchRestriction.category.id,
                        searchRestriction.restriction
                    )
                    is SearchRestriction.None -> dictionaryEntryDao.findLexemes(
                        query
                    )
                }
            }
        ).flow
    }

    /** Get the currently active dictionary */
    suspend fun getActiveDictionary(): Dictionary? = dictionaryDao.getActive().first()
}
