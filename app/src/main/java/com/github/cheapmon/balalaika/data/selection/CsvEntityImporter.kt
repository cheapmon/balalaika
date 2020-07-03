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
import com.github.cheapmon.balalaika.core.Response
import com.github.cheapmon.balalaika.db.AppDatabase
import com.github.cheapmon.balalaika.db.entities.category.Category
import com.github.cheapmon.balalaika.db.entities.category.WidgetType
import com.github.cheapmon.balalaika.db.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.db.entities.property.Property
import com.github.cheapmon.balalaika.db.entities.view.DictionaryView
import com.github.cheapmon.balalaika.db.entities.view.DictionaryViewToCategory
import java.io.InputStream
import java.io.InputStreamReader
import java.util.HashMap
import java.util.Locale
import javax.inject.Inject
import org.apache.commons.csv.CSVFormat

/** Extracting database entities from `.csv` source files */
class CsvEntityImporter @Inject constructor(
    private val db: AppDatabase
) {
    private val categoryIdCache: HashMap<String, Long> = hashMapOf()
    private val lexemeIdCache: HashMap<String, Long> = hashMapOf()
    private val dictionaryViewIdCache: HashMap<String, Long> = hashMapOf()

    suspend fun import(contents: DictionaryContents): Response<Unit> {
        db.withTransaction {
            db.categories().insertAll(readCategories(contents))
            db.lexemes().insertAll(readLexemes(contents))
            db.properties().insertAll(readProperties(contents))
            db.dictionaryViews().insertViews(readDictionaryViews(contents))
            db.dictionaryViews().insertRelation(readDictionaryViewToCategories(contents))
        }
        return Response.Success(Unit)
    }

    private fun readCategories(contents: DictionaryContents): List<Category> {
        var count = 1L
        return records(contents.categories).map {
            categoryIdCache[it["id"]] = count
            Category(
                categoryId = count++,
                externalId = it["id"],
                name = it["name"],
                widget = WidgetType.valueOf(
                    it["widget"].toUpperCase(Locale.ROOT)
                ),
                iconId = it["icon"],
                sequence = it["sequence"].toInt(),
                hidden = it["hidden"] == "1",
                orderBy = it["order_by"] == "1"
            )
        }
    }

    private fun readLexemes(contents: DictionaryContents): List<Lexeme> {
        var count = 1L
        val fromLexeme = records(contents.lexemes).map {
            lexemeIdCache[it["id"]] = count
            Lexeme(
                lexemeId = count++,
                externalId = it["id"],
                form = it["form"],
                baseId = null
            )
        }
        val fromFullForm = records(contents.fullForms).map {
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

    private fun readProperties(contents: DictionaryContents): List<Property> {
        val fromLexeme = records(contents.lexemes)
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
        val fromFullForm = records(contents.fullForms)
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
        val fromProperty = records(contents.properties)
            .filter { record -> record["value"].isNotBlank() }
            .map { record ->
                Property(
                    categoryId = categoryIdCache[record["category"]] ?: -1,
                    lexemeId = lexemeIdCache[record["id"]] ?: -1,
                    value = record["value"]
                )
            }
        return (fromLexeme + fromFullForm + fromProperty)
            .filterNot { it.categoryId == -1L || it.lexemeId == -1L }
    }

    private fun readDictionaryViews(contents: DictionaryContents): List<DictionaryView> {
        var count = 1L
        dictionaryViewIdCache["all"] = count
        val default =
            DictionaryView(
                dictionaryViewId = count++,
                externalId = "all",
                name = "All"
            )
        return listOf(default) + records(contents.views).map { record ->
            dictionaryViewIdCache[record["id"]] = count
            DictionaryView(
                dictionaryViewId = count++,
                externalId = record["id"],
                name = record["name"]
            )
        }
    }

    private fun readDictionaryViewToCategories(
        contents: DictionaryContents
    ): List<DictionaryViewToCategory> {
        val default = records(contents.categories).map { record ->
            DictionaryViewToCategory(
                dictionaryViewId = 1,
                categoryId = categoryIdCache[record["id"]] ?: -1
            )
        }
        return default + records(contents.views).flatMap { record ->
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

    private fun records(input: InputStream) =
        CSVFormat.RFC4180.withFirstRecordAsHeader().parse(
            InputStreamReader(input)
        )
}
