package com.github.cheapmon.balalaika.util

import android.content.Context
import android.graphics.Color
import android.text.SpannedString
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import com.github.cheapmon.balalaika.R

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