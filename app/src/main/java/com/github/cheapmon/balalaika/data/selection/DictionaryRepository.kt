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

import com.github.cheapmon.balalaika.db.entities.dictionary.Dictionary
import com.github.cheapmon.balalaika.db.entities.dictionary.DictionaryDao
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
    private val dictionaryDao: DictionaryDao,
    private val mediator: DictionaryMediator
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO
    private var refreshJob: Job? = null

    init {
        refresh()
    }

    val dictionaries = dictionaryDao.getAll()

    fun getDictionary(externalId: String) = dictionaryDao.getByExternalId(externalId)

    private val _inProgress = MutableStateFlow(false)
    val inProgress: StateFlow<Boolean>
        get() = _inProgress

    fun toggleActive(dictionary: Dictionary) {
        launch {
            if (dictionary.isActive) {
                dictionaryDao.setInactive(dictionary.externalId)
            } else {
                dictionaryDao.setActive(dictionary.externalId)
            }
        }
    }

    fun addDictionary(dictionary: Dictionary) {
        launch {
            showProgess {
                dictionaryDao.setInstalled(dictionary.externalId)
                mediator.installDictionary(dictionary).attempt().suspended()
            }
        }
    }

    fun removeDictionary(externalId: String) {
        launch {
            showProgess {
                dictionaryDao.setInactive(externalId)
                dictionaryDao.setUninstalled(externalId)
                dictionaryDao.setUnupdatable(externalId)
                // TODO: Remove everything else
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
}
