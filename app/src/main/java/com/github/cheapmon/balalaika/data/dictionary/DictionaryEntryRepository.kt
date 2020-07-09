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
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.entities.cache.CacheEntry
import com.github.cheapmon.balalaika.db.entities.cache.CacheEntryDao
import com.github.cheapmon.balalaika.db.entities.category.Category
import com.github.cheapmon.balalaika.db.entities.category.CategoryDao
import com.github.cheapmon.balalaika.db.entities.category.WidgetType
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
    private val cacheEntryDao: CacheEntryDao
) {
    /** Dummy [category][Category] used when no category has been selected in the user interface */
    private val defaultCategory = Category(
        constants.DEFAULT_CATEGORY_ID,
        "",
        "Default",
        WidgetType.PLAIN,
        R.drawable.ic_circle,
        -1,
        hidden = true,
        orderBy = false
    )

    private val _dictionaryView = ConflatedBroadcastChannel(constants.DEFAULT_DICTIONARY_VIEW_ID)
    private val _category = ConflatedBroadcastChannel(constants.DEFAULT_CATEGORY_ID)
    private val _initialKey = ConflatedBroadcastChannel<Long?>(null)
    private val dictionaryView = _dictionaryView.asFlow().distinctUntilChanged()
    private val category = _category.asFlow().distinctUntilChanged()
    private val initialKey = _initialKey.asFlow().distinctUntilChanged()

    /** All available [dictionary views][DictionaryView] */
    val dictionaryViews = viewDao.getAllWithCategories()

    /** All [lexemes][Lexeme] that are currently bookmarked */
    val bookmarks = lexemeDao.getBookmarks()

    /** All available sortable [categories][Category] */
    val categories = categoryDao.getSortable().map {
        listOf(defaultCategory) + it
    }

    /**
     * Current dictionary, depending on the user configuration
     *
     * Emits every time the view, category or initial key is changed.
     */
    val dictionary = combine(dictionaryView, category, initialKey) { d, c, i -> Triple(d, c, i) }
        .flatMapLatest { (d, c, i) -> getDictionary(d, c, i) }

    /** Set dictionary view */
    fun setDictionaryView(id: String) = _dictionaryView.offer(id)

    /** Set dictionary ordering */
    fun setCategory(id: String) = _category.offer(id)

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
}
