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
import androidx.room.Room
import com.github.cheapmon.balalaika.data.AppDatabase
import dagger.Module
import dagger.Provides

/**
 * Database dependency injection module
 *
 * This module inject the application database and all of its data access objects.
 */
@Module
class DatabaseModule {
    /** @suppress */
    @ActivityScope
    @Provides
    fun provideDatabase(context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "balalaika").build()

    /** @suppress */
    @ActivityScope
    @Provides
    fun provideCategories(appDatabase: AppDatabase) = appDatabase.categories()

    /** @suppress */
    @ActivityScope
    @Provides
    fun provideLexemes(appDatabase: AppDatabase) = appDatabase.lexemes()

    /** @suppress */
    @ActivityScope
    @Provides
    fun provideProperties(appDatabase: AppDatabase) = appDatabase.properties()

    /** @suppress */
    @ActivityScope
    @Provides
    fun provideDictionaryEntries(appDatabase: AppDatabase) = appDatabase.dictionaryEntries()

    /** @suppress */
    @ActivityScope
    @Provides
    fun provideDictionaryViews(appDatabase: AppDatabase) = appDatabase.dictionaryViews()

    /** @suppress */
    @ActivityScope
    @Provides
    fun provideHistoryEntries(appDatabase: AppDatabase) = appDatabase.historyEntries()
}