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
package com.github.cheapmon.balalaika.ui.dictionary.widgets

import android.view.ViewGroup
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.db.entities.category.Category
import com.github.cheapmon.balalaika.db.entities.category.WidgetType
import com.github.cheapmon.balalaika.db.entities.property.PropertyWithCategory

/**
 * Widget for external Wordnet information
 *
 * Property values are of the form `<description>;;;<url>`. The description can be arbitrary,
 * `<url>` must be a valid Wordnet lemma hyperlink.
 *
 * @see WidgetType.URL
 */
class WordnetWidget(
    parent: ViewGroup,
    listener: WidgetListener,
    category: Category,
    properties: List<PropertyWithCategory>,
    hasActions: Boolean,
    searchText: String?
) : BaseWidget(parent, listener, category, properties, hasActions, searchText) {
    override fun displayValue(value: String): String {
        return value.split(Regex(";;;")).firstOrNull() ?: ""
    }

    override fun actionIcon(value: String): Int? {
        value.split(Regex(";;;")).getOrNull(1) ?: return null
        return R.drawable.ic_present
    }

    override fun onClickActionButtonListener(value: String) {
        val parts = value.split(Regex(";;;"))
        val word = parts.getOrNull(0) ?: return
        val url = parts.getOrNull(1) ?: return
        listener.onClickWordnetButton(
            word,
            url
        )
    }
}