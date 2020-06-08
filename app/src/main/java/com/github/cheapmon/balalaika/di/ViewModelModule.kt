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
package com.github.cheapmon.balalaika.di

import android.content.Context
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.repositories.DictionaryRepository
import com.github.cheapmon.balalaika.data.storage.Storage
import com.github.cheapmon.balalaika.ui.dictionary.DictionaryViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * View model dependency injection module
 *
 * This module injects view models and view model factories.
 */
@Module
class ViewModelModule {
    /** @suppress */
    @Provides
    fun provideDictionaryViewModelFactory(
        context: Context,
        storage: Storage,
        repository: DictionaryRepository
    ): DictionaryViewModelFactory {
        val categoryId =
            storage.getString(context.getString(R.string.preferences_key_order), null)?.toLong()
        val dictionaryViewId =
            storage.getString(context.getString(R.string.preferences_key_view), null)?.toLong()
        return DictionaryViewModelFactory(repository, categoryId, dictionaryViewId)
    }
}