package com.github.cheapmon.balalaika.util

import android.content.Context
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.room.withTransaction
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.DB
import com.github.cheapmon.balalaika.data.entities.*
import org.apache.commons.csv.CSVFormat
import org.yaml.snakeyaml.Yaml
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

class ImportUtil<T>(private val res: ResourceLoader<T>) {
    private val categoryIdCache: HashMap<String, Long> = hashMapOf()
    private val lexemeIdCache: HashMap<String, Long> = hashMapOf()
    private val dictionaryViewIdCache: HashMap<String, Long> = hashMapOf()

    suspend fun import(context: Context) {
        val config = readConfig()
        val db = DB.getInstance(context.applicationContext)
        val preferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val currentVersion = preferences.getInt(DB_VERSION_KEY, 0)
        if (config.dbVersion > currentVersion) {
            try {
                db.withTransaction {
                    db.clearAllTables()
                    readCategories().forEach { db.categories().insertAll(it) }
                    readLexemes().forEach { db.lexemes().insertAll(it) }
                    readProperties().forEach { db.properties().insertAll(it) }
                    readDictionaryViews().forEach { db.dictionaryViews().insertAll(it) }
                    readDictionaryViewToCategories().forEach { db.dictionaryViews().insertAll(it) }
                }
                preferences.edit().putInt(DB_VERSION_KEY, config.dbVersion).apply()
            } catch (ex: SQLiteException) {
                Log.e(this::class.java.name, "Updating the database failed!", ex)
            }
        }
    }

    private fun readCategories(): List<Category> {
        var count = 1L
        return records(res.categoriesId).map {
            categoryIdCache[it["id"]] = count
            Category(
                categoryId = count++,
                externalId = it["id"],
                name = it["name"],
                widget = WidgetType.valueOf(it["widget"].toUpperCase(Locale.ROOT)),
                sequence = it["sequence"].toInt(),
                hidden = it["hidden"] == "1",
                orderBy = it["order_by"] == "1"
            )
        }
    }

    private fun readLexemes(): List<Lexeme> {
        var count = 1L
        val fromLexeme = records(res.lexemesId).map {
            lexemeIdCache[it["id"]] = count
            Lexeme(
                lexemeId = count++,
                externalId = it["id"],
                form = it["form"],
                baseId = null
            )
        }
        val fromFullForm = records(res.fullFormsId).map {
            lexemeIdCache[it["id"]] = count
            Lexeme(
                lexemeId = count++,
                externalId = it["id"],
                form = it["form"],
                baseId = lexemeIdCache[it["base"]]
            )
        }
        return (fromLexeme + fromFullForm)
    }

    private fun readProperties(): List<Property> {
        val fromLexeme = records(res.lexemesId)
            .flatMap { record ->
                record.toMap().filter { (key, value) ->
                    key != "id" && key != "form" && value.isNotBlank()
                }.map { (id, value) ->
                    Property(
                        categoryId = categoryIdCache[id] ?: -1,
                        lexemeId = lexemeIdCache[record["id"]] ?: -1,
                        value = value
                    )
                }
            }
        val fromFullForm = records(res.fullFormsId)
            .flatMap { record ->
                record.toMap().filter { (key, value) ->
                    key != "id" && key != "form" && key != "base" && value.isNotBlank()
                }.map { (id, value) ->
                    Property(
                        categoryId = categoryIdCache[id] ?: -1,
                        lexemeId = lexemeIdCache[record["id"]] ?: -1,
                        value = value
                    )
                }
            }
        val fromProperty = records(res.propertiesId)
            .filter { record -> record["value"].isNotBlank() }
            .map { record ->
                Property(
                    categoryId = categoryIdCache[record["category"]] ?: -1,
                    lexemeId = lexemeIdCache[record["lexeme"]] ?: -1,
                    value = record["value"]
                )
            }
        return (fromLexeme + fromFullForm + fromProperty)
            .filterNot { it.categoryId == -1L || it.lexemeId == -1L }
    }

    private fun readDictionaryViews(): List<DictionaryView> {
        var count = 1L
        dictionaryViewIdCache["all"] = count
        val default = DictionaryView(dictionaryViewId = count++, externalId = "all", name = "All")
        return listOf(default) + records(res.dictionaryViewsId).map { record ->
            dictionaryViewIdCache[record["id"]] = count
            DictionaryView(
                dictionaryViewId = count++,
                externalId = record["id"],
                name = record["name"]
            )
        }
    }

    private fun readDictionaryViewToCategories(): List<DictionaryViewToCategory> {
        val default = records(res.categoriesId).map { record ->
            DictionaryViewToCategory(
                dictionaryViewId = 1,
                categoryId = categoryIdCache[record["id"]] ?: -1
            )
        }
        return default + records(res.dictionaryViewsId).flatMap { record ->
            record.toMap()
                .filter { (key, value) -> key != "id" && key != "name" && value != "0" }
                .map { (id, _) ->
                    DictionaryViewToCategory(
                        dictionaryViewId = dictionaryViewIdCache[record["id"]] ?: -1,
                        categoryId = categoryIdCache[id] ?: -1
                    )
                }
        }.filterNot { it.dictionaryViewId == -1L || it.categoryId == -1L }
    }

    private fun readConfig(): Config {
        val config: Map<String, Int> = Yaml().load(res.read(res.configId))
        return Config(config[YAML_VERSION_KEY] ?: 0)
    }

    private fun records(id: T) =
        CSVFormat.RFC4180.withFirstRecordAsHeader().parse(InputStreamReader(res.read(id)))

    companion object {
        const val DB_VERSION_KEY = "db_version"
        const val YAML_VERSION_KEY = "version"
    }
}

interface ResourceLoader<T> {
    val categoriesId: T
    val lexemesId: T
    val fullFormsId: T
    val propertiesId: T
    val dictionaryViewsId: T
    val configId: T

    fun read(id: T): InputStream
}

class AndroidResourceLoader(private val context: Context) : ResourceLoader<Int> {
    override val categoriesId: Int = R.raw.categories
    override val lexemesId: Int = R.raw.lexemes
    override val fullFormsId: Int = R.raw.full_forms
    override val propertiesId: Int = R.raw.properties
    override val dictionaryViewsId: Int = R.raw.views
    override val configId: Int = R.raw.config

    override fun read(id: Int): InputStream = context.resources.openRawResource(id)
}

data class Config(
    val dbVersion: Int
)
