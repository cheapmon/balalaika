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

import com.github.cheapmon.balalaika.data.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.data.entities.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.data.entities.history.SearchRestriction
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.di.ActivityScope
import com.github.cheapmon.balalaika.ui.search.SearchFragment
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * Database searching and data handling
 *
 * @see SearchFragment
 */
@ActivityScope
@Suppress("EXPERIMENTAL_API_USAGE")
class SearchRepository @Inject constructor(
    private val dictionaryEntryDao: DictionaryEntryDao
) {
    /** Current query set by user */
    private val queryChannel: ConflatedBroadcastChannel<String> = ConflatedBroadcastChannel()

    /** Current [search restriction][SearchRestriction] set by user */
    private val restrictionChannel: ConflatedBroadcastChannel<SearchRestriction> =
        ConflatedBroadcastChannel()

    /**
     * Current state of computation
     *
     * Holds `true` while the database is queried
     */
    private val inProgressChannel: ConflatedBroadcastChannel<Boolean> =
        ConflatedBroadcastChannel(false)

    init {
        // Search unrestricted by default
        restrictionChannel.offer(SearchRestriction.None)
    }

    /** [Lexemes][Lexeme] matching the user's query */
    val lexemes = queryChannel.asFlow().distinctUntilChanged().debounce(300)
        .combine(restrictionChannel.asFlow().distinctUntilChanged()) { q, r ->
            when (r) {
                is SearchRestriction.None ->
                    dictionaryEntryDao.findLexemes(q)
                is SearchRestriction.Some ->
                    dictionaryEntryDao.findLexemesWith(q, r.category.categoryId, r.restriction)
            }
        }

    /** Current query */
    val query = queryChannel.asFlow()

    /** Current [search restriction][SearchRestriction] */
    val restriction = restrictionChannel.asFlow()

    /** Current state of computation */
    val inProgress = inProgressChannel.asFlow()

    /** Set query */
    fun setQuery(query: String) {
        queryChannel.offer(query)
    }

    /** Set [search restriction][SearchRestriction] */
    fun setRestriction(restriction: SearchRestriction) {
        restrictionChannel.offer(restriction)
    }

    /** Get all [dictionary entries][DictionaryEntry] associated with a [lexeme][Lexeme] */
    suspend fun getDictionaryEntriesFor(lexemeId: Long): List<DictionaryEntry> {
        inProgressChannel.offer(true)
        val q = queryChannel.asFlow().first()
        val r = restrictionChannel.asFlow().first()
        val result = when (r) {
            is SearchRestriction.None ->
                dictionaryEntryDao.find(q, lexemeId)
            is SearchRestriction.Some ->
                dictionaryEntryDao.findWith(q, r.category.categoryId, r.restriction, lexemeId)
        }
        inProgressChannel.offer(false)
        return result
    }

    /** Remove [search restriction][SearchRestriction] */
    fun clearRestriction() {
        restrictionChannel.offer(SearchRestriction.None)
    }
}