package com.github.cheapmon.balalaika.data.insert

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.room.withTransaction
import com.github.cheapmon.balalaika.data.AppDatabase
import com.github.cheapmon.balalaika.data.config.ConfigLoader
import com.github.cheapmon.balalaika.data.storage.Storage
import com.github.cheapmon.balalaika.util.Constants
import javax.inject.Inject

class ImportUtil @Inject constructor(
    private val entityImporter: EntityImporter,
    private val configLoader: ConfigLoader,
    private val storage: Storage,
    private val constants: Constants,
    private val appDatabase: AppDatabase
) {
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
