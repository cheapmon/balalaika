package com.github.cheapmon.balalaika.util

import android.content.Context
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.InputStreamReader

class CSV(private val context: Context) {

    private fun read(resourceID: Int): Iterable<CSVRecord> {
        val input = this.context.resources.openRawResource(resourceID)
        val reader = InputStreamReader(input)
        return CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader)
    }

}