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
package com.github.cheapmon.balalaika.data.selection

import androidx.room.withTransaction
import com.github.cheapmon.balalaika.db.AppDatabase
import com.github.cheapmon.balalaika.db.entities.category.Category
import com.github.cheapmon.balalaika.db.entities.category.WidgetType
import com.github.cheapmon.balalaika.db.entities.config.DictionaryConfig
import com.github.cheapmon.balalaika.db.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.db.entities.property.Property
import com.github.cheapmon.balalaika.db.entities.view.DictionaryView
import com.github.cheapmon.balalaika.db.entities.view.DictionaryViewToCategory
import com.github.cheapmon.balalaika.util.Constants
import java.io.InputStreamReader
import java.util.Locale
import javax.inject.Inject
import org.apache.commons.csv.CSVFormat

/** Extracting database entities from `.csv` source files */
class CsvEntityImporter @Inject constructor(
    private val db: AppDatabase,
    private val constants: Constants
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
    suspend fun import(dictionaryId: String, contents: DictionaryContents) {
        db.withTransaction {
            db.categories().insertAll(readCategories(dictionaryId, contents))
            db.lexemes().insertAll(readLexemes(dictionaryId, contents))
            db.properties().insertAll(readProperties(dictionaryId, contents))
            db.dictionaryViews().insertViews(readDictionaryViews(dictionaryId, contents))
            db.dictionaryViews().insertRelation(
                readDictionaryViewToCategories(dictionaryId, contents)
            )
            db.configurations().insert(generateDefaultConfiguration(dictionaryId))
        }
    }

    private fun readCategories(dictionaryId: String, contents: DictionaryContents): List<Category> {
        val default = Category(
            id = constants.DEFAULT_CATEGORY_ID,
            dictionaryId = dictionaryId,
            name = "Default",
            widget = WidgetType.PLAIN,
            iconName = "ic_circle",
            sequence = -1,
            hidden = true,
            orderBy = true
        )
        return listOf(default) + records(contents.categories).map {
            Category(
                id = it["id"],
                name = it["name"],
                widget = WidgetType.valueOf(
                    it["widget"].toUpperCase(Locale.ROOT)
                ),
                iconName = it["icon"],
                sequence = it["sequence"].toInt(),
                hidden = it["hidden"] == "1",
                orderBy = it["order_by"] == "1",
                dictionaryId = dictionaryId
            )
        }
    }

    private fun readLexemes(dictionaryId: String, contents: DictionaryContents): List<Lexeme> {
        val fromLexeme = records(contents.lexemes).map {
            Lexeme(
                id = it["id"],
                dictionaryId = dictionaryId,
                form = it["form"],
                baseId = null
            )
        }
        val fromFullForm = records(contents.fullForms).map {
            Lexeme(
                id = it["id"],
                dictionaryId = dictionaryId,
                form = it["form"],
                baseId = it["base"]
            )
        }
        return (fromLexeme + fromFullForm)
    }

    private fun readProperties(dictionaryId: String, contents: DictionaryContents): List<Property> {
        val fromLexeme = records(contents.lexemes)
            .flatMap { record ->
                record.toMap().filter { (key, value) ->
                    key != "id" && key != "form" && value.isNotBlank()
                }.map { (id, value) ->
                    Property(
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
                    Property(
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
                Property(
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
    ): List<DictionaryView> {
        val default = DictionaryView(
            id = constants.DEFAULT_DICTIONARY_VIEW_ID,
            name = "All",
            dictionaryId = dictionaryId
        )
        return listOf(default) + records(contents.views).map { record ->
            DictionaryView(id = record["id"], name = record["name"], dictionaryId = dictionaryId)
        }
    }

    private fun readDictionaryViewToCategories(
        dictionaryId: String,
        contents: DictionaryContents
    ): List<DictionaryViewToCategory> {
        val default = records(contents.categories).map { record ->
            DictionaryViewToCategory(
                dictionaryViewId = constants.DEFAULT_DICTIONARY_VIEW_ID,
                categoryId = record["id"],
                dictionaryId = dictionaryId
            )
        }
        return default + records(contents.views).flatMap { record ->
            record.toMap()
                .filter { (key, value) -> key != "id" && key != "name" && value != "0" }
                .map { (id, _) ->
                    DictionaryViewToCategory(
                        dictionaryViewId = record["id"],
                        categoryId = id,
                        dictionaryId = dictionaryId
                    )
                }
        }
    }

    private fun generateDefaultConfiguration(dictionaryId: String): DictionaryConfig {
        return DictionaryConfig(
            id = dictionaryId,
            orderBy = constants.DEFAULT_CATEGORY_ID,
            filterBy = constants.DEFAULT_DICTIONARY_VIEW_ID
        )
    }

    private fun records(input: String) =
        CSVFormat.RFC4180.withFirstRecordAsHeader().parse(
            InputStreamReader(input.byteInputStream())
        )
}
