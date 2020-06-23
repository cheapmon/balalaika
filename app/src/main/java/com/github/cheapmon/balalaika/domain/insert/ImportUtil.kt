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
package com.github.cheapmon.balalaika.domain.insert

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.room.withTransaction
import com.github.cheapmon.balalaika.db.AppDatabase
import com.github.cheapmon.balalaika.domain.config.ConfigLoader
import com.github.cheapmon.balalaika.domain.storage.Storage
import com.github.cheapmon.balalaika.util.Constants
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

/**
 * Asynchronous import of entities into the [app database][AppDatabase]
 *
 * The configuration of input files is checked for a version number. If the version is newer than
 * the one we currently use, a transaction is started and new data is imported from the sources.
 * If there are any problems with the sources, rollback is used. After successful import, the new
 * version number is saved.
 */
@ActivityScoped
class ImportUtil @Inject constructor(
    /** Extracts entities from sources */
    private val entityImporter: EntityImporter,
    /** Reads configuration from sources */
    private val configLoader: ConfigLoader,
    /** Reads and writes version number to persistent storage */
    private val storage: Storage,
    /** Application wide constants */
    private val constants: Constants,
    /** Database to write to */
    private val appDatabase: AppDatabase
) {
    /** Import entities from sources */
    suspend fun import() {
        val config = configLoader.readConfig()
        val currentVersion = storage.getInt(constants.DB_VERSION_KEY, 0)
        if (config.version > currentVersion) {
            try {
                appDatabase.withTransaction {
                    appDatabase.clearAllTables()
                    appDatabase.categories()
                        .insertAll(*entityImporter.readCategories().toTypedArray())
                    appDatabase.lexemes()
                        .insertAll(*entityImporter.readLexemes().toTypedArray())
                    appDatabase.properties()
                        .insertAll(*entityImporter.readProperties().toTypedArray())
                    appDatabase.dictionaryViews()
                        .insertAll(*entityImporter.readDictionaryViews().toTypedArray())
                    appDatabase.dictionaryViews()
                        .insertAll(*entityImporter.readDictionaryViewToCategories().toTypedArray())
                }
                storage.putInt(constants.DB_VERSION_KEY, config.version)
            } catch (ex: SQLiteException) {
                Log.e(this::class.java.name, "Updating the database failed!", ex)
            }
        }
    }
}
