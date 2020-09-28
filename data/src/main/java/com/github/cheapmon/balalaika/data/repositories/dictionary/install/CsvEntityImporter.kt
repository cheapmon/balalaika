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
package com.github.cheapmon.balalaika.data.repositories.dictionary.install

import androidx.room.withTransaction
import com.github.cheapmon.balalaika.data.db.AppDatabase
import com.github.cheapmon.balalaika.data.db.category.CategoryEntity
import com.github.cheapmon.balalaika.data.db.category.WidgetType
import com.github.cheapmon.balalaika.data.db.config.DictionaryConfig
import com.github.cheapmon.balalaika.data.db.dictionary.DictionaryEntity
import com.github.cheapmon.balalaika.data.db.lexeme.LexemeEntity
import com.github.cheapmon.balalaika.data.db.property.PropertyEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewEntity
import com.github.cheapmon.balalaika.data.db.view.DictionaryViewToCategory
import com.github.cheapmon.balalaika.data.di.IoDispatcher
import com.github.cheapmon.balalaika.data.util.Constants
import com.github.cheapmon.balalaika.model.SimpleDictionary
import java.io.InputStreamReader
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.apache.commons.csv.CSVFormat

/** Extracting database entities from `.csv` source files */
@Singleton
internal class CsvEntityImporter @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val db: AppDatabase
) {
    /**
     * Read `.csv` files and import entities into the database
     *
     * Additional to the entities a few defaults are generated:
     * - Default category to sort by (sorts by lexeme form)
     * - Default dictionary view (shows all data categories)
     * - Default dictionary configuration consisting of the default category and dictionary view
     *
     * _Note_: Uses `withTransaction` to ensure source file validity.
     */
    suspend fun import(dictionary: SimpleDictionary, contents: DictionaryContents) =
        withContext(dispatcher) {
            db.withTransaction {
                db.dictionaries().insertAll(readDictionary(dictionary))
                db.categories().insertAll(readCategories(dictionary.id, contents))
                db.lexemes().insertAll(readLexemes(dictionary.id, contents))
                db.properties().insertAll(readProperties(dictionary.id, contents))
                db.dictionaryViews().insertViews(readDictionaryViews(dictionary.id, contents))
                db.dictionaryViews().insertRelation(
                    readDictionaryViewToCategories(dictionary.id, contents)
                )
                db.configurations().insert(generateDefaultConfiguration(dictionary.id))
            }
        }

    private fun readDictionary(dictionary: SimpleDictionary): List<DictionaryEntity> {
        return listOf(
            DictionaryEntity(
                id = dictionary.id,
                version = dictionary.version,
                name = dictionary.name,
                summary = dictionary.summary,
                authors = dictionary.authors,
                additionalInfo = dictionary.additionalInfo
            )
        )
    }

    private fun readCategories(
        dictionaryId: String,
        contents: DictionaryContents
    ): List<CategoryEntity> {
        val default = CategoryEntity(
            id = Constants.DEFAULT_CATEGORY_ID,
            dictionaryId = dictionaryId,
            name = "Default",
            widget = WidgetType.PLAIN,
            iconName = "ic_circle",
            sequence = -1,
            hidden = true,
            sortable = true
        )
        return listOf(default) + records(contents.categories).map {
            CategoryEntity(
                id = it["id"],
                name = it["name"],
                widget = WidgetType.valueOf(
                    it["widget"].toUpperCase(Locale.ROOT)
                ),
                iconName = it["icon"],
                sequence = it["sequence"].toInt(),
                hidden = it["hidden"] == "1",
                sortable = it["order_by"] == "1",
                dictionaryId = dictionaryId
            )
        }
    }

    private fun readLexemes(
        dictionaryId: String,
        contents: DictionaryContents
    ): List<LexemeEntity> {
        val fromLexeme = records(contents.lexemes).map {
            LexemeEntity(
                id = it["id"],
                dictionaryId = dictionaryId,
                form = it["form"],
                baseId = null
            )
        }
        val fromFullForm = records(contents.fullForms).map {
            LexemeEntity(
                id = it["id"],
                dictionaryId = dictionaryId,
                form = it["form"],
                baseId = it["base"]
            )
        }
        return (fromLexeme + fromFullForm)
    }

    private fun readProperties(
        dictionaryId: String,
        contents: DictionaryContents
    ): List<PropertyEntity> {
        val fromLexeme = records(contents.lexemes)
            .flatMap { record ->
                record.toMap().filter { (key, value) ->
                    key != "id" && key != "form" && value.isNotBlank()
                }.map { (id, value) ->
                    PropertyEntity(
                        categoryId = id,
                        dictionaryId = dictionaryId,
                        lexemeId = record["id"],
                        value = value
                    )
                }
            }
        val fromFullForm = records(contents.fullForms)
            .flatMap { record ->
                record.toMap().filter { (key, value) ->
                    key != "id" && key != "form" && key != "base" && value.isNotBlank()
                }.map { (id, value) ->
                    PropertyEntity(
                        categoryId = id,
                        dictionaryId = dictionaryId,
                        lexemeId = record["id"],
                        value = value
                    )
                }
            }
        val fromProperty = records(contents.properties)
            .filter { record -> record["value"].isNotBlank() }
            .map { record ->
                PropertyEntity(
                    categoryId = record["category"],
                    dictionaryId = dictionaryId,
                    lexemeId = record["id"],
                    value = record["value"]
                )
            }
        return (fromLexeme + fromFullForm + fromProperty)
    }

    private fun readDictionaryViews(
        dictionaryId: String,
        contents: DictionaryContents
    ): List<DictionaryViewEntity> {
        val default = DictionaryViewEntity(
            id = Constants.DEFAULT_DICTIONARY_VIEW_ID,
            name = "All",
            dictionaryId = dictionaryId
        )
        return listOf(default) + records(contents.views).map { record ->
            DictionaryViewEntity(
                id = record["id"],
                name = record["name"],
                dictionaryId = dictionaryId
            )
        }
    }

    private fun readDictionaryViewToCategories(
        dictionaryId: String,
        contents: DictionaryContents
    ): List<DictionaryViewToCategory> {
        val default = records(contents.categories).map { record ->
            DictionaryViewToCategory(
                id = Constants.DEFAULT_DICTIONARY_VIEW_ID,
                categoryId = record["id"],
                dictionaryId = dictionaryId
            )
        }
        return default + records(contents.views).flatMap { record ->
            record.toMap()
                .filter { (key, value) -> key != "id" && key != "name" && value != "0" }
                .map { (id, _) ->
                    DictionaryViewToCategory(
                        id = record["id"],
                        categoryId = id,
                        dictionaryId = dictionaryId
                    )
                }
        }
    }

    private fun generateDefaultConfiguration(dictionaryId: String): DictionaryConfig {
        return DictionaryConfig(
            id = dictionaryId,
            sortBy = Constants.DEFAULT_CATEGORY_ID,
            filterBy = Constants.DEFAULT_DICTIONARY_VIEW_ID
        )
    }

    private fun records(input: String) =
        CSVFormat.RFC4180.withFirstRecordAsHeader().parse(
            InputStreamReader(input.byteInputStream())
        )
}
