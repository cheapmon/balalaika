package com.github.cheapmon.balalaika.util

import com.github.cheapmon.balalaika.db.Category
import com.github.cheapmon.balalaika.db.Lemma
import com.github.cheapmon.balalaika.db.LemmaValue
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.InputStreamReader

class CSV(private val res: ResourceLoader) {

    public fun getCategories(): Array<Category> {
        return this.read(this.res.defaultCategoriesID).map {
            Category(id = it["id"], name = it["name"], widget = it["widget"])
        }.toTypedArray()
    }

    public fun getLemmata(): Array<Lemma> {
        return this.read(this.res.defaultWordsID).map {
            Lemma(id = it["id"])
        }.toTypedArray()
    }

    public fun getLemmaValues(): Array<LemmaValue> {
        return this.read(this.res.defaultWordsID).flatMap { record ->
            record.toMap().filterNot { (key, _) -> key == "id" }
                    .map { (key, value) ->
                        LemmaValue(
                                lemmaId = record["id"],
                                categoryId = key,
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