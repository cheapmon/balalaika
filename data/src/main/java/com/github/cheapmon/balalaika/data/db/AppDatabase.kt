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
import com.github.cheapmon.balalaika.data.db.bookmark.BookmarkDao
import com.github.cheapmon.balalaika.data.db.bookmark.BookmarkEntity
import com.github.cheapmon.balalaika.data.db.category.CategoryDao
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.category.WidgetTypeConverters
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfig
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfigDao
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryDao
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import com.github.cheapmon.balalaika.data.db.entry.DictionaryEntryDao
import com.github.cheapmon.balalaika.data.db.entry.PropertyDatabaseView
import com.github.cheapmon.balalaika.data.db.history.HistoryItemDao
import com.github.cheapmon.balalaika.data.db.history.HistoryItemEntity
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeDao
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeEntity
import com.github.cheapmon.balalaika.data.db.property.PropertyDao
import com.github.cheapmon.balalaika.data.db.property.PropertyEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewDao
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewToCategory

/** Application-wide database */
@Database(
    entities = [
        BookmarkEntity::class,
        DictionaryEntity::class,
        CategoryEntity::class,
        LexemeEntity::class,
        PropertyEntity::class,
        DictionaryViewEntity::class,
        DictionaryViewToCategory::class,
        HistoryItemEntity::class,
        DictionaryConfig::class
    ],
    views = [PropertyDatabaseView::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(WidgetTypeConverters::class)
internal abstract class AppDatabase : RoomDatabase() {
    /** Database link for [dictionaries][DictionaryEntity] */
    abstract fun dictionaries(): DictionaryDao

    /** Database link for [categories][CategoryEntity] */
    abstract fun categories(): CategoryDao

    /** Database link for [lexemes][LexemeEntity] */
    abstract fun lexemes(): LexemeDao

    /** Database link for [bookmarks][BookmarkEntity] */
    abstract fun bookmarks(): BookmarkDao

    /** Database link for [properties][PropertyEntity] */
    abstract fun properties(): PropertyDao

    /** Database link for [dictionary entries][PropertyDatabaseView] */
    abstract fun dictionaryEntries(): DictionaryEntryDao

    /** Database link for [dictionary views][DictionaryViewEntity] */
    abstract fun dictionaryViews(): DictionaryViewDao

    /** Database link for [history entries][HistoryItemEntity] */
    abstract fun historyEntries(): HistoryItemDao

    /** Database link for [dictionary configurations][DictionaryConfig] */
    abstract fun configurations(): DictionaryConfigDao
}
