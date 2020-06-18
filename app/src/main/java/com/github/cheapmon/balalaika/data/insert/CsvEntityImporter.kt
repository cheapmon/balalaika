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
package com.github.cheapmon.balalaika.data.insert

import com.github.cheapmon.balalaika.data.entities.category.Category
import com.github.cheapmon.balalaika.data.entities.category.WidgetType
import com.github.cheapmon.balalaika.data.entities.lexeme.Lexeme
import com.github.cheapmon.balalaika.data.entities.property.Property
import com.github.cheapmon.balalaika.data.entities.view.DictionaryView
import com.github.cheapmon.balalaika.data.entities.view.DictionaryViewToCategory
import com.github.cheapmon.balalaika.data.resources.ResourceLoader
import java.io.InputStreamReader
import java.util.HashMap
import java.util.Locale
import javax.inject.Inject
import org.apache.commons.csv.CSVFormat

/** [Importer][EntityImporter] for extracting database entities from `.csv` source files */
class CsvEntityImporter @Inject constructor(
    /** Reads files from application resources */
    private val res: ResourceLoader
) : EntityImporter {
    private val categoryIdCache: HashMap<String, Long> = hashMapOf()
    private val lexemeIdCache: HashMap<String, Long> = hashMapOf()
    private val dictionaryViewIdCache: HashMap<String, Long> = hashMapOf()

    override fun readCategories(): List<Category> {
        var count = 1L
        return records(res.categoriesId).map {
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

    override fun readLexemes(): List<Lexeme> {
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

    override fun readProperties(): List<Property> {
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
                    lexemeId = lexemeIdCache[record["id"]] ?: -1,
                    value = record["value"]
                )
            }
        return (fromLexeme + fromFullForm + fromProperty)
            .filterNot { it.categoryId == -1L || it.lexemeId == -1L }
    }

    override fun readDictionaryViews(): List<DictionaryView> {
        var count = 1L
        dictionaryViewIdCache["all"] = count
        val default =
            DictionaryView(
                dictionaryViewId = count++,
                externalId = "all",
                name = "All"
            )
        return listOf(default) + records(res.dictionaryViewsId).map { record ->
            dictionaryViewIdCache[record["id"]] = count
            DictionaryView(
                dictionaryViewId = count++,
                externalId = record["id"],
                name = record["name"]
            )
        }
    }

    override fun readDictionaryViewToCategories(): List<DictionaryViewToCategory> {
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

    private fun records(id: Int) =
        CSVFormat.RFC4180.withFirstRecordAsHeader().parse(
            InputStreamReader(res.read(id))
        )
}
