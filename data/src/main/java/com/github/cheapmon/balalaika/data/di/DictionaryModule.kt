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
package com.github.cheapmon.balalaika.data.di

import com.github.cheapmon.balalaika.data.repositories.dictionary.DictionaryDataSource
import com.github.cheapmon.balalaika.data.repositories.dictionary.LocalDictionaryDataSource
import com.github.cheapmon.balalaika.data.repositories.dictionary.RemoteDictionaryDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

/** [Dictionary data source][DictionaryDataSource] dependency injection module */
@Module
@InstallIn(ApplicationComponent::class)
internal class DictionaryModule {
    /** @suppress */
    @Provides
    @Local
    fun provideLocalDataSource(source: LocalDictionaryDataSource): DictionaryDataSource =
        source

    /** @suppress */
    @Provides
    @Remote
    fun provideRemoteDataSource(source: RemoteDictionaryDataSource): DictionaryDataSource =
        source
}
