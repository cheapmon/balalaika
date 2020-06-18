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

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.cheapmon.balalaika.data.entities.cache.CacheEntry
import com.github.cheapmon.balalaika.data.entities.cache.CacheEntryDao
import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.category.CategoryDao
import com.github.cheapmon.balalaika.data.entities.category.WidgetType
import com.github.cheapmon.balalaika.data.entities.entry.DictionaryDao
import com.github.cheapmon.balalaika.data.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.data.entities.lexeme.LexemeDao
import com.github.cheapmon.balalaika.data.entities.property.PropertyDao
import com.github.cheapmon.balalaika.data.entities.view.DictionaryView
import com.github.cheapmon.balalaika.data.entities.view.DictionaryViewDao
import com.github.cheapmon.balalaika.ui.bookmarks.BookmarksFragment
import com.github.cheapmon.balalaika.ui.dictionary.DictionaryFragment
import com.github.cheapmon.balalaika.util.Constants
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Application-wide dictionary data handling
 *
 * @see DictionaryFragment
 * @see BookmarksFragment
 */
@ActivityScoped
@Suppress("EXPERIMENTAL_API_USAGE")
class DictionaryRepository @Inject constructor(
    private val constants: Constants,
    categoryDao: CategoryDao,
    private val lexemeDao: LexemeDao,
    private val propertyDao: PropertyDao,
    private val dictionaryDao: DictionaryDao,
    viewDao: DictionaryViewDao,
    private val cacheEntryDao: CacheEntryDao
) {
    /** Dummy [category][Category] used when no category has been selected in the user interface */
    private val defaultCategory = Category(
        -1,
        "default",
        "Default",
        WidgetType.PLAIN,
        "",
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
    fun setDictionaryView(id: Long) = _dictionaryView.offer(id)

    /** Set dictionary ordering */
    fun setCategory(id: Long) = _category.offer(id)

    /** Set the first entry to display */
    fun setInitialKey(id: Long?) = _initialKey.offer(id)

    private fun getDictionary(
        dictionaryViewId: Long,
        categoryId: Long,
        initialKey: Long?
    ): Flow<PagingData<DictionaryEntry>> {
        return flow {
            val pager = Pager(
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
            // Wait for refresh to finish
            refreshCache(dictionaryViewId, categoryId).collect { done ->
                if (done) pager.collect { value -> emit(value) }
            }
        }
    }

    private fun refreshCache(dictionaryViewId: Long, categoryId: Long): Flow<Boolean> {
        return flow {
            emit(false)
            val entries = if (categoryId == constants.DEFAULT_CATEGORY_ID) {
                dictionaryDao.getLexemes(dictionaryViewId)
            } else {
                dictionaryDao.getLexemes(dictionaryViewId, categoryId)
            }.mapIndexed { idx, id -> CacheEntry(idx + 1L, id) }
            cacheEntryDao.clearDictionaryCache()
            cacheEntryDao.insertIntoDictionaryCache(entries)
            emit(true)
        }
    }

    /** Get the position of a lexeme in the dictionary */
    suspend fun getIdOf(externalId: String?): Long? = externalId?.let {
        val lexemeId = lexemeDao.getLexemeIdByExternalId(externalId) ?: return@let null
        cacheEntryDao.findEntryInDictionaryCache(lexemeId)
    }

    /** Toggle bookmark state for a [lexeme][Lexeme] */
    suspend fun toggleBookmark(lexemeId: Long) {
        lexemeDao.toggleBookmark(lexemeId)
    }

    /** Remove all bookmarks */
    suspend fun clearBookmarks() {
        lexemeDao.clearBookmarks()
    }
}
