package com.github.cheapmon.balalaika.util

import android.content.ContentValues
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.InputStreamReader

class CSV(private val res: ResourceLoader) {

    fun getVersion(): Int {
        return this.read(this.res.defaultVersionID).firstOrNull() { record ->
            record["key"] == "version"
        }?.get("value")?.toInt() ?: -1
    }

    fun getCategories(): List<ContentValues> {
        return this.read(this.res.defaultCategoriesID).map {
            ContentValues(5).apply {
                put("id", it["id"])
                put("name", it["name"])
                put("widget", it["widget"])
                put("sequence", it["sequence"])
                put("hidden", it["hidden"])
            }
        }
    }

    fun getLexemes(): List<ContentValues> {
        return this.read(this.res.defaultLexemesID).map {
            ContentValues(1).apply {
                put("id", it["id"])
            }
        }
    }

    fun getLexemeProperties(): List<ContentValues> {
        var count = 0
        val fromLexeme = this.read(this.res.defaultLexemesID).flatMap { record ->
            record.toMap().filterNot { (key, _) -> key == "id" }
                    .map { (key, value) ->
                        ContentValues(4).apply {
                            put("id", count++)
                            put("lexeme_id", record["id"])
                            put("category_id", key)
                            put("value", if (value.isEmpty()) null else value)
                        }
                    }
        }
        val fromProperty = this.read(this.res.defaultPropertiesID).map { record ->
            val value = record["value"].let { if (it.isEmpty()) null else it }
            ContentValues(4).apply {
                put("id", count++)
                put("lexeme_id", record["lexeme"])
                put("category_id", record["category"])
                put("value", value)
            }
        }
        return fromLexeme + fromProperty
    }

    fun getFullForms(): List<ContentValues> {
        return this.read(this.res.defaultFullFormsID).map {
            ContentValues(3).apply {
                put("id", it["id"])
                put("lexeme_id", it["lexeme"])
                put("full_form", it["full_form"])
            }
        }
    }

    private fun read(resourceID: Int): Iterable<CSVRecord> {
        val input = this.res.openCSV(resourceID)
        val reader = InputStreamReader(input)
        return CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader)
    }

}