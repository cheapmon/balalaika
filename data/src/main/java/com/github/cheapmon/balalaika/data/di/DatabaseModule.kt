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

import android.content.Context
import androidx.room.Room
import com.github.cheapmon.balalaika.data.db.AppDatabase
import com.github.cheapmon.balalaika.data.db.CacheDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext

/**
 * Database dependency injection module
 *
 * This module inject the application database and all of its data access objects.
 */
@Module
@InstallIn(ApplicationContext::class)
internal class DatabaseModule {
    /** @suppress */
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "balalaika").build()

    /** @suppress */
    @Provides
    fun provideDictionaries(appDatabase: AppDatabase) = appDatabase.dictionaries()

    /** @suppress */
    @Provides
    fun provideCategories(appDatabase: AppDatabase) = appDatabase.categories()

    /** @suppress */
    @Provides
    fun provideLexemes(appDatabase: AppDatabase) = appDatabase.lexemes()

    /** @suppress */
    @Provides
    fun provideProperties(appDatabase: AppDatabase) = appDatabase.properties()

    /** @suppress */
    @Provides
    fun provideDictionaryEntries(appDatabase: AppDatabase) = appDatabase.dictionaryEntries()

    /** @suppress */
    @Provides
    fun provideDictionaryViews(appDatabase: AppDatabase) = appDatabase.dictionaryViews()

    /** @suppress */
    @Provides
    fun provideHistoryEntries(appDatabase: AppDatabase) = appDatabase.historyEntries()

    /** @suppress */
    @Provides
    fun provideConfigurations(appDatabase: AppDatabase) = appDatabase.configurations()

    /** @suppress */
    @Provides
    fun provideCacheDatabase(@ApplicationContext context: Context) =
        Room.inMemoryDatabaseBuilder(context, CacheDatabase::class.java).build()

    /** @suppress */
    @Provides
    fun provideCacheEntries(cacheDatabase: CacheDatabase) = cacheDatabase.cacheEntries()
}
