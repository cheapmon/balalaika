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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.github.cheapmon.balalaika.R
import com.github.cheapmon.balalaika.databinding.HelperExampleBinding
import com.github.cheapmon.balalaika.db.entities.category.Category
import com.github.cheapmon.balalaika.db.entities.category.WidgetType
import com.github.cheapmon.balalaika.db.entities.property.PropertyWithCategory

/**
 * Widget for example sentences and full-text information
 *
 * Property values have the form `<first>;;;<second>`. The two parts can be arbitrary and will
 * be displayed on top of each other.
 *
 * @see WidgetType.EXAMPLE
 */
class ExampleWidget(
    parent: ViewGroup,
    listener: WidgetListener,
    category: Category,
    properties: List<PropertyWithCategory>,
    hasActions: Boolean,
    searchText: String?
) : PlainWidget(parent, listener, category, properties, hasActions, searchText) {
    override fun createPropertyView(
        inflater: LayoutInflater,
        contentView: ViewGroup,
        property: PropertyWithCategory
    ): View {
        val propertyBinding: HelperExampleBinding =
            DataBindingUtil.inflate(inflater, R.layout.helper_example, parent, false)
        with(propertyBinding) {
            val parts = property.property.value.split(Regex(";;;"))
            val propValue = parts.getOrNull(1)
            title = parts.first()
            if (propValue != null) value = propValue
            else helperExampleContent.visibility = View.GONE
        }
        return propertyBinding.root
    }

    // No meaningful context menu actions
    override fun createContextMenu(): AlertDialog? = null
    override val menuItems: Array<String> = arrayOf()
    override val menuActions: List<() -> Unit> = listOf()
}
