package com.github.cheapmon.balalaika.util

import android.content.Context
import android.text.SpannedString
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.entry.DictionaryEntry
import com.github.cheapmon.balalaika.data.entities.entry.GroupedEntry
import com.github.cheapmon.balalaika.data.entities.property.PropertyWithRelations

fun String.highlight(text: String?, context: Context): SpannedString {
    val contents = this
    return if (text == null || text == "" || !contents.contains(text)) {
        buildSpannedString { append(contents) }
    } else {
        val color = ContextCompat.getColor(context, R.color.colorPrimary)
        buildSpannedString {
            contents.split(Regex("(?<=$text)|(?=$text)")).forEach {
                if (it == text) color(color) { append(it) }
                else append(it)
            }
        }
    }
}

fun List<DictionaryEntry>.grouped(): GroupedEntry? {
    if (this.isEmpty()) return null
    val lexeme = this.first().lexeme
    val base = this.first().base
    val props = this.mapNotNull {
        if (it.property != null && it.category != null)
            PropertyWithRelations(it.property, it.category, it.lexeme)
        else null
    }
    return GroupedEntry(lexeme, base, props)
}