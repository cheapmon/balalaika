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
package com.github.cheapmon.balalaika.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.category.CategoryDao
import com.github.cheapmon.balalaika.data.entities.category.WidgetTypeConverters
import com.github.cheapmon.balalaika.data.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.data.entities.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.data.entities.history.HistoryEntry
import com.github.cheapmon.balalaika.data.entities.history.HistoryEntryDao
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.data.entities.lexeme.LexemeDao
import com.github.cheapmon.balalaika.data.entities.property.Property
import com.github.cheapmon.balalaika.data.entities.property.PropertyDao
import com.github.cheapmon.balalaika.data.entities.view.DictionaryView
import com.github.cheapmon.balalaika.data.entities.view.DictionaryViewDao
import com.github.cheapmon.balalaika.data.entities.view.DictionaryViewToCategory

/**
 * Application-wide database
 *
 * For more information about the application architecture, please refer to the
 * [Architecture documentation][com.github.cheapmon.balalaika.data].
 */
@Database(
    entities = [
        Category::class,
        Lexeme::class,
        Property::class,
        DictionaryView::class,
        DictionaryViewToCategory::class,
        HistoryEntry::class
    ],
    views = [DictionaryEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(WidgetTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    /** Database link for [categories][Category] */
    abstract fun categories(): CategoryDao

    /** Database link for [lexemes][Lexeme] */
    abstract fun lexemes(): LexemeDao

    /** Database link for [properties][Property] */
    abstract fun properties(): PropertyDao

    /** Database link for [dictionary entries][DictionaryEntry] */
    abstract fun dictionaryEntries(): DictionaryEntryDao

    /** Database link for [dictionary views][DictionaryView] */
    abstract fun dictionaryViews(): DictionaryViewDao

    /** Database link for [history entries][HistoryEntry] */
    abstract fun historyEntries(): HistoryEntryDao
}
