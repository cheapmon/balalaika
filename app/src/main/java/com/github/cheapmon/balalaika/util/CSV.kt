package com.github.cheapmon.balalaika.util

import com.github.cheapmon.balalaika.db.Category
import com.github.cheapmon.balalaika.db.Word
import com.github.cheapmon.balalaika.db.WordInfo
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.InputStreamReader

class CSV(private val res: ResourceLoader) {

    public fun getCategories(): Array<Category> {
        return this.read(this.res.defaultCategoriesID).map {
            Category(id = 0, externalId = it["id"], name = it["name"], widget = it["widget"])
        }.toTypedArray()
    }

    public fun getWords(): Array<Word> {
        return this.read(this.res.defaultWordsID).map {
            Word(id = 0, externalId = it["id"])
        }.toTypedArray()
    }

    public fun getWordInfos(words: List<Word>, categories: List<Category>): Array<WordInfo> {
        return this.read(this.res.defaultWordsID).flatMap { record ->
            val wordsMap = hashMapOf(*words.map { it.externalId to it.id }.toTypedArray())
            val categoriesMap = hashMapOf(*categories.map { it.externalId to it.id }.toTypedArray())
            record.toMap().filterNot { (key, _) -> key == "id" }
                    .map { (key, value) ->
                        WordInfo(
                                id = 0,
                                wordId = wordsMap[record["id"]] ?: 0,
                                categoryId = categoriesMap[key] ?: 0,
                                value = value
                        )
                    }
        }.toTypedArray()
    }

    private fun read(resourceID: Int): Iterable<CSVRecord> {
        val input = this.res.openCSV(resourceID)
        val reader = InputStreamReader(input)
        return CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader)
    }

}