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
package com.github.cheapmon.balalaika.data.dictionary

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.cheapmon.balalaika.data.dictionary.wordnet.WordnetApi
import com.github.cheapmon.balalaika.data.dictionary.wordnet.WordnetInfoMapper
import com.github.cheapmon.balalaika.data.tryLoad
import com.github.cheapmon.balalaika.db.entities.cache.CacheEntry
import com.github.cheapmon.balalaika.db.entities.cache.CacheEntryDao
import com.github.cheapmon.balalaika.db.entities.category.Category
import com.github.cheapmon.balalaika.db.entities.category.CategoryDao
import com.github.cheapmon.balalaika.db.entities.config.DictionaryConfigDao
import com.github.cheapmon.balalaika.db.entities.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.db.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.db.entities.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.db.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.db.entities.lexeme.LexemeDao
import com.github.cheapmon.balalaika.db.entities.property.PropertyDao
import com.github.cheapmon.balalaika.db.entities.view.DictionaryView
import com.github.cheapmon.balalaika.db.entities.view.DictionaryViewDao
import com.github.cheapmon.balalaika.ui.bookmarks.BookmarksFragment
import com.github.cheapmon.balalaika.ui.dictionary.DictionaryFragment
import com.github.cheapmon.balalaika.util.Constants
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

/**
 * Application-wide dictionary data handling
 *
 * @see DictionaryFragment
 * @see BookmarksFragment
 */
@ActivityScoped
@Suppress("EXPERIMENTAL_API_USAGE")
class DictionaryEntryRepository @Inject constructor(
    private val constants: Constants,
    categoryDao: CategoryDao,
    private val lexemeDao: LexemeDao,
    private val propertyDao: PropertyDao,
    private val dictionaryEntryDao: DictionaryEntryDao,
    viewDao: DictionaryViewDao,
    dictionaryDao: DictionaryDao,
    private val configDao: DictionaryConfigDao,
    private val cacheEntryDao: CacheEntryDao,
    private val wordnetApi: WordnetApi,
    private val mapper: WordnetInfoMapper
) {
    private val config = dictionaryDao.getActive()
        .filterNotNull()
        .flatMapLatest { configDao.getConfigFor(it.id) }

    /** Current selected dictionary view */
    val dictionaryView = config.filterNotNull().map { it.filterBy }

    /** Current selected category to order by */
    val category = config.filterNotNull().map { it.orderBy }

    private val _initialKey = ConflatedBroadcastChannel<Long?>(null)
    private val initialKey = _initialKey.asFlow().distinctUntilChanged()

    /** All available [dictionary views][DictionaryView] */
    val dictionaryViews = viewDao.getAllWithCategories()

    /** All [lexemes][Lexeme] that are currently bookmarked */
    val bookmarks = lexemeDao.getBookmarks()

    /** All available sortable [categories][Category] */
    val categories = categoryDao.getSortable()

    /**
     * Current dictionary, depending on the user configuration
     *
     * Emits every time the view, category or initial key is changed.
     */
    val dictionary = combine(dictionaryView, category, initialKey) { d, c, i -> Triple(d, c, i) }
        .flatMapLatest { (d, c, i) -> getDictionary(d, c, i) }

    /** Current active dictionary */
    val currentDictionary = dictionaryDao.getActive()

    /** Set dictionary view */
    suspend fun setDictionaryView(id: String) {
        val config = config.first() ?: return
        val result = config.copy(filterBy = id)
        configDao.update(result)
    }

    /** Set dictionary ordering */
    suspend fun setCategory(id: String) {
        val config = config.first() ?: return
        val result = config.copy(orderBy = id)
        configDao.update(result)
    }

    /** Set the first entry to display */
    fun setInitialKey(id: Long?) = _initialKey.offer(id)

    private suspend fun getDictionary(
        dictionaryViewId: String,
        categoryId: String,
        initialKey: Long?
    ): Flow<PagingData<DictionaryEntry>> {
        refreshCache(dictionaryViewId, categoryId)
        return Pager(
            config = PagingConfig(pageSize = constants.PAGE_SIZE),
            initialKey = initialKey,
            pagingSourceFactory = {
                DictionaryPagingSource(
                    constants,
                    cacheEntryDao,
                    lexemeDao,
                    propertyDao,
                    dictionaryViewId
                )
            }
        ).flow
    }

    private suspend fun refreshCache(dictionaryViewId: String, categoryId: String) {
        val entries = if (categoryId == constants.DEFAULT_CATEGORY_ID) {
            dictionaryEntryDao.getLexemes(dictionaryViewId)
        } else {
            dictionaryEntryDao.getLexemes(dictionaryViewId, categoryId)
        }.mapIndexed { idx, id -> CacheEntry(idx + 1L, id) }
        cacheEntryDao.clearDictionaryCache()
        cacheEntryDao.insertIntoDictionaryCache(entries)
    }

    /** Get the position of a lexeme in the dictionary */
    suspend fun getIdOf(id: String?): Long? = id?.let {
        cacheEntryDao.findEntryInDictionaryCache(id)
    }

    /** Toggle bookmark state for a [lexeme][Lexeme] */
    suspend fun toggleBookmark(lexemeId: String) {
        lexemeDao.toggleBookmark(lexemeId)
    }

    /** Remove all bookmarks */
    suspend fun clearBookmarks() {
        lexemeDao.clearBookmarks()
    }

    /** Load Wordnet information for a word */
    fun getWordnetData(url: String) = tryLoad { wordnetApi.getWordnetData(url) }
        .map { loadState -> loadState.map { node -> mapper.map(node) } }
}
