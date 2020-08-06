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
package com.github.cheapmon.balalaika.data.selection

import androidx.room.withTransaction
import com.github.cheapmon.balalaika.data.LoadState
import com.github.cheapmon.balalaika.data.tryLoad
import com.github.cheapmon.balalaika.db.AppDatabase
import com.github.cheapmon.balalaika.db.entities.config.DictionaryConfig
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.db.entities.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.ui.selection.SelectionFragment
import com.github.cheapmon.balalaika.util.Constants
import dagger.hilt.android.scopes.ActivityScoped
import java.net.ConnectException
import javax.inject.Inject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Dictionary data handling
 *
 * @see SelectionFragment
 */
@ActivityScoped
class DictionaryRepository @Inject constructor(
    private val constants: Constants,
    private val db: AppDatabase,
    private val dictionaryDao: DictionaryDao,
    private val mediator: DictionaryMediator
) {
    /** All available dictionaries */
    val dictionaries = dictionaryDao.getAll()

    /** Currently active dictionary */
    val currentDictionary = dictionaryDao.getActive()

    /** Get a single dictionary from the database */
    fun getDictionary(id: String) = dictionaryDao.findById(id)

    /** (De)activate a dictionary */
    fun toggleActive(dictionary: Dictionary): Flow<LoadState<Unit, DictionaryError>> = tryLoad {
        if (dictionary.isActive) {
            dictionaryDao.setInactive(dictionary.id)
        } else {
            dictionaryDao.setActive(dictionary.id)
        }
    }.map { it.mapError(::toDictionaryError) }

    /** Install a dictionary */
    fun addDictionary(dictionary: Dictionary): Flow<LoadState<Unit, DictionaryError>> = tryLoad {
        removeEntities(dictionary.id)
        mediator.installDictionary(dictionary)
        dictionaryDao.setInstalled(dictionary.id)
    }.map { it.mapError(::toDictionaryError) }

    /** Remove a dictionary */
    fun removeDictionary(id: String): Flow<LoadState<Unit, DictionaryError>> = tryLoad {
        removeEntities(id)
        db.dictionaries().remove(id)
        refresh().collect()
    }.map { it.mapError(::toDictionaryError) }

    /**
     * Update a dictionary and its contents in the database
     *
     * At the moment we use a very simple approach to update the database: Existing entities are
     * removed and new entities imported. Additionally, three things need to be fixed:
     * - Bookmarks
     * - Search history
     * - Dictionary configuration
     *
     * Bookmarks are implicitly handled because they are saved directly on the lexeme. The search
     * history cascades any changes done to the table. The dictionary configuration is replaced
     * by defaults if the assigned category or dictionary view is missing.
     */
    fun updateDictionary(dictionary: Dictionary): Flow<LoadState<Unit, DictionaryError>> = tryLoad {
        val configuration = db.configurations().getConfigFor(dictionary.id).first()
        db.withTransaction {
            db.configurations().removeConfigFor(dictionary.id)
            db.dictionaryViews().removeRelations(dictionary.id)
            db.dictionaryViews().removeViews(dictionary.id)
            db.properties().removeInDictionary(dictionary.id)
            db.lexemes().removeInDictionary(dictionary.id)
            db.categories().removeInDictionary(dictionary.id)
        }
        mediator.installDictionary(dictionary)
        // Ensure that configuration uses correct foreign keys
        var orderBy = configuration?.orderBy
        if (orderBy == null || db.categories().findById(orderBy) == null) {
            orderBy = constants.DEFAULT_CATEGORY_ID
        }
        var filterBy = configuration?.filterBy
        if (filterBy == null || db.dictionaryViews().findById(filterBy) == null) {
            filterBy = constants.DEFAULT_DICTIONARY_VIEW_ID
        }
        db.configurations().insert(DictionaryConfig(dictionary.id, orderBy, filterBy))
        db.dictionaries().setUnupdatable(dictionary.id)
    }.map { it.mapError(::toDictionaryError) }

    /** Update the dictionary list in the database */
    fun refresh(): Flow<LoadState<Unit, DictionaryError>> = tryLoad {
        mediator.updateDictionaryList()
    }.map { it.mapError(::toDictionaryError) }

    private suspend fun removeEntities(dictionaryId: String) {
        db.withTransaction {
            db.historyEntries().removeInDictionary(dictionaryId)
            db.configurations().removeConfigFor(dictionaryId)
            db.dictionaryViews().removeRelations(dictionaryId)
            db.dictionaryViews().removeViews(dictionaryId)
            db.properties().removeInDictionary(dictionaryId)
            db.lexemes().removeInDictionary(dictionaryId)
            db.categories().removeInDictionary(dictionaryId)
        }
    }

    private fun toDictionaryError(cause: Throwable?): DictionaryError {
        return when (cause) {
            is TimeoutCancellationException -> DictionaryError.Timeout
            is ConnectException -> DictionaryError.Network
            else -> DictionaryError.Internal
        }
    }
}
