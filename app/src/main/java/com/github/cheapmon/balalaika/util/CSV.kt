package com.github.cheapmon.balalaika.util

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.InputStreamReader

typealias Name = String
typealias Widget = String

class CSV(private val res: ResourceLoader) {

    public fun getCategories(): Array<Pair<Name, Widget>> {
        return this.read(this.res.defaultCategoriesID).map {
            it["name"] to it["widget"]
        }.toTypedArray()
    }

    private fun read(resourceID: Int): Iterable<CSVRecord> {
        val input = this.res.open(resourceID)
        val reader = InputStreamReader(input)
        return CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader)
    }

}