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
import com.github.cheapmon.balalaika.model.DataCategory
import com.github.cheapmon.balalaika.model.Property

/**
 * Widget for external Wordnet information
 *
 * @see Property.Wordnet
 */
class WordnetWidget(
    parent: ViewGroup,
    category: DataCategory,
    properties: List<Property.Wordnet>,
    hasActions: Boolean,
    menuListener: WidgetMenuListener,
    actionListener: WidgetActionListener<Property.Wordnet>
) : Widget<Property.Wordnet>(
    parent,
    category,
    properties,
    hasActions,
    menuListener,
    actionListener
) {
    override fun displayName(property: Property.Wordnet): String =
        property.name

    override fun actionIcon(property: Property.Wordnet): Int? =
        R.drawable.ic_present
}
