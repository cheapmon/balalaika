package com.github.cheapmon.balalaika.util

import com.github.cheapmon.balalaika.db.Category
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.InputStreamReader

class CSV(private val res: ResourceLoader) {

    public fun getCategories(): Array<Category> {
        return this.read(this.res.defaultCategoriesID).map {
            Category(id = 0, externalId = it["id"], name = it["name"], widget = it["widget"])
        }.toTypedArray()
    }

    private fun read(resourceID: Int): Iterable<CSVRecord> {
        val input = this.res.open(resourceID)
        val reader = InputStreamReader(input)
        return CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader)
    }

}