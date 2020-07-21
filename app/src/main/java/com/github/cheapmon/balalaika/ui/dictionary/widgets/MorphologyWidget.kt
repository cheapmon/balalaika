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
import com.github.cheapmon.balalaika.db.entities.category.Category
import com.github.cheapmon.balalaika.db.entities.category.WidgetType
import com.github.cheapmon.balalaika.db.entities.property.PropertyWithCategory

/**
 * Widget for morphological information
 *
 * Property values are divided by `|` symbols. Each morphological part is displayed
 * separately in the context menu.
 *
 * @see WidgetType.MORPHOLOGY
 */
class MorphologyWidget(
    parent: ViewGroup,
    listener: WidgetListener,
    category: Category,
    properties: List<PropertyWithCategory>,
    hasActions: Boolean,
    searchText: String?
) : BaseWidget(parent, listener, category, properties, hasActions, searchText) {
    override val menuItems: Array<String> = properties.flatMap {
        displayValue(it.property.value).split(Regex("\\|"))
    }.toTypedArray()
}
