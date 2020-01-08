package com.github.cheapmon.balalaika.util

import com.github.cheapmon.balalaika.db.Category
import com.github.cheapmon.balalaika.db.Lexeme
import com.github.cheapmon.balalaika.db.LexemeProperty
import com.github.cheapmon.balalaika.db.FullForm
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.InputStreamReader

class CSV(private val res: ResourceLoader) {

    public fun getVersion(): Int {
        return this.read(this.res.defaultVersionID).firstOrNull() { record ->
            record["key"] == "version"
        }?.get("value")?.toInt() ?: -1
    }

    public fun getCategories(): Array<Category> {
        return this.read(this.res.defaultCategoriesID).map {
            Category(id = it["id"], name = it["name"], widget = it["widget"])
        }.toTypedArray()
    }

    public fun getLexemes(): Array<Lexeme> {
        return this.read(this.res.defaultLexemesID).map {
            Lexeme(id = it["id"])
        }.toTypedArray()
    }

    public fun getLexemeProperties(): Array<LexemeProperty> {
        val fromLemma = this.read(this.res.defaultLexemesID).flatMap { record ->
            record.toMap().filterNot { (key, _) -> key == "id" }
                    .map { (key, value) ->
                        LexemeProperty(
                                id = 0,
                                lexemeId = record["id"],
                                categoryId = key,
                                value = if(value.isEmpty()) null else value
                        )
                    }
        }
        val fromProperty = this.read(this.res.defaultPropertiesID).map { record ->
            val value = record["value"].let { if(it.isEmpty()) null else it }
            LexemeProperty(
                    id = 0,
                    lexemeId = record["lexeme"],
                    categoryId = record["category"],
                    value = value
            )
        }
        return (fromLemma + fromProperty).toTypedArray()
    }

    public fun getFullForms(): Array<FullForm> {
        return this.read(this.res.defaultFullFormsID).map {
            FullForm(id = 0, lexemeId = it["lexeme"], fullForm = it["full_form"])
        }.toTypedArray()
    }

    private fun read(resourceID: Int): Iterable<CSVRecord> {
        val input = this.res.openCSV(resourceID)
        val reader = InputStreamReader(input)
        return CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader)
    }

}