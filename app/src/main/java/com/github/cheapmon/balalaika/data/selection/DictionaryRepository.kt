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
import arrow.core.Either
import com.github.cheapmon.balalaika.db.AppDatabase
import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.db.entities.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.util.logger
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@ActivityScoped
class DictionaryRepository @Inject constructor(
    private val db: AppDatabase,
    private val dictionaryDao: DictionaryDao,
    private val mediator: DictionaryMediator
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO
    private var refreshJob: Job? = null

    init {
        refresh()
    }

    val dictionaries = dictionaryDao.getAll()

    fun getDictionary(id: String) = dictionaryDao.findById(id)

    private val _inProgress = MutableStateFlow(false)
    val inProgress: StateFlow<Boolean>
        get() = _inProgress

    fun toggleActive(dictionary: Dictionary) {
        launch {
            if (dictionary.isActive) {
                dictionaryDao.setInactive(dictionary.id)
            } else {
                dictionaryDao.setActive(dictionary.id)
            }
        }
    }

    fun addDictionary(dictionary: Dictionary) {
        launch {
            showProgess {
                dictionaryDao.setInstalled(dictionary.id)
                removeEntities(dictionary.id)
                val result = mediator.installDictionary(dictionary).attempt().suspended()
                if (result is Either.Left) logger { error(result.a) }
            }
        }
    }

    fun removeDictionary(id: String) {
        launch {
            showProgess {
                removeEntities(id)
                db.dictionaries().remove(id)
                refresh()
            }
        }
    }

    fun refresh() {
        refreshJob?.cancel()
        refreshJob = launch { mediator.updateDictionaryList() }
    }

    private suspend fun showProgess(block: suspend () -> Unit) {
        _inProgress.value = true
        block()
        _inProgress.value = false
    }

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
}
