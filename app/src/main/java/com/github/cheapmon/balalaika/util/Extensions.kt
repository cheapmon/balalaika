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
package com.github.cheapmon.balalaika.util

import android.content.Context
import android.text.SpannedString
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.data.entities.entry.PropertyDatabaseView
import com.github.cheapmon.balalaika.ui.search.SearchAdapter

/**
 * Highlight part of a string
 *
 * This is used in the user interface to indicate matching [dictionary entries][PropertyDatabaseView]
 * for a search query.
 *
 * @see SearchAdapter
 */
fun String.highlight(text: String?, context: Context): SpannedString {
    val contents = this
    return if (text == null || text == "" || !contents.contains(text)) {
        buildSpannedString { append(contents) }
    } else {
        val color = ContextCompat.getColor(context, R.color.colorPrimary)
        buildSpannedString {
            // Split before and after text
            contents.split(Regex("(?<=$text)|(?=$text)")).forEach {
                if (it == text) color(color) { append(it) }
                else append(it)
            }
        }
    }
}
