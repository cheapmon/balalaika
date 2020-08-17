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
package com.github.cheapmon.balalaika.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.cheapmon.balalaika.data.db.category.Category
import com.github.cheapmon.balalaika.data.db.category.CategoryDao
import com.github.cheapmon.balalaika.data.db.category.WidgetTypeConverters
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfig
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfigDao
import com.github.cheapmon.balalaika.data.db.dictionary.Dictionary
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.data.db.entry.PropertyDatabaseView
import com.github.cheapmon.balalaika.data.db.history.HistoryEntry
import com.github.cheapmon.balalaika.data.db.history.HistoryEntryDao
import com.github.cheapmon.balalaika.data.db.lexeme.Lexeme
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeDao
import com.github.cheapmon.balalaika.data.db.property.Property
import com.github.cheapmon.balalaika.data.db.property.PropertyDao
import com.github.cheapmon.balalaika.data.db.view.DictionaryView
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewDao
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewToCategory

/** Application-wide database */
@Database(
    entities = [
        Dictionary::class,
        Category::class,
        Lexeme::class,
        Property::class,
        DictionaryView::class,
        DictionaryViewToCategory::class,
        HistoryEntry::class,
        DictionaryConfig::class
    ],
    views = [PropertyDatabaseView::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(WidgetTypeConverters::class)
internal abstract class AppDatabase : RoomDatabase() {
    /** Database link for [dictionaries][Dictionary] */
    abstract fun dictionaries(): DictionaryDao

    /** Database link for [categories][Category] */
    abstract fun categories(): CategoryDao

    /** Database link for [lexemes][Lexeme] */
    abstract fun lexemes(): LexemeDao

    /** Database link for [properties][Property] */
    abstract fun properties(): PropertyDao

    /** Database link for [dictionary entries][PropertyDatabaseView] */
    abstract fun dictionaryEntries(): DictionaryEntryDao

    /** Database link for [dictionary views][DictionaryView] */
    abstract fun dictionaryViews(): DictionaryViewDao

    /** Database link for [history entries][HistoryEntry] */
    abstract fun historyEntries(): HistoryEntryDao

    /** Database link for [dictionary configurations][DictionaryConfig] */
    abstract fun configurations(): DictionaryConfigDao
}
